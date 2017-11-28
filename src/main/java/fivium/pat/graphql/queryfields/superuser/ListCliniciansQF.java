package fivium.pat.graphql.queryfields.superuser;

import static graphql.Scalars.GraphQLString;
import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static graphql.schema.GraphQLObjectType.newObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import fivium.pat.graphql.PAT_BaseQF;
import fivium.pat.utils.PAT_DAO;
import graphql.Scalars;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLArgument;
import graphql.schema.GraphQLObjectType;

public class ListCliniciansQF extends PAT_BaseQF {

  private static final String LIST_CLINICIANS_PREPARED_SQL_QUERY = "SELECT Email, Firstname, Lastname FROM clinicians";
  private static final String LIST_CLINICIAN_PREPARED_SQL_QUERY = "SELECT Email, Firstname, Lastname FROM clinicians WHERE Email = ?";
  
  private static Log logger = LogFactory.getLog(ListCliniciansQF.class);

  @Override
  protected GraphQLObjectType defineField() {
    return newObject()
            .name("ListClinicians")
            .description("List Clinicians")
            .field(newFieldDefinition()
                    .name("Email")
                    .description("The email address of the clinician")
                    .type(GraphQLString))
            .field(newFieldDefinition()
                    .name("Firstname")
                    .description("The first name of the clinician")
                    .type(GraphQLString))
            .field(newFieldDefinition()
                    .name("Lastname")
                    .description("The last name of the clinician")
                    .type(GraphQLString))
            .build();
  }

  @Override
  protected List<GraphQLArgument> defineArguments() {
	return Arrays.asList(
			new GraphQLArgument("Email", Scalars.GraphQLString)
			);
  }

  @Override
  protected Object fetchData(DataFetchingEnvironment environment) {
    Object[] queryArgs = new Object[]{
    		environment.getArgument("Email")
    };
    Collection<Map<String, String>> result = new ArrayList<Map<String, String>>();

    try {
		if (queryArgs.length > 0 && queryArgs[0] != null ) {
			result = PAT_DAO.executeStatement(LIST_CLINICIAN_PREPARED_SQL_QUERY, queryArgs);
		} else {
			result = PAT_DAO.executeStatement(LIST_CLINICIANS_PREPARED_SQL_QUERY, queryArgs);
		}
    	
    } catch (Exception e) {
			logger.error("Unexpected error occured", e);
		}

		if (result.isEmpty()) {
			logger.error("No Clinicians Found.");
		}
    return result;
  }
}
