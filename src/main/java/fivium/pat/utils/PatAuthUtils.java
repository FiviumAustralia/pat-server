package fivium.pat.utils;

import static fivium.pat.utils.Constants.CLINICIAN_PORTAL_LOGIN_CONTEXT;
import static fivium.pat.utils.Constants.MOBILE_APP_LOGIN_CONTEXT;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mindrot.jbcrypt.BCrypt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class PatAuthUtils {

	private static final String JWT_CONTEXT_CLAIM_KEY = "login_context";
	private static final String JWT_ROLE_CLAIM_KEY = "role";

	private static final String PATIENT_ROLE = "patient";
	private static final String CLINICIAN_ROLE = "user";
	private static final String SUPERUSER_ROLE = "superuser";
	
	private static final String[] VALID_CLINICIAN_ROLES = new String[]{CLINICIAN_ROLE, SUPERUSER_ROLE};
	
	private static final String AUTHENTICATE_PATIENT_PREPARED_SQL_QUERY = "SELECT p_id, company FROM patient WHERE p_id = ? AND Active = \'Not Active\'";
	private static final String GET_TERMS_AND_CONDITIONS_SQL_QUERY = "SELECT Terms_and_Conditions, Permissions FROM company WHERE Company_Name = ?";
	private static final String AUTHENTICATE_PATIENT_PREPARED_SQL_QUERY_UPDATE_TOKEN_SQL_QUERY = "UPDATE patient SET Active= \'Active\', Token = ? where p_id= ?;";

	private static final String AUTHENTICATE_CLINICIAN_PREPARED_SQL_QUERY = "SELECT Firstname, Lastname, Password, Role FROM clinicians WHERE Email = ?";
	private static final String AUTHENTICATE_CLINICIAN_PREPARED_SQL_QUERY_UPDATE_TOKEN = "UPDATE clinicians SET Token=? where Email= ?;";

	private static final String VERIFY_PATIENT_JWT_TOKEN_SQL_QUERY = "SELECT p_id, company FROM patient WHERE p_id = ? AND Active = \'Active\'";
	private static final String VERIFY_CLINICIAN_JWT_TOKEN_SQL_QUERY = "SELECT Role FROM clinicians WHERE Email = ?";
	private static final String VERIFY_SUPERUSER_JWT_TOKEN_SQL_QUERY = "SELECT Role FROM clinicians WHERE Email = ? AND Role = \'superuser\'";

	private static final String PATIENTS_URL_PATTERN = "";
	private static final String CLINICIANS_URL_PATTERN = "";
	private static final String SUPERUSER_URL_PATTERN = "";

	private static Log logger = LogFactory.getLog(PatAuthUtils.class);

	public static boolean isValidJWT(String jwt, String requestUrlPattern) {

		// first check if jwt is blank
		if (StringUtils.isBlank(jwt)) {
			logger.warn("attempted to validate blank jwt");
			return false;
		}

		// verify the jwt was issued from this server
		Claims jwtClaims = Jwts.parser().setSigningKey(Constants.JWT_KEY).parseClaimsJws(jwt).getBody();

		// verify jwt
		if (PATIENTS_URL_PATTERN.equals(requestUrlPattern)) {
			return verifyPatientJwt(jwtClaims);
		} else if (CLINICIANS_URL_PATTERN.equals(requestUrlPattern)) {
			return verifyClinicianJwt(jwtClaims);
		} else if (SUPERUSER_URL_PATTERN.equals(requestUrlPattern)) {
			return verifySuperuserJwt(jwtClaims);
		} else {
			logger.warn("Attempted to validate JWT for unexpected URL pattern: " + requestUrlPattern);
			return false;
		}

	}

	public static Map<String, String> loginPatient(String requestPatientId) {

		logger.trace("Entering loginPatient...");

		// Initialise result of this method
		Map<String, String> patientLoginResultMap = new HashMap<String, String>();

		try {

			// Call auth patient query
			Collection<Map<String, String>> authenticatePatientResult = PAT_DAO
					.executeStatement(AUTHENTICATE_PATIENT_PREPARED_SQL_QUERY, new Object[] { requestPatientId });
			if (authenticatePatientResult.isEmpty()) {
				throw new Exception("Empty result when executing AUTHENTICATE_PATIENT_PREPARED_SQL_QUERY");
			}

			// Extract company from auth query result
			String company = authenticatePatientResult.iterator().next().get("company");

			// Create JWT token for the patient
			String token = issueJwtToken(requestPatientId, MOBILE_APP_LOGIN_CONTEXT, PATIENT_ROLE);

			// Fetch terms and conditions for the patient's company
			Collection<Map<String, String>> termsAndConditionsResult = PAT_DAO
					.executeStatement(GET_TERMS_AND_CONDITIONS_SQL_QUERY, new Object[] { company });
			Map<String, String> terms = termsAndConditionsResult.iterator().next();

			// Update database with patient's token
			PAT_DAO.executeStatement(AUTHENTICATE_PATIENT_PREPARED_SQL_QUERY_UPDATE_TOKEN_SQL_QUERY,
					new Object[] { token, requestPatientId });

			// Populate the result
			patientLoginResultMap.put("jwt_token", token);
			patientLoginResultMap.put("terms", terms.get("Terms_and_Conditions"));
			patientLoginResultMap.put("permissions", terms.get("Permissions"));
			patientLoginResultMap.put("company", company);
		} catch (Exception e) {
			logger.error("Exception occurred trying to authenticate p_id", e);
			patientLoginResultMap.put("error", "Supplied user id doesn't exist, please contact your clinician");
		}

		return patientLoginResultMap;

	}

	public static Map<String, String> loginClinician(String requestClinicianId, String requestClinicianPassword) {

		logger.trace("Entering loginClinician...");

		// Initialise result of this method
		Map<String, String> clinicianLoginResultMap = new HashMap<String, String>();

		try {

			// call auth clinician query
			Collection<Map<String, String>> authenticateClinicianResult = PAT_DAO.executeStatement(
					AUTHENTICATE_CLINICIAN_PREPARED_SQL_QUERY,
					new Object[] { requestClinicianId, requestClinicianPassword });
			if (authenticateClinicianResult.isEmpty()) {
				throw new Exception("Empty result when executing AUTHENTICATE_CLINICIAN_PREPARED_SQL_QUERY");
			}

			// Extract the value map (row 0) from the auth clinician query
			// result
			Map<String, String> authenticateClinicianResultValues = authenticateClinicianResult.iterator().next();

			// Verify the request password matches the returned password
			if (!BCrypt.checkpw(requestClinicianPassword, authenticateClinicianResultValues.get("Password"))) {
				clinicianLoginResultMap.put("jwt_token", "Invalid Credentials");
				return clinicianLoginResultMap;
			}

			// Create a JWT token for the clinician
			String token = issueJwtToken(requestClinicianId, CLINICIAN_PORTAL_LOGIN_CONTEXT, authenticateClinicianResultValues.get("Role"));

			// Update the database with the clinician's JWT token
			PAT_DAO.executeStatement(AUTHENTICATE_CLINICIAN_PREPARED_SQL_QUERY_UPDATE_TOKEN,
					new Object[] { token, requestClinicianId });

			// Populate the result
			clinicianLoginResultMap.put("Firstname", authenticateClinicianResultValues.get("Firstname"));
			clinicianLoginResultMap.put("Lastname", authenticateClinicianResultValues.get("Lastname"));
			clinicianLoginResultMap.put("Role", authenticateClinicianResultValues.get("Role"));
			clinicianLoginResultMap.put("jwt_token", token);

		} catch (Exception e) {
			logger.error("unexpected error occured", e);
			clinicianLoginResultMap.put("jwt_token", "Invalid Credentials");
		}

		return clinicianLoginResultMap;

	}

	private static boolean verifyPatientJwt(Claims jwtClaims) {

		try {

			// verify jwt was created using patient login
			if (!jwtClaims.get(JWT_CONTEXT_CLAIM_KEY).equals(MOBILE_APP_LOGIN_CONTEXT)) {
				throw new Exception("Supplied JWT token contains invalid login_context claim!"
						+ " Expected: " + MOBILE_APP_LOGIN_CONTEXT + " but contained: " + jwtClaims.get(JWT_CONTEXT_CLAIM_KEY));
			}
			
			// verify jwt has patient role
			if (!jwtClaims.get(JWT_ROLE_CLAIM_KEY).equals(PATIENT_ROLE)) {
				throw new Exception("Supplied JWT token contains invalid role claim!"
						+ " Expected: " + PATIENT_ROLE + " but contained: " + jwtClaims.get(JWT_ROLE_CLAIM_KEY));
			}
			
			String patientId = jwtClaims.getSubject();

			// Call auth patient query
			Collection<Map<String, String>> verifyPatientResult = PAT_DAO
					.executeStatement(VERIFY_PATIENT_JWT_TOKEN_SQL_QUERY, new Object[] { patientId });
			if (verifyPatientResult.isEmpty()) {
				throw new Exception("Empty result when executing VERIFY_PATIENT_JWT_TOKEN_SQL_QUERY");
			}

			return true;

		} catch (Exception e) {
			logger.error("Exception occurred trying to verify patient jwt", e);
			return false;
		}
	}

	private static boolean verifyClinicianJwt(Claims jwtClaims) {

		try {
			
			// verify jwt was created using clinician login
			if (!jwtClaims.get(JWT_CONTEXT_CLAIM_KEY).equals(CLINICIAN_PORTAL_LOGIN_CONTEXT)) {
				throw new Exception("Supplied JWT token contains invalid login_context claim!"
						+ " Expected: " + CLINICIAN_PORTAL_LOGIN_CONTEXT + " but contained: " + jwtClaims.get(JWT_CONTEXT_CLAIM_KEY));
			}
			
			// verify jwt has valid clinician role
			if (!Arrays.asList(VALID_CLINICIAN_ROLES).contains(jwtClaims.get(JWT_ROLE_CLAIM_KEY)) ) {
				throw new Exception("Supplied JWT token contains invalid role claim!"
						+ " Expected one of: " + Arrays.toString(VALID_CLINICIAN_ROLES) + " but contained: " + jwtClaims.get(JWT_ROLE_CLAIM_KEY));
			}

			String clinicianId = jwtClaims.getSubject();

			// Call auth patient query
			Collection<Map<String, String>> authenticatePatientResult = PAT_DAO
					.executeStatement(VERIFY_CLINICIAN_JWT_TOKEN_SQL_QUERY, new Object[] { clinicianId });
			if (authenticatePatientResult.isEmpty()) {
				throw new Exception("Empty result when executing VERIFY_CLINICIAN_JWT_TOKEN_SQL_QUERY");
			}

			return true;

		} catch (Exception e) {
			logger.error("Exception occurred trying to verify clinician jwt", e);
			return false;
		}
	}

	private static boolean verifySuperuserJwt(Claims jwtClaims) {

		try {

			// verify jwt was created using clinician login
			if (!jwtClaims.get(JWT_CONTEXT_CLAIM_KEY).equals(CLINICIAN_PORTAL_LOGIN_CONTEXT)) {
				throw new Exception("Supplied JWT token contains invalid login_context claim!"
						+ " Expected: " + CLINICIAN_PORTAL_LOGIN_CONTEXT + " but contained: " + jwtClaims.get(JWT_CONTEXT_CLAIM_KEY));
			}
			
			// verify jwt has superuser role
			if (!jwtClaims.get(JWT_ROLE_CLAIM_KEY).equals(SUPERUSER_ROLE)) {
				throw new Exception("Supplied JWT token contains invalid role claim!"
						+ " Expected: " + SUPERUSER_ROLE + " but contained: " + jwtClaims.get(JWT_ROLE_CLAIM_KEY));
			}
			
			String superuserId = jwtClaims.getSubject();

			// Call auth patient query
			Collection<Map<String, String>> authenticatePatientResult = PAT_DAO
					.executeStatement(VERIFY_SUPERUSER_JWT_TOKEN_SQL_QUERY, new Object[] { superuserId });
			if (authenticatePatientResult.isEmpty()) {
				throw new Exception("Empty result when executing VERIFY_SUPERUSER_JWT_TOKEN_SQL_QUERY");
			}

			return true;

		} catch (Exception e) {
			logger.error("Exception occurred trying to verify superuser jwt", e);
			return false;
		}
	}

	private static String issueJwtToken(String id, String context, String role) {
		String token = Jwts.builder()
				.setSubject(id)
				.claim(JWT_CONTEXT_CLAIM_KEY, context)
				.claim(JWT_ROLE_CLAIM_KEY, role)
				.signWith(SignatureAlgorithm.HS512, Constants.JWT_KEY).compact();
		return token;
	}


}
