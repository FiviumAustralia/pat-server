package fivium.pat.graphql.queryfields.patient;

import static graphql.Scalars.GraphQLString;
import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static graphql.schema.GraphQLObjectType.newObject;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import fivium.pat.graphql.PAT_BaseQF;
import fivium.pat.utils.PAT_DAO;
import graphql.GraphQLException;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLArgument;
import graphql.schema.GraphQLObjectType;

public class RetrieveRSS_ListQF extends PAT_BaseQF {

	private static Log logger = LogFactory.getLog(RetrieveRSS_ListQF.class);

	private static final String LIST_RSS_FEEDS_SQL_QUERY= "SELECT * from rss_feeds";
	
	@Override
	protected GraphQLObjectType defineField() {
	    return newObject()
	            .name("RetrieveRSS_List")
	            .description("Retrieve a list of the RSS feeds the user can subscribe to.")
	            .field(newFieldDefinition()
	                    .name("title")
	                    .description("The title of the feed")
	                    .type(GraphQLString))
	            .field(newFieldDefinition()
	                    .name("url")
	                    .description("The url of the feed")
	                    .type(GraphQLString))
	            .field(newFieldDefinition()
	                    .name("color")
	                    .description("The color of the feed")
	                    .type(GraphQLString))
	            .build();
	}

	protected List<GraphQLArgument> defineArguments() {
		return Arrays.asList();
	}

	@Override
	protected Object fetchData(DataFetchingEnvironment environment) {
		Collection<Map<String, String>> result;

	    try {
			result = PAT_DAO.executeStatement(LIST_RSS_FEEDS_SQL_QUERY, new Object[]{});			
	    } catch (Exception e) {
	    	logger.error("Unexpected error occured", e);
	        throw new GraphQLException("Unexpected error fetching list of RSS Feeds", e);
	    }

	    if (result.isEmpty()) {
	      throw new GraphQLException("No feeds to return.");
	    }
	    
	    return result;
	}

}
