package fivium.pat.servlets;

import static fivium.pat.utils.Constants.CLINICIAN_PORTAL_LOGIN_CONTEXT;
import static fivium.pat.utils.Constants.MOBILE_APP_LOGIN_CONTEXT;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.gson.Gson;

import fivium.pat.utils.PatAuthUtils;

public class LoginServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private static final String ID_PARAM = "id";
	private static final String PWORD_PARAM = "pwd";
	private static final String LOGIN_CONTEXT_PARAM = "ctx";
	
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

		Map<String, String> loginResult = new HashMap<>();

		if (MOBILE_APP_LOGIN_CONTEXT.equals(loginContext)) {
			loginResult = PatAuthUtils.loginPatient(id);
		} else if (CLINICIAN_PORTAL_LOGIN_CONTEXT.equals(loginContext)) {
			loginResult = PatAuthUtils.loginClinician(id,password);
		} else {
			loginResult.put("error", loginContext + " login context is not supported.");
		}

		String jsonResult = new Gson().toJson(loginResult);
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


}
