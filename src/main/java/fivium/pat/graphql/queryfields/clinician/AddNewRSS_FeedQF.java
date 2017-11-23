package fivium.pat.graphql.queryfields.clinician;

import static graphql.Scalars.GraphQLString;
import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static graphql.schema.GraphQLObjectType.newObject;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import fivium.pat.graphql.queryfields.PAT_BaseQF;
import fivium.pat.utils.PAT_DAO;
import graphql.GraphQLException;
import graphql.Scalars;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLArgument;
import graphql.schema.GraphQLObjectType;

public class AddNewRSS_FeedQF extends PAT_BaseQF {

	private static Log logger = LogFactory.getLog(AddNewRSS_FeedQF.class);

	private static final String ADD_NEW_RSS_FEED_SQL_QUERY= "INSERT INTO rss_feeds (title, url, color) VALUES (?,?,?)";
	
	@Override
	protected GraphQLObjectType defineField() {
	    return newObject()
	            .name("AddNewRSS_Feed")
	            .description("Adds a new entry to the RSS feeds list")
			    .field(newFieldDefinition()
			    		.name("result")
			            .type(GraphQLString))		    
			   			.build();	
	}

	protected List<GraphQLArgument> defineArguments() {
		return Arrays.asList(
				new GraphQLArgument("title", Scalars.GraphQLString),
				new GraphQLArgument("url", Scalars.GraphQLString),
				new GraphQLArgument("color", Scalars.GraphQLString)
		);
	}

	@Override
	protected Object fetchData(DataFetchingEnvironment environment) {
		
		Object[] queryArgs = new Object[] { environment.getArgument("title"),environment.getArgument("url"),environment.getArgument("color") };
		Map<String, String> result = new HashMap<String, String>();
		
	    try {
			PAT_DAO.executeStatement(ADD_NEW_RSS_FEED_SQL_QUERY, queryArgs);
			result.put("result", "Succesfully added new RSS feed.");
	    } catch (Exception e) {
	    	logger.error("Unexpected error occured", e);
	        throw new GraphQLException("Unexpected error adding new RSS feed", e);
	    }
	    
	    return result;
	}

}
