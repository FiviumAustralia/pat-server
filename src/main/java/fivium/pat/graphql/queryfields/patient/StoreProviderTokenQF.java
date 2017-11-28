package fivium.pat.graphql.queryfields.patient;

import static graphql.Scalars.GraphQLString;
import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static graphql.schema.GraphQLObjectType.newObject;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;

import com.google.gson.Gson;

import fivium.pat.graphql.PAT_BaseQF;
import fivium.pat.provider.data.AppData;
import fivium.pat.provider.utils.FitbitDataRetriever;
import fivium.pat.utils.Constants;
import fivium.pat.utils.PatUtils;
import fivium.pat.utils.PAT_DAO;
import graphql.Scalars;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLArgument;
import graphql.schema.GraphQLObjectType;

public class StoreProviderTokenQF extends PAT_BaseQF {
	private static Log logger = LogFactory.getLog(StoreProviderTokenQF.class);

	private static final String STORE_REFRESH_TOKEN_SQL_QUERY = "UPDATE patient SET provider_user_id=?, provider_refresh_token=?, provider_permissions=?, provider=? WHERE p_id=?";
	private static final String FITBIT_ACESS_TOKEN_ENDPOINT = "https://api.fitbit.com/oauth2/token";

	@Override
	protected GraphQLObjectType defineField() {
		return newObject().name("ProviderToken").description("Store the provider token received by a patient")
				.field(newFieldDefinition()
						.name("response").type(GraphQLString))
				.field(newFieldDefinition()
						.name("appData").type(GraphQLString))
				.build();
	}

	protected List<GraphQLArgument> defineArguments() {
		return Arrays.asList(new GraphQLArgument("users_jwt", Scalars.GraphQLString),
				new GraphQLArgument("activity_token", Scalars.GraphQLString),
				new GraphQLArgument("provider", Scalars.GraphQLString));
	}

	protected Object fetchData(DataFetchingEnvironment environment) {
		logger.info("Entering ProviderToken...");
		Map<String, String> resultMap = new HashMap<String, String>();
		String activity_token = environment.getArgument("activity_token").toString();
		String provider = environment.getArgument("provider").toString();
		String subject = PatUtils.getUserIdFromJWT(environment.getArgument("users_jwt").toString());
		String access_token = "", refresh_token = "", scope = "", user_id = "";
		HttpClient client = HttpClientBuilder.create().build();
		HttpPost post = getPostRequest(provider, activity_token);
		if (post == null) {
			resultMap.put("response", "Unknown provider \"" + provider + "\". Currently only fitbit is supported");
			return resultMap;
		} else {
			try {
				HttpResponse response = client.execute(post);
				if (response.getStatusLine().getStatusCode() != HttpServletResponse.SC_OK) {
					throw new Exception("Encountered following HTTP error: " + response.getStatusLine().getStatusCode());
				}
				String resultAsJSON_String = IOUtils.toString(response.getEntity().getContent());
				logger.debug("fitbit_response as JSON string:  " + resultAsJSON_String);
				Map<String, String> resultAsJSON_Object = new Gson().fromJson(resultAsJSON_String, Map.class);
				access_token = resultAsJSON_Object.get("access_token");
				refresh_token = resultAsJSON_Object.get("refresh_token");
				scope = resultAsJSON_Object.get("scope");
				user_id = resultAsJSON_Object.get("user_id");
			} catch (Exception e) {
				logger.error("An exception occured getting access token from fitbit" + e);
				resultMap.put("response", "Unable to verify activity_token with fitbit");
				return resultMap;
			}
		}
		resultMap = saveUsersTokens(provider, subject, refresh_token, scope, user_id, access_token);

		return resultMap;
	}

	private Map<String, String> saveUsersTokens(String provider, String subject, String refresh_token, String scope, String user_id, String access_token) {
		Map<String, String> resultMap = new HashMap<String, String>();
		AppData appData = FitbitDataRetriever.pollFitbitForAUser(user_id, access_token);
		Object[] queryArgs = new Object[] { user_id, refresh_token, scope, provider, subject };
		if (logger.isDebugEnabled()) {
			logger.debug("Saving provider activity token for " + subject);
		}
		Collection<Map<String, String>> sqlResult;
		try {
			sqlResult = PAT_DAO.executeStatement(STORE_REFRESH_TOKEN_SQL_QUERY, queryArgs);
			if (sqlResult.isEmpty()) {
				resultMap.put("response", new Gson().toJson(appData));
			}
		} catch (Exception ex) {
			logger.error("Exception occurred trying to store provider token", ex);
			resultMap.put("response", ex.getMessage());
			return resultMap;
		}
		return resultMap;
	}

	private HttpPost getPostRequest(String provider, String activity_token) {
		if ("fitbit".equalsIgnoreCase(provider)) {
			// get access_token and refresh_token from fitbit
			HttpPost post = new HttpPost(FITBIT_ACESS_TOKEN_ENDPOINT);
			post.setHeader("Content-Type", "application/x-www-form-urlencoded");
			post.setHeader("Authorization", "Basic " + Constants.FITBIT_CLIENT_SECRET);
			String authString = "code=" + activity_token + "&grant_type=authorization_code&clientId="
					+ Constants.FITBIT_CLIENT_ID + "&redirect_uri=" + Constants.FITBIT_REDIRECT_URI;
			post.setEntity(new StringEntity(authString, "UTF-8"));
			return post;
		} else {
			return null;
		}
	}

}
