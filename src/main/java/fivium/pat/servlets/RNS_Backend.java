package fivium.pat.servlets;

import javax.servlet.http.HttpServlet;

import fivium.pat.graphql.schema.RNS_GraphQL_SchemaFactory;
import graphql.schema.GraphQLSchema;

/**
 * Servlet implementation class RNS_Backend
 */
@Deprecated
public class RNS_Backend extends BaseServlet {
	
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public RNS_Backend() {
		super();
	}


	@Override
	protected GraphQLSchema getGraphQL_SchemaInstance() {
		return RNS_GraphQL_SchemaFactory.getInstance().getRns_graphQL_Schema();
	}
}
