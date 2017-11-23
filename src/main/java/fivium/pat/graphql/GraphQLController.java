package fivium.pat.graphql;

import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import graphql.ExecutionInput;
import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.schema.GraphQLSchema;

public class GraphQLController {

	private static GraphQLController instance = null;
	private static final Logger log = LoggerFactory.getLogger(GraphQLController.class);

	public Map<String, Object> executeOperation(String query, GraphQLSchema schema, Map<String, Object> variables) {

		GraphQL graphQL = GraphQL.newGraphQL(schema).build();
		ExecutionInput executionInput = ExecutionInput.newExecutionInput().query(query).variables(variables).build();		
		ExecutionResult executionResult = graphQL.execute(executionInput);
		
		if (executionResult.getErrors().size() > 0) {
			Map<String, Object> result = new LinkedHashMap<String, Object>();
			result.put("errors", executionResult.getErrors());
			log.error("Errors: {}", executionResult.getErrors());
			return result;
		} else {
			return (Map<String, Object>) executionResult.getData();
		}
	}

	public static GraphQLController getInstance() {
		if (instance == null) {
			instance = new GraphQLController();
		}
		return instance;
	}
}
