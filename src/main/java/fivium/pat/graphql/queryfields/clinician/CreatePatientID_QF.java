package fivium.pat.graphql.queryfields.clinician;

import static graphql.Scalars.GraphQLString;
import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static graphql.schema.GraphQLObjectType.newObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import fivium.pat.graphql.PAT_BaseQF;
import fivium.pat.utils.PAT_DAO;
import graphql.Scalars;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLArgument;
import graphql.schema.GraphQLObjectType;
public class CreatePatientID_QF extends PAT_BaseQF {
	private static Log logger = LogFactory.getLog(CreatePatientID_QF.class);
	private static final String CREATE_PATIENT_ID_PREPARED_SQL_QUERY = "INSERT INTO patient (p_id, Token, Active) VALUES (?, '', 'Not Active')";
	
	@Override
	protected GraphQLObjectType defineField() {
		return newObject()
			    .name("CreatePatientID")
			    .description("Create's a new Patient ID")
			    .field(newFieldDefinition()
			    		.name("result")
			            .type(GraphQLString))		    
			   			.build();	
	}

	@Override
	protected List<GraphQLArgument> defineArguments() {
		return Arrays.asList(
				new GraphQLArgument("p_id", Scalars.GraphQLString),
				new GraphQLArgument("user", Scalars.GraphQLString),
				new GraphQLArgument("password", Scalars.GraphQLString)
				);
	}

	@Override
	protected Object fetchData(DataFetchingEnvironment environment) {
		logger.info("Entering CreatePatientID...");
		Object[] queryArgs = new Object[] { environment.getArgument("p_id"), environment.getArgument("user"), environment.getArgument("password") };
		Map<String, String> resultMap = new HashMap<String, String>();
		try {

			PAT_DAO.executeStatement(CREATE_PATIENT_ID_PREPARED_SQL_QUERY, queryArgs);
			resultMap.put("result","New Patient ID created succesfully.");

		} catch (Exception e) {
			logger.error("Exception occurred trying to authenticate p_id", e);
			resultMap.put("result","Exception occurred trying to authenticate p_id"+e);
		}
		logger.info("CreatePatientId Result"+resultMap.get("result"));
		return resultMap;
	}
}
