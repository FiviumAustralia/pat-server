package fivium.pat.graphql.queryfields.patient;

import static graphql.Scalars.GraphQLString;
import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static graphql.schema.GraphQLObjectType.newObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
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

public class RetrieveCharitableCompanyList_QF extends PAT_BaseQF {

	private static Log logger = LogFactory.getLog(RetrieveCharitableCompanyList_QF.class);
	private static final String GET_CHARITABLE_COMPANIES = "SELECT Company_Name FROM company WHERE category = \'Charitable\'";

	@Override
	protected GraphQLObjectType defineField() {

		return newObject().name("RetrieveChariatbleCompanies")
				.description("Get all the charitable companies that a patient can contribute data to.")
				.field(newFieldDefinition().name("list").description("The list to be returned.").type(GraphQLString)).build();
	}

	@Override
	protected List<GraphQLArgument> defineArguments() {
		return Arrays.asList();
	}

	@Override
	protected Object fetchData(DataFetchingEnvironment environment) {
		logger.trace("Entering fetch charitable companies...");
		Map<String, String> resultMap = new HashMap<String, String>();
		Collection<Map<String, String>> result;
		try {
			result = PAT_DAO.executeStatement(GET_CHARITABLE_COMPANIES, null);
			if (!result.isEmpty()) {
				logger.trace("Companies found... size "+result.size());
				Iterator it = result.iterator();
				List<String> resultList = new ArrayList<String>();
				while (it.hasNext()) {
					Map<String, String> listOfCompanies = (Map<String, String>) it.next();
					resultList.add(listOfCompanies.get("Company_Name"));
				}
				resultMap.put("list", String.join(",", resultList));
			}
		} catch (Exception e) {
			logger.error("Exception occurred trying fetch list of charitable companies", e);
			throw new GraphQLException("Cannot fetch list of charitable companies.");
		}
		if (result.isEmpty()) {
			logger.debug("Cannot fetch list of charitable companies.");
			resultMap.put("list", "No Charitable companies exist");
		}
		return resultMap;
	}

}
