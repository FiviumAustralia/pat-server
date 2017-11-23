package fivium.pat.graphql.queryfields.superuser;

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
import fivium.pat.utils.RnsUtils;
import graphql.Scalars;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLArgument;
import graphql.schema.GraphQLObjectType;

public class CreateCompany_QF extends PAT_BaseQF {
    private static Log logger = LogFactory.getLog(CreateCompany_QF.class);
    private static final String VERIFY_SUPER_USER = "SELECT Password, Salt from internaluser WHERE User = ?";
    private static final String CREATE_COMPANY_PREPARED_SQL_QUERY = "INSERT INTO company (Company_Name, Terms_and_Conditions, Permissions, Category) VALUES (?, ?, ?, ?)";

    //Returned GraphQL Object after a successful request
    @Override
    protected GraphQLObjectType defineField() {
        return newObject()
                .name("CreateCompany")
                .description("Create a new company")
                .field(newFieldDefinition()
                    .name("result")
                    .type(GraphQLString))
                    .build();
    }

    //Input arguments
    @Override
    protected List<GraphQLArgument> defineArguments() {
        return Arrays.asList(
                new GraphQLArgument("user", Scalars.GraphQLString),
                new GraphQLArgument("password", Scalars.GraphQLString),
                new GraphQLArgument("company_name", Scalars.GraphQLString),
                new GraphQLArgument("terms_and_conditions", Scalars.GraphQLString),
                new GraphQLArgument("permissions", Scalars.GraphQLString),
                new GraphQLArgument("category", Scalars.GraphQLString)
        );
    }

    @Override
    protected Object fetchData(DataFetchingEnvironment environment) {
        logger.info("Creating Company...");
        Object[] queryArgs = new Object[] { environment.getArgument("company_name"),environment.getArgument("terms_and_conditions"),environment.getArgument("permissions"),environment.getArgument("category")  };
        Object[] queryArgs_2 = new Object[] {environment.getArgument("user"),environment.getArgument("password")};
        Collection<Map<String, String>> resultCreateCompany;
        String resultMessage = null;
        try {
            if (verifySuperUser(VERIFY_SUPER_USER, queryArgs_2, queryArgs_2, 1) == true) {
                resultCreateCompany = PAT_DAO.executeStatement(CREATE_COMPANY_PREPARED_SQL_QUERY, queryArgs);
                   resultMessage = "New Company created succesfully.";
            } else {
                resultMessage = "Invalid Credentials to create company";
            }
        } catch (Exception e) {
            logger.error("Exception occurred trying to authenticate p_id", e);
            resultMessage = e.getLocalizedMessage();
        }
        logger.info("CreateCompany Result"+resultMessage);
        Map<String, String> resultMap = new HashMap<String, String>();
        resultMap.put("result", resultMessage);
        return resultMap;
    }
}
