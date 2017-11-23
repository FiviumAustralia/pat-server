package fivium.pat.servlets;

import fivium.pat.graphql.schema.ClinicianActionsSchema;
import graphql.schema.GraphQLSchema;

public class CliniciansServlet extends BaseServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected GraphQLSchema getGraphQL_SchemaInstance() {
		return ClinicianActionsSchema.getInstance().getClinicianActionsSchema();
	}

}
