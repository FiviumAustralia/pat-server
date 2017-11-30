package fivium.pat.graphql.queryfields.superuser;

import static graphql.Scalars.GraphQLString;
import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static graphql.schema.GraphQLObjectType.newObject;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mindrot.jbcrypt.BCrypt;

import fivium.pat.graphql.PAT_BaseQF;
import fivium.pat.utils.PAT_DAO;
import graphql.GraphQLException;
import graphql.Scalars;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLArgument;
import graphql.schema.GraphQLObjectType;

public class AddCliniciansQF extends PAT_BaseQF {

	private static final String ADD_CLINICIAN_PREPARED_SQL_QUERY = "INSERT INTO clinicians (Email, Password, Firstname, Lastname, Token, Role) VALUES (?,?,?,?,?,?)";
	
	private static Log logger = LogFactory.getLog(AddCliniciansQF.class);
	
	@Override
	protected GraphQLObjectType defineField() {
		return newObject()
			    .name("AddClinicians")
			    .description("Makes a request to the internal server to create a new clinician")
			    .field(newFieldDefinition()
			    		.name("result")
			            .type(GraphQLString))		    
			   .build();	
	}

	@Override
	protected List<GraphQLArgument> defineArguments() {
		return Arrays.asList(
				new GraphQLArgument("clinician_email", Scalars.GraphQLString),
				new GraphQLArgument("clinician_password", Scalars.GraphQLString),
				new GraphQLArgument("clinician_first_name", Scalars.GraphQLString),
				new GraphQLArgument("clinician_last_name", Scalars.GraphQLString)
				);
	}

	@Override
	protected Object fetchData(DataFetchingEnvironment environment)  {
		Map<String, String> resultMap = new HashMap<String, String>();
		try {
			Object[] queryArgs = new Object[] {
					environment.getArgument("clinician_email"),
					environment.getArgument("clinician_password"),
					environment.getArgument("clinician_first_name"),
					environment.getArgument("clinician_last_name"),
					0,
					"user"
			};
			Collection<Map<String, String>> sqlResult;
			String hashed = BCrypt.hashpw(queryArgs[1].toString(), BCrypt.gensalt());
			queryArgs[1] = hashed;
			try {
				sqlResult = PAT_DAO.executeStatement(ADD_CLINICIAN_PREPARED_SQL_QUERY, queryArgs);
				if(sqlResult.isEmpty()){
					resultMap.put("result", "Clinician added successfully");
				}
				else {
					resultMap.put("result", "ID already exists");
				}
			} catch (Exception e) {
				logger.error("Unexpected execution error", e);
				throw new GraphQLException("Unexpected execution error", e);
			}
		} catch (Exception ex) {
			logger.error("Unexpected error", ex);
			resultMap.put("result", ex.getMessage());
		}
		
		return resultMap;
	}
	


}
