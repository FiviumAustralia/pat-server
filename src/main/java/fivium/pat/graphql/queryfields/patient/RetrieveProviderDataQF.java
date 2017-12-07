package fivium.pat.graphql.queryfields.patient;

import static graphql.Scalars.GraphQLString;
import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static graphql.schema.GraphQLObjectType.newObject;
import static fivium.pat.utils.Constants.JWT_GRAPHQL_QUERY_PARAM;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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

public class RetrieveProviderDataQF extends PAT_BaseQF {
	
	private static Log logger = LogFactory.getLog(RetrieveProviderDataQF.class);

	private static final String PROVIDER_FITBIT = "fitbit";
	

	@Override
	protected GraphQLObjectType defineField() {
		return newObject().name("RetrieveProviderData").description("Retrieve data from Fitbit when the user loads the dashboard.")
				.field(newFieldDefinition().name("response").type(GraphQLString)).build();
	}

	protected List<GraphQLArgument> defineArguments() {
		return Arrays.asList(
				new GraphQLArgument("provider", Scalars.GraphQLString),
				new GraphQLArgument("lastFetchedDate", Scalars.GraphQLString));
	}

protected Object fetchData(DataFetchingEnvironment environment) {
		
		Map<String, String> resultMap = new HashMap<String, String>();
		String provider = environment.getArgument("provider");
		String patientId = PatUtils.getUserIdFromJWT(environment.getArgument(JWT_GRAPHQL_QUERY_PARAM).toString());
		
		if(!PROVIDER_FITBIT.equalsIgnoreCase(provider)) {
			resultMap.put("response", "Unknown provider");
			return resultMap;
		}
		Collection<Map<String, String>> resultGetUser;
		try {
			resultGetUser = PAT_DAO.executeStatement(Constants.GET_SINGLE_FITBIT_USER, new Object[] {patientId});
			if(resultGetUser.isEmpty()) {
				resultMap.put("response", "User is not authorized yet");
				return resultMap;
			} 
			String refreshToken = resultGetUser.iterator().next().get("provider_refresh_token");	
			String accessToken  = FitbitDataRetriever.getAccessToken(patientId, refreshToken, false);
			String lastFetchedDate = environment.getArgument("lastFetchedDate");
			AppData  appData = FitbitDataRetriever.pullFitbitDataForMobileApp(patientId,lastFetchedDate, accessToken);
			
			if(appData == null) {
				resultMap.put("response", "Error - failed to retreieve fitbit data");
				return resultMap;
			} 
			resultMap.put("response", new Gson().toJson(appData));	
			
		} catch (Exception e) {
			logger.error("Exception occurred trying to get the provider_refresh_token in the retrive data from fitbit route"+e);
			resultMap.put("response", "Unable to get fitbit data, please authorise your FitBit app again");
			return resultMap;
		}
		
		return resultMap;
	}

}
