package fivium.pat.graphql.queryfields.clinician;

import static fivium.pat.utils.Constants.JWT_GRAPHQL_QUERY_PARAM;
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
import org.mindrot.jbcrypt.BCrypt;

import fivium.pat.graphql.PAT_BaseQF;
import fivium.pat.utils.PAT_DAO;
import fivium.pat.utils.PatUtils;
import graphql.Scalars;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLArgument;
import graphql.schema.GraphQLObjectType;

public class CliniciansChangePasswordQF extends PAT_BaseQF {

	private static final String CLINICIAN_CHANGE_PASSWORD_PREPARED_SQL_QUERY = "UPDATE pat.clinicians SET Password = ? WHERE Email=?;";
	private static final String GET_EXISTING_PASSWORD = "SELECT Password FROM pat.clinicians WHERE Email=?;";

	private static Log logger = LogFactory.getLog(CliniciansChangePasswordQF.class);

	@Override
	protected GraphQLObjectType defineField() {
		return newObject().name("ChangePassword")
				.description("Makes a request to the internal server to change the clinicians password")
				.field(newFieldDefinition().name("result").type(GraphQLString)).build();
	}

	@Override
	protected List<GraphQLArgument> defineArguments() {
		return Arrays.asList(
				new GraphQLArgument("clinician_current_password", Scalars.GraphQLString),
				new GraphQLArgument("clinician_new_password", Scalars.GraphQLString));
	}

	@Override
	protected Object fetchData(DataFetchingEnvironment environment) {
		Map<String, String> resultMap = new HashMap<String, String>();
		try {

			// Extract vars from graphql query
			String clinicianEmail = PatUtils.getUserIdFromJWT((String) environment.getArgument(JWT_GRAPHQL_QUERY_PARAM));
			String currentClinicianPassword = environment.getArgument("clinician_current_password");
			String newClinicianPassword = environment.getArgument("clinician_new_password");
			
			// Get existing pw for given clinician
			Collection<Map<String, String>> sqlResult = PAT_DAO.executeStatement(GET_EXISTING_PASSWORD, new Object[] { clinicianEmail });
			if (sqlResult.isEmpty()) {
				logger.error("No rows returned for " + clinicianEmail);
				resultMap.put("result", "Invalid Credentials ");
				return resultMap;
			}
			
			// Check supplied old pw matches pw in DB
			String existingHashedPasswordInDB = sqlResult.iterator().next().get("Password");
			if (!BCrypt.checkpw(currentClinicianPassword, existingHashedPasswordInDB)) {
				logger.error("Passwords don't match!");
				resultMap.put("result", "Invalid Credentials");
				return resultMap;
			}

			// set new hashed pw in db for clincian
			String newClinicianPasswordHashed = BCrypt.hashpw(newClinicianPassword, BCrypt.gensalt());
			sqlResult = PAT_DAO.executeStatement(CLINICIAN_CHANGE_PASSWORD_PREPARED_SQL_QUERY,
					new Object[] { newClinicianPasswordHashed, clinicianEmail });
			resultMap.put("result", "Password changed successfully");

		} catch (Exception ex) {
			logger.error("Unexpected error occured", ex);
			resultMap.put("result", "Invalid Credentials");
		}
		return resultMap;
	}
}
