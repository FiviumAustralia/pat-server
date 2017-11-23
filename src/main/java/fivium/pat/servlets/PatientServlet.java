package fivium.pat.servlets;

import fivium.pat.graphql.schema.PatientActionsSchema;
import graphql.schema.GraphQLSchema;

public class PatientServlet extends BaseServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected GraphQLSchema getGraphQL_SchemaInstance() {
		return PatientActionsSchema.getInstance().getPatientActionsSchema();
	}

}
