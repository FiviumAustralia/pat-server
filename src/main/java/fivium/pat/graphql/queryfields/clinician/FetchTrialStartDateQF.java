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

import fivium.pat.graphql.PAT_BaseQF;
import fivium.pat.utils.PAT_DAO;
import fivium.pat.utils.PatAuthUtils;
import fivium.pat.utils.PatUtils;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLArgument;
import graphql.schema.GraphQLObjectType;

public class FetchTrialStartDateQF extends PAT_BaseQF {

	private static final String FETCH_START_DATE = "SELECT MIN(dateA) FROM (SELECT date AS dateA FROM stepdata UNION ALL SELECT date AS dateB FROM weightdata) AS tab;";
	private static Log logger = LogFactory.getLog(FetchTrialStartDateQF.class);
	
	@Override
	protected GraphQLObjectType defineField() {
		return newObject()
			    .name("FetchDates")
			    .description("Fetches the trial start date from the database")
			    .field(newFieldDefinition()
			    		.name("result")
			            .type(GraphQLString))		    
			   .build();	
	}

	@Override
	protected List<GraphQLArgument> defineArguments() {
		return Arrays.asList();
	}

	@Override
	protected Object fetchData(DataFetchingEnvironment environment)  {
		Map<String, String> resultMap = new HashMap<String, String>();
			Collection<Map<String, String>> sqlResult;
			try {
				sqlResult = PAT_DAO.executeStatement(FETCH_START_DATE, null);
				if(!sqlResult.isEmpty() && null != sqlResult.iterator().next().get("max(date)")){
					resultMap.put("result", sqlResult.iterator().next().get("max(date)"));
				} else {
					resultMap.put("result", PatUtils.getCurrentFormattedDate());
				}
			}	catch (Exception e) {
				logger.error("Unexpected error occured", e);
				resultMap.put("result", "Error fetching start date");
			}
		return resultMap;
	}
}
