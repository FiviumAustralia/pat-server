package fivium.pat.servlets;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mindrot.jbcrypt.BCrypt;

import com.google.gson.Gson;

import fivium.pat.utils.Constants;
import fivium.pat.utils.LegacyInternalServerUtils;
import fivium.pat.utils.PAT_DAO;
import fivium.pat.utils.RnsUtils;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class LoginServlet extends HttpServlet {

	private static final String ID_PARAM = "id";
	private static final String PWORD_PARAM = "pwd";
	private static final String LOGIN_CONTEXT_PARAM = "login_context";

	private static final String MOBILE_APP_LOGIN_CONTEXT = "mobile_app_login";
	private static final String CLINICIAN_PORTAL_LOGIN_CONTEXT = "cp_login";
	private static final String COMPANY_MANAGEMENT_PORTAL_LOGIN_CONTEXT = "cm_login";

	private static final String AUTHENTICATE_PATIENT_PREPARED_SQL_QUERY = "SELECT p_id, company FROM patient WHERE p_id = ? AND Active = \'Not Active\'";
	private static final String GET_TERMS_AND_CONDITIONS_SQL_QUERY = "SELECT Terms_and_Conditions, Permissions FROM company WHERE Company_Name = ?";
	private static final String AUTHENTICATE_PATIENT_PREPARED_SQL_QUERY_UPDATE_TOKEN_SQL_QUERY = "UPDATE patient SET Active= \'Active\', Token = ? where p_id= ?;";

	private static final String AUTHENTICATE_CLINICIAN_PREPARED_SQL_QUERY = "SELECT Firstname, Lastname, Password, Role FROM clinicians WHERE Email = ?";
	private static final String AUTHENTICATE_CLINICIAN_PREPARED_SQL_QUERY_UPDATE_TOKEN = "UPDATE clinicians SET Token=? where Email= ?;";

	private static final long serialVersionUID = 1L;

	private static Log logger = LogFactory.getLog(LoginServlet.class);

	public LoginServlet() {
		super();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response) Implemented here as final to prevent subclasses overriding
	 *      this method
	 */
	protected final void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		// extract out expected params from the request
		String loginContext = request.getParameter(LOGIN_CONTEXT_PARAM);
		String id = request.getParameter(ID_PARAM);
		String password = request.getParameter(PWORD_PARAM);

		if (!isValidLoginContext(loginContext)) {
			RnsUtils.set400Reponse(response, loginContext + " login action is not supported.");
			return;
		}

		Map<String, Object> verifyResult = new HashMap<>();

		if (MOBILE_APP_LOGIN_CONTEXT.equals(loginContext)) {
			verifyResult = verifyMobileAppLogin(id);
		} else if (CLINICIAN_PORTAL_LOGIN_CONTEXT.equals(loginContext)) {
			verifyResult = verifyClinicianPortalLogin(id, password);
		} else if (COMPANY_MANAGEMENT_PORTAL_LOGIN_CONTEXT.equals(loginContext)) {
			// TODO implement when portal exists!
			// verifyResult = verifyCompanyManagementPortalLogin(id, password);
			verifyResult.put("error", loginContext + " login action is not supported.");
		} else {
			verifyResult.put("error", loginContext + " login action is not supported.");
		}

		String jsonResult = new Gson().toJson(verifyResult);
		logger.debug("jsonResult: " + jsonResult);
		response.getWriter().write(jsonResult);

	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response) Implemented here as final to prevent subclasses overriding
	 *      this method
	 */
	protected final void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doPost(request, response);
	}

	private boolean isValidLoginContext(String action) {

		if (StringUtils.isBlank(action)) {
			return false;
		}

		if (action.equals(MOBILE_APP_LOGIN_CONTEXT) || action.equals(CLINICIAN_PORTAL_LOGIN_CONTEXT)
				|| action.equals(COMPANY_MANAGEMENT_PORTAL_LOGIN_CONTEXT)) {
			return true;
		} else {
			return false;
		}

	}

	private Map<String, Object> verifyMobileAppLogin(String id) {

		logger.trace("Entering verifyMobileAppLogin...");

		// Initialise result of this method
		Map<String, Object> patientLoginResultMap = new HashMap<String, Object>();

		try {

			// Call auth patient query
			Collection<Map<String, String>> authenticatePatientResult = PAT_DAO
					.executeStatement(AUTHENTICATE_PATIENT_PREPARED_SQL_QUERY, new Object[] { id });
			if (authenticatePatientResult.isEmpty()) {
				throw new Exception("Empty result when executing AUTHENTICATE_PATIENT_PREPARED_SQL_QUERY");
			}

			// Extract company from auth query result
			String company = authenticatePatientResult.iterator().next().get("company");

			// Create JWT token for the patient
			String token = issueJwtToken(id, MOBILE_APP_LOGIN_CONTEXT);

			// Fetch terms and conditions for the patient's company
			Collection<Map<String, String>> termsAndConditionsResult = PAT_DAO
					.executeStatement(GET_TERMS_AND_CONDITIONS_SQL_QUERY, new Object[] { company });
			Map<String, String> terms = termsAndConditionsResult.iterator().next();

			// Update database with patient's token
			PAT_DAO.executeStatement(AUTHENTICATE_PATIENT_PREPARED_SQL_QUERY_UPDATE_TOKEN_SQL_QUERY,
					new Object[] { token, id });

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

	private Map<String, Object> verifyClinicianPortalLogin(String id, String requestPassword) {

		logger.trace("Entering verifyClinicianPortalLogin...");
		
		// Initialise result of this method
		Map<String, Object> clinicianLoginResultMap = new HashMap<String, Object>();

		try {
			
			// call auth clinician query
			Collection<Map<String, String>> authenticateClinicianResult = PAT_DAO
					.executeStatement(AUTHENTICATE_CLINICIAN_PREPARED_SQL_QUERY, new Object[] { id, requestPassword });
			if (authenticateClinicianResult.isEmpty()) {
				throw new Exception("Empty result when executing AUTHENTICATE_CLINICIAN_PREPARED_SQL_QUERY");
			}

			// Extract the value map (row 0) from the auth clinician query result
			Map<String, String> authenticateClinicianResultValues = authenticateClinicianResult.iterator().next();

			// Verify the request password matches the returned password
			if (!BCrypt.checkpw(requestPassword, authenticateClinicianResultValues.get("Password"))) {
				clinicianLoginResultMap.put("jwt_token", "Invalid Credentials");
				return clinicianLoginResultMap;
			}

			// Create a JWT token for the clinician
			String token = issueJwtToken(id, CLINICIAN_PORTAL_LOGIN_CONTEXT);
			
			// Update the database with the clinician's JWT token
			PAT_DAO.executeStatement(AUTHENTICATE_CLINICIAN_PREPARED_SQL_QUERY_UPDATE_TOKEN,
					new Object[] { token, id });

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

	private Map<String, Object> verifyCompanyManagementPortalLogin(String id, String password) {
		return null;
	}

	private String issueJwtToken(String id, String context) {
		String token = Jwts.builder().setSubject((String) id).claim(LOGIN_CONTEXT_PARAM, context)
				.signWith(SignatureAlgorithm.HS512, Constants.JWT_KEY).compact();
		return token;
	}

}
