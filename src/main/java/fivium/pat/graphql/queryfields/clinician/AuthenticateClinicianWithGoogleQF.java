package fivium.pat.graphql.queryfields.clinician;

import static graphql.Scalars.GraphQLString;
import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static graphql.schema.GraphQLObjectType.newObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import fivium.pat.graphql.queryfields.PAT_BaseQF;
import fivium.pat.utils.Constants;
import fivium.pat.utils.PAT_DAO;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLArgument;
import graphql.schema.GraphQLObjectType;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class AuthenticateClinicianWithGoogleQF extends PAT_BaseQF {
	private static Log logger = LogFactory.getLog(AuthenticateClinicianWithGoogleQF.class);
	private static final String GOOGLE_AUTH_VERIFICATION_URL = "https://www.googleapis.com/oauth2/v3/tokeninfo?access_token=";

	private static final String AUTHENTICATE_PATIENT_PREPARED_SQL_QUERY = "SELECT Role FROM clinicians WHERE Email = ?";
	private static final String AUTHENTICATE_PATIENT_PREPARED_SQL_QUERY_UPDATE_TOKEN = "UPDATE clinicians SET Token=? where Email= ?;";

	@Override
	protected GraphQLObjectType defineField() {

		return newObject().name("AuthenticateClinicianUsingGoogle").description("Authenticate Clinician using Google Jwt")
				.field(newFieldDefinition().name("jwt_token").description("The authentication token for the clinician")
						.type(GraphQLString))
				.field(newFieldDefinition().name("Role").description("The role of the user logging in").type(GraphQLString))
				.build();
	}

	@Override
	protected List<GraphQLArgument> defineArguments() {
		return Arrays.asList(new GraphQLArgument("token", GraphQLString));
	}

	@Override
	protected Object fetchData(DataFetchingEnvironment environment) {
		Map<String, String> resultMap = new HashMap<String, String>();
		try {
			Map<String, String> graphqlResultMap = getUserMap(environment.getArgument("token").toString());
			if (Boolean.parseBoolean(graphqlResultMap.get("email_verified"))) {
				Collection<Map<String, String>> queryResult;
				Collection<Map<String, String>> updateTokenResult;
				queryResult = PAT_DAO.executeStatement(AUTHENTICATE_PATIENT_PREPARED_SQL_QUERY,
						new Object[] { graphqlResultMap.get("email") });
				if (!queryResult.isEmpty()) {
					Map<String, String> queryResultMap = queryResult.iterator().next();
					String role = queryResultMap.get("Role");
					String token = Jwts.builder().setSubject(graphqlResultMap.get("email"))
							.signWith(SignatureAlgorithm.HS512, Constants.JWT_KEY).compact();
					resultMap.put("jwt_token", token);
					resultMap.put("Role", role);
					Object[] queryArgsUpdate = new Object[] { token, graphqlResultMap.get("email") };
					updateTokenResult = PAT_DAO.executeStatement(AUTHENTICATE_PATIENT_PREPARED_SQL_QUERY_UPDATE_TOKEN,
							queryArgsUpdate);
					return resultMap;
				} else {
					return resultMap.put("jwt_token", "Invalid Credentials");
				}
			} else {
				return resultMap.put("jwt_token", "user is not verified.");
			}

		} catch (ClassNotFoundException e) {
			logger.error("ClassNotFoundException Occurred", e);
		} catch (SQLException e) {
			logger.error("SQLException Occurred", e);
		} catch (Exception e) {
			logger.error("Unexpected Error Occurred", e);
		}
		return resultMap.toString();
	}

	private Map<String, String> getUserMap(String token) {

		StringBuilder result = new StringBuilder();
		Gson gson = new GsonBuilder().disableHtmlEscaping().create();
		Map<String, String> resultMap = new HashMap<String, String>();
		URL url;
		try {
			url = new URL(GOOGLE_AUTH_VERIFICATION_URL + token);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line;
			while ((line = rd.readLine()) != null) {
				result.append(line);
			}
			Type type = new TypeToken<Map<String, String>>() {
			}.getType();
			resultMap = gson.fromJson(result.toString(), type);
			rd.close();
		} catch (MalformedURLException e) {
			resultMap.put("email_verified", "false");
			logger.error("Malformed URL exception ", e);
		} catch (IOException e) {
			resultMap.put("email_verified", "false");
			logger.error("IOException ", e);
		} catch(Exception e) {
			resultMap.put("email_verified", "false");
			logger.error("Email not verified ", e);
		}
		return resultMap;

	}

}
