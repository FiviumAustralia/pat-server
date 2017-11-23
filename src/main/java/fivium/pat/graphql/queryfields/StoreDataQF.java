package fivium.pat.graphql.queryfields;

import static graphql.Scalars.GraphQLString;
import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static graphql.schema.GraphQLObjectType.newObject;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import fivium.pat.utils.Constants;
import graphql.Scalars;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLArgument;
import graphql.schema.GraphQLObjectType;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;

@Deprecated
public class StoreDataQF extends PAT_BaseQF{
	private static Log logger = LogFactory.getLog(StoreDataQF.class);
	protected GraphQLObjectType defineField() {
		return newObject()
			    .name("StoreData")
			    .description("Store a user's activity snapshot (for the day?)")
			    .field(newFieldDefinition()
			    		.name("response")
			            .type(GraphQLString))		    
			   			.build();	
	}
	protected List<GraphQLArgument> defineArguments() {
		return Arrays.asList(
				new GraphQLArgument("token", Scalars.GraphQLString),
				new GraphQLArgument("data", Scalars.GraphQLString)
				);
	}

	protected Object fetchData(DataFetchingEnvironment environment) {
		logger.info("Entering StoringData...");
		Map<String, String> resultMap = new HashMap<String, String>();
		String token = environment.getArgument("token").toString();
		String data = environment.getArgument("data").toString();
		if(logger.isDebugEnabled()){
			Jws<Claims> claims = Jwts.parser().setSigningKey(Constants.JWT_KEY).parseClaimsJws(token);
			Claims claimsBody = claims.getBody();
			String subject = claimsBody.getSubject();
			logger.debug("Storing data for "+subject);
		}
		String tokenWithRandomNumber = token + "_"+(int)(Math.random() * 1000) + 1;
		try {
			String filePath = Constants.RNS_PROPS_PATH + "data.properties";
			logger.debug("Props file path: " + filePath);
			File f = new File(filePath);
			System.out.println(filePath);
			if(!f.exists()) { 
				f.createNewFile();
			}
			PropertiesConfiguration props = new PropertiesConfiguration(filePath);
			props.setProperty(tokenWithRandomNumber, data);
			props.save();
			resultMap.put("response", "Success");
		} catch (Exception ex) {
			logger.error("Exception occurred while storing data", ex);
			resultMap.put("response", ex.getMessage());
		} 
		return resultMap;
	}
}
