package fivium.pat.servlets;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.gson.Gson;

import fivium.pat.graphql.GraphQLController;
import fivium.pat.utils.Constants;
import fivium.pat.utils.RnsUtils;
import graphql.schema.GraphQLSchema;

public abstract class BaseServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;	
	private static final String TABLE_NAME = "internaluser";
	
	private static final List<String> VALID_CONTENT_TYPES = Arrays.asList(
			"application/json", 
			"application/json; charset=utf-8"
			);  
	
	private static Log logger = LogFactory.getLog(BaseServlet.class);

	protected abstract GraphQLSchema getGraphQL_SchemaInstance();	
	
	private String graphQL_Query = null;
	
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request,
	 *      HttpServletResponseresponse)
	 */
	protected final void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		// validate request
		if (!isValidRequest(request, response)) {
			return;
		}
		
		logger.debug("GraphQl Query " + graphQL_Query);
		
		//Execute GraphQL query
		GraphQLController graphQLControllerInstance = GraphQLController.getInstance();
		Map<String, Object> result = graphQLControllerInstance.executeOperation(graphQL_Query, getGraphQL_SchemaInstance(), createGraphQL_QueryVariables(request));
		
		//Convert the GraphQL result map into a JSON string
		String jsonResult = new Gson().toJson(result);
		
		logger.debug("jsonResult: " + jsonResult);
		response.getWriter().write(jsonResult);
		
	}
	
	private boolean isValidRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
		
		// if request isn't a POST the return a 400 error
		if (!request.getMethod().equals("POST")) {
			RnsUtils.set400Reponse(response, "Invalid Request, " + request.getMethod() + " requests are not allowed.");
			return false;
		}
		
		// if the content type of the request isn't "application/json" OR "application/json; charset=utf-8" then return a 400 error
		if (! VALID_CONTENT_TYPES.contains(request.getContentType())  ) {
			RnsUtils.set400Reponse(response, "Invalid Request, " + request.getContentType() + " requests are not allowed");
			return false;
		}
		
		// if JWT verification is required and fails then return a 400 error
		if( servletRequiresJWT_Verification() && !RnsUtils.verifyToken(request.getHeader("Authorization"), TABLE_NAME)){
			RnsUtils.set400Reponse(response, "Unauthorized access");
			return false;
		}
		
		// if JSON request parsing fails then return a 400 error.  Otherwise, set the graphQL_Query local variable 
		Map<String, Object> jsonRequestObject = RnsUtils.parseJsonRequest(request);
		if (jsonRequestObject == null || !jsonRequestObject.containsKey("graphQL_Query")) {
			RnsUtils.set400Reponse(response, "Invalid Request, failed to parse JSON request.");
			return false;
		} else {
			graphQL_Query = (String) jsonRequestObject.get("graphQL_Query");
			return true;
		}
		
	}
	
	private Map<String, Object> createGraphQL_QueryVariables(HttpServletRequest servletRequest) {
		
		HashMap<String, Object> vars = new HashMap<String, Object>();
		vars.put("jwt_token", servletRequest.getHeader("Authorization"));		
		return vars;	
	}
	
	/**
	 * By default Servlets don't require JWT verification since the JWT token will be verified by the SecurityFilter class.
	 * However Servlets that are excluded from the SecurityFilter in the web.xml should override this method and return true to perform
	 * JWT verification at the Servlet level.
	 * @return whether JWT verification is required at the servlet level
	 */
	protected boolean servletRequiresJWT_Verification() {
		return false;
	}
	
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 *      Implemented here as final to prevent subclasses overriding this method
	 */
	protected final void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}
	
	
}
