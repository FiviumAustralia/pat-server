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
import org.mindrot.jbcrypt.BCrypt;

import fivium.pat.graphql.queryfields.PAT_BaseQF;
import fivium.pat.utils.PAT_DAO;
import fivium.pat.utils.LegacyInternalServerUtils;
import graphql.GraphQLException;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLArgument;
import graphql.schema.GraphQLObjectType;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class AuthenticateClinicianQF extends PAT_BaseQF {

	private static final String AUTHENTICATE_PATIENT_PREPARED_SQL_QUERY = "SELECT Firstname, Lastname, Password, Role FROM clinicians WHERE Email = ?";
	private static final String AUTHENTICATE_PATIENT_PREPARED_SQL_QUERY_UPDATE_TOKEN = "UPDATE clinicians SET Token=? where Email= ?;";

	private static Log logger = LogFactory.getLog(AuthenticateClinicianQF.class);
	
	@Override
	protected GraphQLObjectType defineField() {

		return newObject().name("AuthenticateClinician").description("Authenticate Clinician")
				.field(newFieldDefinition()
						.name("Firstname")
						.description("The first name of the clinician authenticated")
						.type(GraphQLString))
				.field(newFieldDefinition()
						.name("Lastname")
						.description("The last name of the clinician authenticated")
						.type(GraphQLString))
				.field(newFieldDefinition()
						.name("jwt_token")
						.description("The authentication token for the clinician")
						.type(GraphQLString))
				.field(newFieldDefinition()
						.name("Role")
						.description("The role of the user logging in")
						.type(GraphQLString))
				.build();
	}

	@Override
	protected List<GraphQLArgument> defineArguments() {
		return Arrays.asList(new GraphQLArgument("Email", GraphQLString),
				new GraphQLArgument("Password", GraphQLString));
	}

	@Override
	protected Object fetchData(DataFetchingEnvironment environment) {
		Object[] queryArgs = new Object[] {
				environment.getArgument("Email"),
				environment.getArgument("Password")};

		Collection<Map<String, String>> result;
		Collection<Map<String, String>> updateTokenResult;
		Map<String, String> resultMap = new HashMap<String, String>();
		try {
			result = PAT_DAO.executeStatement(AUTHENTICATE_PATIENT_PREPARED_SQL_QUERY, queryArgs);
			if (!result.isEmpty()) {
			 Map<String, String> aMap = result.iterator().next();
				String password = aMap.get("Password");
				String role = aMap.get("Role");
				String firstname = aMap.get("Firstname");
				String lastname = aMap.get("Lastname");
				if (BCrypt.checkpw(queryArgs[1].toString(), password)) {
					String token = Jwts.builder().setSubject((String) environment.getArgument("Email"))
							.signWith(SignatureAlgorithm.HS512, LegacyInternalServerUtils.JWT_KEY).compact();
					resultMap.put("Firstname", firstname);
					resultMap.put("Lastname", lastname);
					resultMap.put("jwt_token", token);
					resultMap.put("Role", role);
					Object[] queryArgsUpdate = new Object[] { token, environment.getArgument("Email") };
					updateTokenResult = PAT_DAO.executeStatement(AUTHENTICATE_PATIENT_PREPARED_SQL_QUERY_UPDATE_TOKEN,
							queryArgsUpdate);
					return resultMap;
				} else {
					return resultMap.put("jwt_token", "Invalid Credentials");
				}
			} else {
				return resultMap.put("jwt_token", "Invalid Credentials");
			}
		} catch (Exception e) {
			logger.error("unexpected error occured", e);
			throw new GraphQLException("Unexpected execution error", e);
		}
	}

}
