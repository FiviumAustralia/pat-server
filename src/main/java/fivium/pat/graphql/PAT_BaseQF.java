package fivium.pat.graphql;

import static graphql.Scalars.GraphQLString;

import java.util.ArrayList;
import java.util.List;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLArgument;
import graphql.schema.GraphQLObjectType;

public abstract class PAT_BaseQF implements DataFetcher {

	protected GraphQLObjectType field;

	protected List<GraphQLArgument> arguments;

	protected abstract GraphQLObjectType defineField();

	protected abstract List<GraphQLArgument> defineArguments();

	protected abstract Object fetchData(DataFetchingEnvironment environment);

	protected PAT_BaseQF() {
		field = defineField();

		arguments = new ArrayList<GraphQLArgument>();
		arguments.addAll(defineArguments());
		arguments.add(new GraphQLArgument("jwt_token", GraphQLString));
	}

	public GraphQLObjectType getField() {
		return field;
	}
	
	public List<GraphQLArgument> getArguments() {
		return arguments;
	}

	public Object get(DataFetchingEnvironment environment) {
			return fetchData(environment);
	}
}
