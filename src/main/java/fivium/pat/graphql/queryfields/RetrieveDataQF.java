package fivium.pat.graphql.queryfields;

import static graphql.Scalars.GraphQLString;
import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static graphql.schema.GraphQLObjectType.newObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import fivium.pat.utils.Constants;
import fivium.pat.utils.PAT_DAO;
import fivium.pat.utils.RnsUtils;
import graphql.Scalars;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLArgument;
import graphql.schema.GraphQLObjectType;

@Deprecated
public class RetrieveDataQF extends PAT_BaseQF {
	private static Log logger = LogFactory.getLog(RetrieveDataQF.class);
	private static final String VERIFY_SUPER_USER = "SELECT Password, Salt from internaluser WHERE User = ?";
	@Override
	protected GraphQLObjectType defineField() {
		return newObject().name("RetrieveData").description("Retrieve the entire properites file")
				.field(newFieldDefinition().name("data").type(GraphQLString)).build();
	}

	@Override
	protected List<GraphQLArgument> defineArguments() {
		return Arrays.asList(
				new GraphQLArgument("user", Scalars.GraphQLString),
				new GraphQLArgument("password", Scalars.GraphQLString)
				);
	}

	@Override
	protected Object fetchData(DataFetchingEnvironment environment) {
		logger.info("Entering RetrieveData...");
		Map<String, String> resultMap = new HashMap<String, String>();
		Collection<Map<String, String>> resultVerifyUser;
		try {
			Object[] queryArgs = new Object[] {environment.getArgument("user"), environment.getArgument("password")};
			Object[] queryArgs_2 = new Object[] {environment.getArgument("user")};
			resultVerifyUser = PAT_DAO.executeStatement(VERIFY_SUPER_USER, queryArgs_2);
			Map<String, String> aMap = new HashMap<String, String>();
				aMap = resultVerifyUser.iterator().next();
			byte[] encoded = aMap.get("Salt").getBytes("ISO-8859-1"); 
			if(!resultVerifyUser.isEmpty() && RnsUtils.isExpectedPassword(queryArgs[1].toString().toCharArray(), encoded,aMap.get("Password").getBytes("ISO-8859-1"))){
				logger.info("Super user verified");
				String filePath = Constants.RNS_PROPS_PATH + "data.properties";
				FileInputStream fis = new FileInputStream(filePath);
				String content = IOUtils.toString(fis);
				resultMap.put("data", content);
				logger.info("content retrieved.");
				backupCurrentPropsFile(filePath);
				//clear data from props file
				PropertiesConfiguration props = new PropertiesConfiguration(filePath);
				props.clear();
				props.save();
				logger.info("properties file cleared");
			} else {
					logger.debug("Invalid super user credentials while retrieving data");
				resultMap.put("data", "Invalid Credentials");
			}
		} catch (Exception ex) {
			logger.error("Exception occurred while retrieving data", ex);
			resultMap.put("data", "Failed to retrieve properties file data");
		}

		return resultMap;
	}
	
	private void backupCurrentPropsFile(String currentPropsFilePath) throws IOException {
		
		String newPropsFileBaseName = StringUtils.substringBefore(currentPropsFilePath, "data.properties") + "data_";
		logger.debug("Attempting props file backup...");
		String newPropsFileFullName = newPropsFileBaseName + new SimpleDateFormat("yyyy-MM-dd__HH-mm-ss").format(new Date()) + ".properties";		
		logger.debug("Source file: " + currentPropsFilePath + "\nDestination path: " + newPropsFileFullName);
		Files.copy(new File(currentPropsFilePath).toPath(), new File(newPropsFileFullName).toPath());
	}
}
