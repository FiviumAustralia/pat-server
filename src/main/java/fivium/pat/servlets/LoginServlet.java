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
	private static final String ACTION_PARAM = "action";

	private static final String MOBILE_APP_LOGIN_ACTION = "ma_login";
	private static final String CLINICIAN_PORTAL_LOGIN_ACTION = "cp_login";
	private static final String COMPANY_MANAGEMENT_PORTAL_LOGIN_ACTION = "cm_login";

	private static final String AUTHENTICATE_PATIENT_PREPARED_SQL_QUERY = "SELECT p_id, company FROM patient WHERE p_id = ? AND Active = \'Not Active\'";
	private static final String GET_TERMS_AND_CONDITIONS_SQL_QUERY = "SELECT Terms_and_Conditions, Permissions FROM company WHERE Company_Name = ?";
	private static final String AUTHENTICATE_PATIENT_PREPARED_SQL_QUERY_UPDATE_TOKEN_SQL_QUERY = "UPDATE patient SET Active=?,Token=? where p_id= ?;";

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
		String action = request.getParameter(ID_PARAM);
		String id = request.getParameter(PWORD_PARAM);
		String password = request.getParameter(ACTION_PARAM);

		if (!isValidAction(action)) {
			RnsUtils.set400Reponse(response, action + " login action is not supported.");
			return;
		}

		Map<String, Object> verifyResult = new HashMap<>();

		if (MOBILE_APP_LOGIN_ACTION.equals(action)) {
			verifyResult = verifyMobileAppLogin(id);
		} else if (CLINICIAN_PORTAL_LOGIN_ACTION.equals(action)) {
			verifyResult = verifyClinicianPortalLogin(id, password);
		} else if (COMPANY_MANAGEMENT_PORTAL_LOGIN_ACTION.equals(action)) {
			// TODO implement when portal exists!
			// verifyResult = verifyCompanyManagementPortalLogin(id, password);
			verifyResult.put("error", action + " login action is not supported.");
		} else {
			verifyResult.put("error", action + " login action is not supported.");
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

	private boolean isValidAction(String action) {

		if (StringUtils.isBlank(action)) {
			return false;
		}

		if (action.equals(MOBILE_APP_LOGIN_ACTION) || action.equals(CLINICIAN_PORTAL_LOGIN_ACTION)
				|| action.equals(COMPANY_MANAGEMENT_PORTAL_LOGIN_ACTION)) {
			return true;
		} else {
			return false;
		}

	}

	private Map<String, Object> verifyMobileAppLogin(String id) {

		logger.trace("Entering verifyMobileAppLogin...");
		Object[] queryArgs = new Object[] { id };
		logger.info("Authenticating p_id: " + id);
		Map<String, Object> resultMap = new HashMap<String, Object>();
		Collection<Map<String, String>> result;
		Collection<Map<String, String>> termsAndConditions;
		try {
			result = PAT_DAO.executeStatement(AUTHENTICATE_PATIENT_PREPARED_SQL_QUERY, queryArgs);
			if (result.isEmpty()) {
				throw new Exception("Empty result when executing AUTHENTICATE_PATIENT_PREPARED_SQL_QUERY");
			}
			Iterator<Map<String, String>> iter = result.iterator();
			Map<String, String> resultRow1 = iter.next();
			String company = resultRow1.get("company");
			String token = Jwts.builder().setSubject((String) id).signWith(SignatureAlgorithm.HS512, Constants.JWT_KEY)
					.compact();
			termsAndConditions = PAT_DAO.executeStatement(GET_TERMS_AND_CONDITIONS_SQL_QUERY, new Object[] { company });
			Map<String, String> terms = termsAndConditions.iterator().next();
			Object[] queryArgs_2 = new Object[] { "Active", token, id };
			PAT_DAO.executeStatement(AUTHENTICATE_PATIENT_PREPARED_SQL_QUERY_UPDATE_TOKEN_SQL_QUERY, queryArgs_2);
			logger.info("User authenticated, token generated.");
			resultMap.put("jwt_token", token);
			resultMap.put("terms", terms.get("Terms_and_Conditions"));
			resultMap.put("permissions", terms.get("Permissions"));
			resultMap.put("company", company);
		} catch (Exception e) {
			logger.error("Exception occurred trying to authenticate p_id", e);
			resultMap.put("error", "Supplied user id doesn't exist, please contact your clinician");
		}

		return resultMap;

	}

	private Map<String, Object> verifyClinicianPortalLogin(String id, String p_word) {
		Object[] queryArgs = new Object[] { id, p_word };

		Collection<Map<String, String>> result;
		Collection<Map<String, String>> updateTokenResult;
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
			result = PAT_DAO.executeStatement(AUTHENTICATE_CLINICIAN_PREPARED_SQL_QUERY, queryArgs);
			if (result.isEmpty()) {
				resultMap.put("jwt_token", "Invalid Credentials");
				return resultMap;
			}

			Map<String, String> aMap = result.iterator().next();
			String password = aMap.get("Password");
			String role = aMap.get("Role");
			String firstname = aMap.get("Firstname");
			String lastname = aMap.get("Lastname");
			
			if (!BCrypt.checkpw(queryArgs[1].toString(), password)) {
				resultMap.put("jwt_token", "Invalid Credentials");
				return resultMap;
			}
			
			String token = Jwts.builder().setSubject((String) id)
					.signWith(SignatureAlgorithm.HS512, LegacyInternalServerUtils.JWT_KEY).compact();
			resultMap.put("Firstname", firstname);
			resultMap.put("Lastname", lastname);
			resultMap.put("jwt_token", token);
			resultMap.put("Role", role);
			Object[] queryArgsUpdate = new Object[] { token, id };
			updateTokenResult = PAT_DAO.executeStatement(AUTHENTICATE_CLINICIAN_PREPARED_SQL_QUERY_UPDATE_TOKEN,
					queryArgsUpdate);
			return resultMap;

		} catch (Exception e) {
			logger.error("unexpected error occured", e);
			resultMap.put("jwt_token", "Invalid Credentials");
			return resultMap;
		}
	}

	private Map<String, Object> verifyCompanyManagementPortalLogin(String id, String password) {
		return null;
	}

}
