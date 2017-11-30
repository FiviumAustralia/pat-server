package fivium.pat.graphql.queryfields.superuser;

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
import graphql.GraphQLException;
import graphql.Scalars;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLArgument;
import graphql.schema.GraphQLObjectType;

public class DeleteClinicianQF extends PAT_BaseQF {

	private static final String DELETE_CLINICIAN_PREPARED_SQL_QUERY = "DELETE FROM clinicians WHERE Email=?;";
	private static Log logger = LogFactory.getLog(DeleteClinicianQF.class);
	
	@Override
	protected GraphQLObjectType defineField() {
		return newObject()
			    .name("DeleteClinician")
			    .description("Makes a request to the internal server to delete a clinician")
			    .field(newFieldDefinition()
			    		.name("result")
			            .type(GraphQLString))		    
			   .build();	
	}

	@Override
	protected List<GraphQLArgument> defineArguments() {
		return Arrays.asList(
				new GraphQLArgument("clinician_email", Scalars.GraphQLString)
				);
	}

	@Override
	protected Object fetchData(DataFetchingEnvironment environment)  {
		Map<String, String> resultMap = new HashMap<String, String>();
		try {
			Object[] queryArgs = new Object[] {
					environment.getArgument("clinician_email"),
			};
			Collection<Map<String, String>> sqlResult;
			try {
				sqlResult = PAT_DAO.executeStatement(DELETE_CLINICIAN_PREPARED_SQL_QUERY, queryArgs);
				if(sqlResult.isEmpty()){
					resultMap.put("result", "Clinician deleted successfully");
				}
			}	catch (Exception e) {
				logger.error("Unexpected error occured", e);
				throw new GraphQLException("Unexpected execution error", e);
			}
		} catch (Exception ex) {
			logger.error("Unexpected error occured", ex);
			resultMap.put("result", ex.getMessage());
		}
		
		return resultMap;
	}
	


}
