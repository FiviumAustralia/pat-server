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
import fivium.pat.utils.Constants;
import fivium.pat.utils.PAT_DAO;
import graphql.GraphQLException;
import graphql.Scalars;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLArgument;
import graphql.schema.GraphQLObjectType;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;

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
		return Arrays.asList(new GraphQLArgument("jwt", Scalars.GraphQLString),
				new GraphQLArgument("clinician_current_password", Scalars.GraphQLString),
				new GraphQLArgument("clinician_new_password", Scalars.GraphQLString));
	}

	@Override
	protected Object fetchData(DataFetchingEnvironment environment) {
		Map<String, String> resultMap = new HashMap<String, String>();
		try {
			Object[] queryArgs = new Object[] { environment.getArgument("clinician_new_password"),
					environment.getArgument("jwt"), environment.getArgument("clinician_current_password"), };

			Jws<Claims> claims = Jwts.parser().setSigningKey(Constants.JWT_KEY).parseClaimsJws(queryArgs[1].toString());
			Claims claimsBody = claims.getBody();
			String subject = claimsBody.getSubject();
			queryArgs[1] = subject;
			Collection<Map<String, String>> sqlResult;
			Object[] queryArgsExistingPassword = new Object[] { subject };
			try {
				sqlResult = PAT_DAO.executeStatement(GET_EXISTING_PASSWORD, queryArgsExistingPassword);
				if (!sqlResult.isEmpty()) {
					Map<String, String> aMap = new HashMap<String, String>();
					aMap = sqlResult.iterator().next();
					String password = aMap.get("Password");
					if (BCrypt.checkpw(queryArgs[2].toString(), password)) {
						String hashed = BCrypt.hashpw(queryArgs[0].toString(), BCrypt.gensalt());
						sqlResult = PAT_DAO.executeStatement(CLINICIAN_CHANGE_PASSWORD_PREPARED_SQL_QUERY,
								new Object[] { hashed, subject });
						resultMap.put("result", "Password changed successfully");
					} else {
						resultMap.put("result", "Invalid Credentials ");
					}

				} else {
					resultMap.put("result", "Invalid Credentials ");
				}
			} catch (Exception e) {
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
