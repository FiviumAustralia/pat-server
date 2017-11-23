package fivium.pat.graphql.queryfields;

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

import fivium.pat.utils.Constants;
import fivium.pat.utils.PAT_DAO;
import graphql.GraphQLException;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLArgument;
import graphql.schema.GraphQLObjectType;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class AuthenticatePatientQF extends PAT_BaseQF {

	private static Log logger = LogFactory.getLog(AuthenticatePatientQF.class);
	private static final String AUTHENTICATE_PATIENT_PREPARED_SQL_QUERY = "SELECT p_id, company FROM patient WHERE p_id = ? AND Active = \'Not Active\'";
	private static final String GET_TERMS_AND_CONDITIONS_SQL_QUERY = "SELECT Terms_and_Conditions, Permissions FROM company WHERE Company_Name = ?";
	private static final String AUTHENTICATE_PATIENT_PREPARED_SQL_QUERY_UPDATE_TOKEN_SQL_QUERY = "UPDATE patient SET Active=?,Token=? where p_id= ?;";

	@Override
	protected GraphQLObjectType defineField() {

		return newObject().name("AuthenticatePatient").description("Authenticate Patient")
				.field(newFieldDefinition().name("jwt_token").description("The authentication token for the patient")
						.type(GraphQLString))
				.field(newFieldDefinition().name("terms")
						.description("These are the terms and conditions that a patient has to agree to").type(GraphQLString))
				.field(newFieldDefinition().name("permissions").description("The permissions that the company has requested.")
						.type(GraphQLString))
				.field(newFieldDefinition().name("company").description("The company name.")
						.type(GraphQLString))
				.build();
	}

	@Override
	protected List<GraphQLArgument> defineArguments() {
		return Arrays.asList(new GraphQLArgument("p_id", GraphQLString));
	}

	@Override
	protected Object fetchData(DataFetchingEnvironment environment) {
		logger.trace("Entering AuthenticatePatient...");
		Object[] queryArgs = new Object[] {environment.getArgument("p_id")};
		logger.info("Authenticating p_id: "+ environment.getArgument("p_id"));
		Map<String, String> resultMap = new HashMap<String, String>();
		Collection<Map<String, String>> result;
		Collection<Map<String, String>> termsAndConditions;
		try {
			result = PAT_DAO.executeStatement(AUTHENTICATE_PATIENT_PREPARED_SQL_QUERY, queryArgs);
			if(!result.isEmpty()){
				String company = result.iterator().next().get("Company");
				String token = Jwts.builder().setSubject((String) environment.getArgument("p_id"))
						.signWith(SignatureAlgorithm.HS512, Constants.JWT_KEY).compact();
				termsAndConditions = PAT_DAO.executeStatement(GET_TERMS_AND_CONDITIONS_SQL_QUERY, new Object[] {company});
				Map<String, String> terms = termsAndConditions.iterator().next();
				Object[] queryArgs_2 = new Object[] {"Active",token, environment.getArgument("p_id") };
				PAT_DAO.executeStatement(AUTHENTICATE_PATIENT_PREPARED_SQL_QUERY_UPDATE_TOKEN_SQL_QUERY, queryArgs_2);
				logger.info("User authenticated, token generated.");
				resultMap.put("jwt_token", token);
				resultMap.put("terms", terms.get("Terms_and_Conditions"));
				resultMap.put("permissions", terms.get("Permissions"));
				resultMap.put("company", company);
			}
		} catch (Exception e) {
			logger.error("Exception occurred trying to authenticate p_id", e);
			throw new GraphQLException("Supplied user id doesn't exist, please contact your clinician");
		}

		if (result.isEmpty()) {
				logger.debug("Supplied p_id doesn't exist");
			throw new GraphQLException("Supplied user id doesn't exist, please contact your clinician");
		}
		return resultMap;
	}

}
