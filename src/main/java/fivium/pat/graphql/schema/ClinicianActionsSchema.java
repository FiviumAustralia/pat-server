package fivium.pat.graphql.schema;

import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static graphql.schema.GraphQLObjectType.newObject;

import fivium.pat.graphql.queryfields.clinician.AddNewRSS_FeedQF;
import fivium.pat.graphql.queryfields.clinician.AddPatientQF;
import fivium.pat.graphql.queryfields.clinician.CliniciansChangePasswordQF;
import fivium.pat.graphql.queryfields.clinician.EditPatientQF;
import fivium.pat.graphql.queryfields.clinician.FetchTrialStartDateQF;
import fivium.pat.graphql.queryfields.clinician.GenerateCSV_QF;
import fivium.pat.graphql.queryfields.clinician.GenerateGraphDataQF;
import fivium.pat.graphql.queryfields.clinician.ListPatientsQF;
import graphql.schema.GraphQLList;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLSchema;

public class ClinicianActionsSchema {

	private static ClinicianActionsSchema instance = null;
	
	private GraphQLSchema clinicianActionsSchema;
	
	public static ClinicianActionsSchema getInstance() {
		if (instance == null) {
			instance = new ClinicianActionsSchema();
		}
		return instance;
	}
	
	public GraphQLSchema getClinicianActionsSchema() {
		return clinicianActionsSchema;
	}

	private ClinicianActionsSchema() {

		AddNewRSS_FeedQF addNewRssFeedQF = new AddNewRSS_FeedQF();
		AddPatientQF addPatientQF = new AddPatientQF();
		CliniciansChangePasswordQF changePassword = new CliniciansChangePasswordQF();
		EditPatientQF editPatient = new EditPatientQF();
		FetchTrialStartDateQF fetchDates = new FetchTrialStartDateQF();
	    GenerateCSV_QF generateCSV_QF = new GenerateCSV_QF();
	    GenerateGraphDataQF graphData = new GenerateGraphDataQF();
	    ListPatientsQF listPatientsQF = new ListPatientsQF();

		GraphQLObjectType rootObjectType = newObject().name("userQueries")				
	            .field(newFieldDefinition()
	            		.type(addNewRssFeedQF.getField())
	            		.name("AddNewRSS_Feed")
	            		.dataFetcher(addNewRssFeedQF)
	            		.argument(addNewRssFeedQF.getArguments()))            
	            .field(newFieldDefinition()
	                    .type(addPatientQF.getField())
	                    .name("AddPatient")
	                    .dataFetcher(addPatientQF)
	                    .argument(addPatientQF.getArguments()))
	            .field(newFieldDefinition()
	            		.type(changePassword.getField())
	            		.name("ChangePassword")
	            		.dataFetcher(changePassword)
	            		.argument(changePassword.getArguments()))
	            .field(newFieldDefinition()
	                    .type(editPatient.getField())
	                    .name("EditPatient")
	                    .dataFetcher(editPatient)
	                    .argument(editPatient.getArguments()))
	            .field(newFieldDefinition()
	            		.type(fetchDates.getField() )
	            		.name("FetchDates")
	            		.dataFetcher(fetchDates)
	            		.argument(fetchDates.getArguments()))  
	            .field(newFieldDefinition()
	            		.type(generateCSV_QF.getField() )
	            		.name("GenerateCSV")
	            		.dataFetcher(generateCSV_QF)
	            		.argument(generateCSV_QF.getArguments()))
	            .field(newFieldDefinition()
	            		.type(graphData.getField() )
	            		.name("GraphData")
	            		.dataFetcher(graphData)
	            		.argument(graphData.getArguments()))
	            .field(newFieldDefinition()
	                    .type(new GraphQLList(listPatientsQF.getField()))
	                    .name("ListPatients")
	                    .dataFetcher(listPatientsQF)
	                    .argument(listPatientsQF.getArguments()))
				.build();
		clinicianActionsSchema = new GraphQLSchema(rootObjectType);
	}

	
}
