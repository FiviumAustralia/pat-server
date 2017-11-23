package fivium.pat.graphql.schema;

import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static graphql.schema.GraphQLObjectType.newObject;

import fivium.pat.graphql.queryfields.RetrieveDataQF;
import fivium.pat.graphql.queryfields.StoreDataQF;
import fivium.pat.graphql.queryfields.clinician.AddNewRSS_FeedQF;
import fivium.pat.graphql.queryfields.clinician.CreatePatientID_QF;
import fivium.pat.graphql.queryfields.patient.ProviderTokenQF;
import fivium.pat.graphql.queryfields.patient.RetrieveCharitableCompanyList_QF;
import fivium.pat.graphql.queryfields.patient.RetrieveProviderDataQF;
import fivium.pat.graphql.queryfields.patient.RetrieveRSS_ListQF;
import fivium.pat.graphql.queryfields.patient.UpdatePatientFirebaseTokenQF;
import fivium.pat.graphql.queryfields.superuser.CreateCompany_QF;
import graphql.schema.GraphQLList;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLSchema;

@Deprecated
public class RNS_GraphQL_SchemaFactory {
	private static RNS_GraphQL_SchemaFactory instance = null;
	private GraphQLSchema rns_graphQL_Schema;

	private RNS_GraphQL_SchemaFactory() {

		StoreDataQF storeDataQF = new StoreDataQF();
		RetrieveDataQF retrieveDataQF = new RetrieveDataQF();
		CreatePatientID_QF createPatientID_QF = new CreatePatientID_QF();
		CreateCompany_QF createCompany_QF = new CreateCompany_QF();
		RetrieveCharitableCompanyList_QF companyList_QF = new RetrieveCharitableCompanyList_QF();
		ProviderTokenQF saveProviderTokenQF = new ProviderTokenQF();
		RetrieveProviderDataQF providerData = new RetrieveProviderDataQF();
		RetrieveRSS_ListQF rssListQF = new RetrieveRSS_ListQF();
		UpdatePatientFirebaseTokenQF updatePatientFirebaseTokenQF = new UpdatePatientFirebaseTokenQF();
		AddNewRSS_FeedQF addNewRssFeedQF = new AddNewRSS_FeedQF();

		GraphQLObjectType rootObjectType = newObject().name("userQueries")
				.field(newFieldDefinition().type(storeDataQF.getField()).name("StoreData").dataFetcher(storeDataQF)
						.argument(storeDataQF.getArguments()))
				.field(newFieldDefinition().type(retrieveDataQF.getField()).name("RetrieveData").dataFetcher(retrieveDataQF)
						.argument(retrieveDataQF.getArguments()))
				.field(newFieldDefinition().type(createPatientID_QF.getField()).name("CreatePatientID")
						.dataFetcher(createPatientID_QF).argument(createPatientID_QF.getArguments()))
				.field(newFieldDefinition().type(createCompany_QF.getField()).name("CreateCompany")
						.dataFetcher(createCompany_QF).argument(createCompany_QF.getArguments()))
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
	            .field(newFieldDefinition().type(addNewRssFeedQF.getField()).name("AddNewRSS_Feed")
	            		.dataFetcher(addNewRssFeedQF).argument(addNewRssFeedQF.getArguments()))
				.build();
		rns_graphQL_Schema = new GraphQLSchema(rootObjectType);
	}

	public static RNS_GraphQL_SchemaFactory getInstance() {
		if (instance == null) {
			instance = new RNS_GraphQL_SchemaFactory();
		}
		return instance;
	}

	public GraphQLSchema getRns_graphQL_Schema() {
		return rns_graphQL_Schema;
	}
}
