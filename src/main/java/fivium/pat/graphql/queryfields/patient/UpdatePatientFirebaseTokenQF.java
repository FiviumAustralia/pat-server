package fivium.pat.graphql.queryfields.patient;

import static graphql.Scalars.GraphQLString;
import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static graphql.schema.GraphQLObjectType.newObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import fivium.pat.utils.PatUtils;
import fivium.pat.graphql.PAT_BaseQF;
import fivium.pat.utils.PAT_DAO;
import graphql.GraphQLException;
import graphql.Scalars;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLArgument;
import graphql.schema.GraphQLObjectType;

public class UpdatePatientFirebaseTokenQF extends PAT_BaseQF {

	private static Log logger = LogFactory.getLog(UpdatePatientFirebaseTokenQF.class);

	private static final String UPDATE_PATIENT_FIREBASE_TOKEN = "UPDATE patient SET firebase_device_token = ? WHERE p_id = ?";
	
	@Override
	protected GraphQLObjectType defineField() {
	    return newObject()
	            .name("UpdatePatientFirebaseToken")
	            .description("Update's the patients Firebase Token in the DB.")
			    .field(newFieldDefinition()
			    		.name("result")
			            .type(GraphQLString))		    
			   			.build();	
	}

	protected List<GraphQLArgument> defineArguments() {
		return Arrays.asList(
				new GraphQLArgument("firebase_token", Scalars.GraphQLString)
			);
	}

	@Override
	protected Object fetchData(DataFetchingEnvironment environment) {
		
		Map<String, String> result = new HashMap<String, String>();
		
	    try {
	    	String p_id = PatUtils.getUserIdFromJWT((String) environment.getArgument("jwt_token"));
			PAT_DAO.executeStatement(UPDATE_PATIENT_FIREBASE_TOKEN, new Object[] { environment.getArgument("firebase_token"), p_id });
			result.put("result", "Sucesfully updated firebase token.");
	    } catch (Exception e) {
	    	logger.error("Unexpected error occured", e);
	        throw new GraphQLException("Unexpected error updating firebase token", e);
	    }

	    return result;
	}

}
