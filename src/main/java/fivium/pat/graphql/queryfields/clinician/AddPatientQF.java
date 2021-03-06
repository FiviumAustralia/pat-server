package fivium.pat.graphql.queryfields.clinician;

import static fivium.pat.utils.Constants.*;
import static graphql.Scalars.GraphQLString;
import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static graphql.schema.GraphQLObjectType.newObject;

import java.util.*;

import fivium.pat.utils.PatUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import fivium.pat.graphql.PAT_BaseQF;
import fivium.pat.utils.PAT_DAO;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLArgument;
import graphql.schema.GraphQLObjectType;

public class AddPatientQF extends PAT_BaseQF {

	private static final String ADD_PATIENT_DETAILS_PREPARED_SQL_QUERY = "INSERT INTO pat.patient_details (study_id, first_name, last_name, mrn, dob, contact, email, address, next_of_kin_relationship, next_of_kin_first_name, next_of_kin_last_name, next_of_kin_contact_number) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";
	private static final String ADD_PATIENT_PREPARED_SQL_QUERY = "INSERT INTO pat.patient (p_id, Token, provider_user_id, provider_refresh_token, provider_permissions, provider, Active, company, firebase_device_token) VALUES (?,?,?,?,?,?,?,?,?) ";

	private static Log logger = LogFactory.getLog(AddPatientQF.class);

	@Override
	protected GraphQLObjectType defineField() {
		return newObject().name("AddPatient")
				.description("Makes a request to the intermediate server to create a new patient")
				.field(newFieldDefinition().name("result").type(GraphQLString)).build();
	}

	@Override
	protected List<GraphQLArgument> defineArguments() {
		return Arrays.asList(
				new GraphQLArgument("patient_first_name", GraphQLString),
				new GraphQLArgument("patient_last_name", GraphQLString),
				new GraphQLArgument("patient_study_id", GraphQLString),
				new GraphQLArgument("patient_mrn", GraphQLString),
				new GraphQLArgument("patient_dob", GraphQLString),
				new GraphQLArgument("patient_contact", GraphQLString),
				new GraphQLArgument("patient_email", GraphQLString),
				new GraphQLArgument("patient_address", GraphQLString),
				new GraphQLArgument("patient_next_of_kin_relationship", GraphQLString),
				new GraphQLArgument("patient_next_of_kin_first_name", GraphQLString),
				new GraphQLArgument("patient_next_of_kin_last_name", GraphQLString),
				new GraphQLArgument("patient_next_of_kin_contact_number", GraphQLString));
	}

	@Override
	protected Object fetchData(DataFetchingEnvironment environment) {

		Map<String, String> resultMap = new HashMap<String, String>();

		try {
			String clinician_id = PatUtils.getUserIdFromJWT(environment.getArgument(JWT_GRAPHQL_QUERY_PARAM).toString());
			Collection<Map<String, String>> company_result = PAT_DAO.executeFetchStatement(GET_CLINICIAN_COMPANY, new Object[] { clinician_id });
			String clinician_company = company_result.iterator().next().get("Company");
			Object[] queryArgsNewPatient = new Object[] { environment.getArgument("patient_study_id"), "", "", "", "", "","Not Active", clinician_company, "" };

			Object[] queryArgsNewPatientDetails = new Object[] { 
					environment.getArgument("patient_study_id"),                    // #1 study_id
					environment.getArgument("patient_first_name"),                  // #2 first_name
					environment.getArgument("patient_last_name"),                   // #3 last_name
					environment.getArgument("patient_mrn"),                         // #4 mrn
					environment.getArgument("patient_dob"),                         // #5 dob
					environment.getArgument("patient_contact"),                     // #6 contact
					environment.getArgument("patient_email"),                       // #7 email
					environment.getArgument("patient_address"),                     // #8 address
					environment.getArgument("patient_next_of_kin_relationship"),    // #9 next_of_kin_relationship
					environment.getArgument("patient_next_of_kin_first_name"),      //#10 next_of_kin_first_name
					environment.getArgument("patient_next_of_kin_last_name"),       //#11 next_of_kin_last_name
					environment.getArgument("patient_next_of_kin_contact_number"),  //#12 next_of_kin_contact_number
					};

			// insert new patient details into internal db
			PAT_DAO.executeStatement(ADD_PATIENT_DETAILS_PREPARED_SQL_QUERY, queryArgsNewPatientDetails);
			PAT_DAO.executeStatement(ADD_PATIENT_PREPARED_SQL_QUERY, queryArgsNewPatient);

			resultMap.put("result", "Patient Created Sucesfully.");

		} catch (Exception ex) {
			logger.error("Unexpected exception occured", ex);
		}

		return resultMap;
	}
}
