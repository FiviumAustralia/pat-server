package fivium.pat.graphql.queryfields.clinician;

import static graphql.Scalars.GraphQLString;
import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static graphql.schema.GraphQLObjectType.newObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import fivium.pat.graphql.PAT_BaseQF;
import fivium.pat.utils.PAT_DAO;
import graphql.GraphQLException;
import graphql.GraphqlErrorHelper;
import graphql.Scalars;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLArgument;
import graphql.schema.GraphQLObjectType;

public class ListPatientsQF extends PAT_BaseQF {

	private static final String LIST_PATIENTS_PREPARED_SQL_BASE_QUERY  =
	"SELECT p.*, lateststepsync.last_steps_sync_date, latestweightsync.last_weight_sync_date, latestsurveysync.last_survey_sync_date "+
	"FROM patient_details p "+
	"LEFT JOIN "+
	"(SELECT p_id as study_id, max(date) last_steps_sync_date "+
	"FROM fitness_data "+
	"GROUP BY p_id) lateststepsync "+
	"ON p.study_id = lateststepsync.study_id "+
	"LEFT JOIN "+
	"(SELECT study_id, max(date) last_weight_sync_date "+
	"FROM weightdata "+
	"GROUP BY study_id) latestweightsync "+
	"ON p.study_id = latestweightsync.study_id "+
	"LEFT JOIN "+
	"(SELECT study_id, max(date) last_survey_sync_date "+
	"FROM surveydata "+
	"GROUP BY study_id) latestsurveysync "+
	"ON p.study_id = latestsurveysync.study_id ";
	
	private static final String LIST_PATIENT_PREPARED_SQL_WHERE_CLAUSE = " WHERE p.study_id = ?";
	
	private static Log logger = LogFactory.getLog(ListPatientsQF.class);
	
	@Override
	protected GraphQLObjectType defineField() {
		return newObject()
				.name("ListPatients")
				.description("List Patients")
				.field(newFieldDefinition()
						.name("first_name")
						.description("The authentication token for the clinician")
						.type(GraphQLString))
				.field(newFieldDefinition()
						.name("last_name")
						.description("The authentication token for the clinician")
						.type(GraphQLString))
				.field(newFieldDefinition()
						.name("study_id")
						.description("The id assigned to the patient for this study")
						.type(GraphQLString))
				.field(newFieldDefinition()
						.name("mrn")
						.description("The mrn")
						.type(GraphQLString))
				.field(newFieldDefinition()
						.name("dob")
						.description("The dob of the patient")
						.type(GraphQLString))
				.field(newFieldDefinition()
						.name("contact")
						.description("The contact details the patient for this study")
						.type(GraphQLString))
				.field(newFieldDefinition()
						.name("email")
						.description("The email address of the patient for this study")
						.type(GraphQLString))
				.field(newFieldDefinition()
						.name("address")
						.description("The address of the patient for this study")
						.type(GraphQLString))
				.field(newFieldDefinition()
						.name("next_of_kin_relationship")
						.description("The next of kin for the patient for this study")
						.type(GraphQLString))
				.field(newFieldDefinition()
						.name("next_of_kin_first_name")
						.description("The next of kin first name of the patient for this study")
						.type(GraphQLString))
				.field(newFieldDefinition()
						.name("next_of_kin_last_name")
						.description("The next of kin last name of the patient for this study")
						.type(GraphQLString))
				.field(newFieldDefinition()
						.name("next_of_kin_contact_number")
						.description("The next of kin contact the patient for this study")
						.type(GraphQLString))
				.field(newFieldDefinition()
						.name("date_created")
						.description("The date patient was created")
						.type(GraphQLString))
				.field(newFieldDefinition()
						.name("last_steps_sync_date")
						.description("The latest sync date made by the patient")
						.type(GraphQLString))
				.field(newFieldDefinition()
						.name("last_weight_sync_date")
						.description("The latest sync date made by the patient")
						.type(GraphQLString))
				.field(newFieldDefinition()
						.name("last_survey_sync_date")
						.description("The latest sync date made by the patient")
						.type(GraphQLString))
				.build();
	}

	@Override
	protected List<GraphQLArgument> defineArguments() {
		return Arrays.asList(
				new GraphQLArgument("study_id", Scalars.GraphQLString)
				);
	}

	@Override
	protected Object fetchData(DataFetchingEnvironment environment) {
		Object[] queryArgs = new Object[] {
					environment.getArgument("study_id")
				};

		Collection<Map<String, String>> result = new ArrayList<Map<String, String>>();
		try {
			// Add the WHERE claws to the SQL statement if the study ID param is supplied
			String sql = (queryArgs.length > 0 && queryArgs[0] != null ) ?
					LIST_PATIENTS_PREPARED_SQL_BASE_QUERY + LIST_PATIENT_PREPARED_SQL_WHERE_CLAUSE :
					LIST_PATIENTS_PREPARED_SQL_BASE_QUERY;
			
			result = PAT_DAO.executeStatement(sql, queryArgs);

			if (result.isEmpty()) {
				logger.error("No Patients Found.");
			}
			
		} catch (Exception e) {
			logger.error("Unexpected error occured", e);
		}


		return result;
	}
	

}
