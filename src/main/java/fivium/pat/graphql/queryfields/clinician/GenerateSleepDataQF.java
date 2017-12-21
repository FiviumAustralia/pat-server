// Sample GraphQL Query: {"graphQL_Query":"query ($jwt_token: String) {SleepData (jwt_token:$jwt_token, study_id: \"42\") {result} }"}
package fivium.pat.graphql.queryfields.clinician;

import static graphql.Scalars.GraphQLString;
import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static graphql.schema.GraphQLObjectType.newObject;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import fivium.pat.graphql.PAT_BaseQF;
import fivium.pat.utils.PAT_DAO;
import graphql.GraphQLException;
import graphql.Scalars;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLArgument;
import graphql.schema.GraphQLObjectType;

import static fivium.pat.utils.Constants.GET_CLINICIAN_COMPANY;
import static fivium.pat.utils.Constants.JWT_GRAPHQL_QUERY_PARAM;
import fivium.pat.utils.PatUtils;

public class GenerateSleepDataQF extends PAT_BaseQF {

  private static final String GENERATE_GRAPH_PREPARED_SQL_QUERY = "SELECT sleepdata.duration, sleepdata.efficiency, sleepdata.start_time, sleepdata.end_time, sleepdata.minutes_asleep, sleepdata.minutes_awake, sleepdata.minutes_restless, patient_details.first_name FROM sleepdata INNER JOIN patient_details ON sleepdata.study_id = patient_details.study_id INNER JOIN patient ON sleepdata.study_id = patient.p_id WHERE date= (SELECT MAX(date) FROM sleepdata) AND patient.Company = ?";
  private static final String GENERATE_USER_GRAPH_PREPARED_SQL_QUERY= "SELECT sleepdata.duration, sleepdata.efficiency, sleepdata.date, sleepdata.start_time, sleepdata.end_time, sleepdata.minutes_asleep, sleepdata.minutes_awake, sleepdata.minutes_restless, patient_details.first_name FROM sleepdata INNER JOIN patient_details ON sleepdata.study_id = patient_details.study_id INNER JOIN patient ON sleepdata.study_id = patient.p_id  WHERE patient.Company = ? AND sleepdata.study_id = ?";
  private static Log logger = LogFactory.getLog(GenerateSleepDataQF.class);

  @Override
  protected GraphQLObjectType defineField() {
    return newObject()
            .name("SleepData")
            .description("Get the latest sleep data for graphs")
            .field(newFieldDefinition()
  			    		.name("result")
  			            .type(GraphQLString))		    
  			   .build();	
  }

  @Override
  protected List<GraphQLArgument> defineArguments() {
  	return Arrays.asList(
  			new GraphQLArgument("study_id", Scalars.GraphQLString)
  			);
  }

  @Override
  protected Object fetchData(DataFetchingEnvironment environment) {
	  Collection<Map<String, String>> sqlResult;
      Map<String, String> result = new HashMap<String, String>();
      Map<String, String> aMap = new HashMap<String, String>();
      Gson gson = new GsonBuilder().disableHtmlEscaping().create();

    try {
    	String clinician_id = PatUtils.getUserIdFromJWT(environment.getArgument(JWT_GRAPHQL_QUERY_PARAM).toString());
    	Collection<Map<String, String>> company_result = PAT_DAO.executeFetchStatement(GET_CLINICIAN_COMPANY, new Object[] { clinician_id });
    	String clinician_company = company_result.iterator().next().get("Company");
    	  
        
        Object[] queryArgs = new Object[] {
        		clinician_company,
        		environment.getArgument("study_id")
    			};
//    	If two arguments in the query (in this case, Company and study_id)
    	 if (queryArgs.length > 1 && queryArgs[1] != null ) {
    		 sqlResult = PAT_DAO.executeFetchStatement(GENERATE_USER_GRAPH_PREPARED_SQL_QUERY, queryArgs);
       } else{
				sqlResult = PAT_DAO.executeFetchStatement(GENERATE_GRAPH_PREPARED_SQL_QUERY, queryArgs);
       }
    	 if (!sqlResult.isEmpty()) {
					String dailySleepData = gson.toJson(sqlResult);
					aMap.put("dailySleepData", dailySleepData);
					String jsonResponse = gson.toJson(aMap);
					jsonResponse = jsonResponse.replace("\\", "");
					result.put("result", jsonResponse);
			}
    } catch (Exception e) {
    	logger.error("Unexpected error occured", e);
        throw new GraphQLException("Unexpected error fetching list of patients", e);
    }
    return result;
  }
}
