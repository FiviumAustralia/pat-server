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

public class EditPatientQF extends PAT_BaseQF {

	private static final String UPDATE_PATIENT_PREPARED_SQL_QUERY = "UPDATE pat.patient_details SET first_name = ?, last_name = ?, mrn = ?, dob = ?, contact = ?, address = ?, email = ?, next_of_kin_relationship = ?, next_of_kin_first_name = ?, next_of_kin_last_name = ?, next_of_kin_contact_number = ? WHERE study_id = ? AND EXISTS(SELECT * FROM patient WHERE pat.patient_details.study_id = patient.p_id AND company = ?)";
	private static Log logger = LogFactory.getLog(EditPatientQF.class);

	@Override
	protected GraphQLObjectType defineField() {
		return newObject().name("EditPatient").description("Makes a request to the server to edit patient")
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

			Object[] queryArgs = new Object[] { environment.getArgument("patient_first_name"),
					environment.getArgument("patient_last_name"), environment.getArgument("patient_mrn"),
					environment.getArgument("patient_dob"), environment.getArgument("patient_contact"),
					environment.getArgument("patient_address"), environment.getArgument("patient_email"),
					environment.getArgument("patient_next_of_kin_relationship"),
					environment.getArgument("patient_next_of_kin_first_name"),
					environment.getArgument("patient_next_of_kin_last_name"),
					environment.getArgument("patient_next_of_kin_contact_number"),
					environment.getArgument("patient_study_id"),
					clinician_company };
			PAT_DAO.executeStatement(UPDATE_PATIENT_PREPARED_SQL_QUERY, queryArgs);
			resultMap.put("result", "Patient details sucesfully updated.");
		} catch (Exception ex) {
			logger.error("Unexpected error occured", ex);
			resultMap.put("result", "Erorr occured updating patient details");
		}

		return resultMap;
	}

}
