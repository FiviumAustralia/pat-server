package fivium.pat.graphql;

import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static graphql.schema.GraphQLObjectType.newObject;

import fivium.pat.graphql.queryfields.AuthenticatePatientQF;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLSchema;

@Deprecated
public class GraphQL_LoginSchemaFactory {

	private static GraphQL_LoginSchemaFactory instance = null;

	private GraphQLSchema rns_login_graphQL_Schema;

	private GraphQL_LoginSchemaFactory() {
		AuthenticatePatientQF authenticatePatientQF = new AuthenticatePatientQF();

		GraphQLObjectType rootObjectType = newObject()
				.name("loginQueries")
				.field(newFieldDefinition()
						.type(authenticatePatientQF.getField())
						.name("AuthenticatePatient")
						.dataFetcher(authenticatePatientQF)
						.argument(authenticatePatientQF.getArguments()))
				.build();
		
		rns_login_graphQL_Schema = new GraphQLSchema(rootObjectType);
		
	}
	
	public static GraphQL_LoginSchemaFactory getInstance(){
		if(instance == null){
			instance = new GraphQL_LoginSchemaFactory();
			return instance;
		}
		return instance;
	}

	public GraphQLSchema getRns_login_graphQL_Schema() {
		return rns_login_graphQL_Schema;
	}
}
