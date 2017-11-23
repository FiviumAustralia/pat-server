package fivium.pat.servlets;

import fivium.pat.graphql.schema.SuperUserActionsSchema;
import graphql.schema.GraphQLSchema;

public class SuperUserServlet extends BaseServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected GraphQLSchema getGraphQL_SchemaInstance() {
		return SuperUserActionsSchema.getInstance().getSuperUserActionsSchema();
	}

}
