package fivium.pat.graphql.schema;

import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static graphql.schema.GraphQLObjectType.newObject;

import fivium.pat.graphql.queryfields.superuser.AddCliniciansQF;
import fivium.pat.graphql.queryfields.superuser.CreateCompany_QF;
import fivium.pat.graphql.queryfields.superuser.DeleteClinicianQF;
import fivium.pat.graphql.queryfields.superuser.ListCliniciansQF;
import graphql.schema.GraphQLList;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLSchema;

public class SuperUserActionsSchema {

	private static SuperUserActionsSchema instance = null;

	private GraphQLSchema superUserActionsSchema;

	public static SuperUserActionsSchema getInstance() {
		if (instance == null) {
			instance = new SuperUserActionsSchema();
		}
		return instance;
	}

	public GraphQLSchema getSuperUserActionsSchema() {
		return superUserActionsSchema;
	}

	private SuperUserActionsSchema() {

		CreateCompany_QF createCompany_QF = new CreateCompany_QF();
	    DeleteClinicianQF deleteClinician = new DeleteClinicianQF();
	    ListCliniciansQF listCliniciansQF = new ListCliniciansQF();
	    AddCliniciansQF addClincianQF = new AddCliniciansQF();

		GraphQLObjectType rootObjectType = newObject().name("userQueries")
				.field(newFieldDefinition().type(createCompany_QF.getField()).name("CreateCompany")
						.dataFetcher(createCompany_QF).argument(createCompany_QF.getArguments()))
				
	            .field(newFieldDefinition()
	                    .type(addClincianQF.getField())
	                    .name("AddClinician")
	                    .dataFetcher(addClincianQF)
	                    .argument(addClincianQF.getArguments())
	            )
	            .field(newFieldDefinition()
	                    .type(deleteClinician.getField())
	                    .name("DeleteClinician")
	                    .dataFetcher(deleteClinician)
	                    .argument(deleteClinician.getArguments())
	            )
	            .field(newFieldDefinition()
	                    .type(new GraphQLList(listCliniciansQF.getField()))
	                    .name("ListClinicians")
	                    .dataFetcher(listCliniciansQF)
	                    .argument(listCliniciansQF.getArguments())
	            )
				
				.build();

		superUserActionsSchema = new GraphQLSchema(rootObjectType);
	}

}
