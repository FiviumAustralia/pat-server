package fivium.pat.graphql.schema;

import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static graphql.schema.GraphQLObjectType.newObject;

import fivium.pat.graphql.queryfields.patient.StoreProviderTokenQF;
import fivium.pat.graphql.queryfields.patient.RetrieveCharitableCompanyList_QF;
import fivium.pat.graphql.queryfields.patient.RetrieveProviderDataQF;
import fivium.pat.graphql.queryfields.patient.RetrieveRSS_ListQF;
import fivium.pat.graphql.queryfields.patient.UpdatePatientFirebaseTokenQF;
import graphql.schema.GraphQLList;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLSchema;

public class PatientActionsSchema {

	private static PatientActionsSchema instance = null;
	
	private GraphQLSchema patientActionsSchema;
	
	public static PatientActionsSchema getInstance() {
		if (instance == null) {
			instance = new PatientActionsSchema();
		}
		return instance;
	}
	
	public GraphQLSchema getPatientActionsSchema() {
		return patientActionsSchema;
	}

	private PatientActionsSchema() {

		RetrieveCharitableCompanyList_QF companyList_QF = new RetrieveCharitableCompanyList_QF();
		StoreProviderTokenQF saveProviderTokenQF = new StoreProviderTokenQF();
		RetrieveProviderDataQF providerData = new RetrieveProviderDataQF();
		RetrieveRSS_ListQF rssListQF = new RetrieveRSS_ListQF();
		UpdatePatientFirebaseTokenQF updatePatientFirebaseTokenQF = new UpdatePatientFirebaseTokenQF();

		GraphQLObjectType rootObjectType = newObject().name("userQueries")
				.field(newFieldDefinition().type(companyList_QF.getField()).name("RetrieveChariatbleCompanies")
						.dataFetcher(companyList_QF).argument(companyList_QF.getArguments()))
				.field(newFieldDefinition().type(saveProviderTokenQF.getField()).name("SaveProviderToken")
						.dataFetcher(saveProviderTokenQF).argument(saveProviderTokenQF.getArguments()))
				.field(newFieldDefinition().type(providerData.getField()).name("RetrieveProviderData")
						.dataFetcher(providerData).argument(providerData.getArguments()))
	            .field(newFieldDefinition()
	                    .type(new GraphQLList(rssListQF.getField())).name("RetrieveRSS_List")
	                    .dataFetcher(rssListQF).argument(rssListQF.getArguments()))
	            .field(newFieldDefinition().type(updatePatientFirebaseTokenQF.getField()).name("UpdatePatientFirebaseToken")
	            		.dataFetcher(updatePatientFirebaseTokenQF).argument(updatePatientFirebaseTokenQF.getArguments()))
				.build();
		
		patientActionsSchema = new GraphQLSchema(rootObjectType);
	}
	
}
