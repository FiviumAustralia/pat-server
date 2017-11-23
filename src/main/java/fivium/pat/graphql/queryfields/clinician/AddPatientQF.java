package fivium.pat.graphql.queryfields.clinician;

import static graphql.Scalars.GraphQLString;
import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static graphql.schema.GraphQLObjectType.newObject;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Detainted;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;

import com.google.gson.Gson;

import fivium.pat.graphql.queryfields.PAT_BaseQF;
import fivium.pat.utils.PAT_DAO;
import fivium.pat.utils.LegacyInternalServerUtils;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLArgument;
import graphql.schema.GraphQLObjectType;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Deprecated
public class AddPatientQF extends PAT_BaseQF {

	private static final String ADD_PATIENT_PREPARED_SQL_QUERY = "INSERT INTO rns_internal.patient (study_id, first_name, last_name, mrn, dob, contact, email, address, next_of_kin_relationship, next_of_kin_first_name, next_of_kin_last_name, next_of_kin_contact_number) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";
	private static final String INTERMEDIATE_SERVER_ENDPOINT = "https://rnsqs.fiviumdev.com:8443/rns-java-backend-war/internalServerActions";
	private static final String INTERMEDIATE_SERVER_CREATE_ID_SUCCESS_MESSAGE = "New Patient ID created succesfully.";
	
	private static Log logger = LogFactory.getLog(AddPatientQF.class);
	
	@Override
	protected GraphQLObjectType defineField() {
		return newObject()
			    .name("AddPatient")
			    .description("Makes a request to the intermediate server to create a new patient")
			    .field(newFieldDefinition()
			    		.name("result")
			            .type(GraphQLString))		    
			   .build();	
	}

	@Override
	protected List<GraphQLArgument> defineArguments() {
		return Arrays.asList(
				new GraphQLArgument("clinician_jwt", GraphQLString),
				new GraphQLArgument("patient_first_name", GraphQLString),
				new GraphQLArgument("patient_last_name", GraphQLString),
				new GraphQLArgument("patient_study_id", GraphQLString),
				new GraphQLArgument("patient_mrn", GraphQLString),
				new GraphQLArgument("patient_dob", GraphQLString),
				new GraphQLArgument("patient_contact", GraphQLString),
				new GraphQLArgument("patient_email", GraphQLString),
				new GraphQLArgument("patient_address", GraphQLString),
				new GraphQLArgument("patient_next_of_kin_relationship", GraphQLString),
				new GraphQLArgument("patient_next_of_kin_first_name", GraphQLString),
				new GraphQLArgument("patient_next_of_kin_last_name", GraphQLString),
				new GraphQLArgument("patient_next_of_kin_contact_number", GraphQLString)
				);
	}

	@Override
	protected Object fetchData(DataFetchingEnvironment environment)  {
		
		Map<String, String> resultMap = new HashMap<String, String>();
		String intermediateServerResult = null;
		
		try {

			// make call to intermediate server to add new patient_study_id
			intermediateServerResult = addNewStudyID_ToIntermediateServer((String) environment.getArgument("patient_study_id"));
			
			// if adding ID to intermediate server/database failed then abort process and return
			if (!intermediateServerResult.equals(INTERMEDIATE_SERVER_CREATE_ID_SUCCESS_MESSAGE)) {			
				resultMap.put("result", "Call to insert new study into intermediate database failed. Reason: " + intermediateServerResult);		
				return resultMap;
			}

			// build query params for inserting new user into internal database 
			Object[] queryArgs = new Object[] {
					environment.getArgument("patient_study_id"),
					environment.getArgument("patient_first_name"),
					environment.getArgument("patient_last_name"),
					environment.getArgument("patient_mrn"),
					environment.getArgument("patient_dob"),
					environment.getArgument("patient_contact"),
					environment.getArgument("patient_email"),
					environment.getArgument("patient_address"),
					environment.getArgument("patient_next_of_kin_relationship"),
					environment.getArgument("patient_next_of_kin_first_name"),
					environment.getArgument("patient_next_of_kin_last_name"),
					environment.getArgument("patient_next_of_kin_contact_number"),};
			
			// insert new patient details into internal db
			PAT_DAO.executeStatement(ADD_PATIENT_PREPARED_SQL_QUERY, queryArgs);
				
			resultMap.put("result", "Patient Created Sucesfully.");			
		
		} catch (Exception ex) {
			
			logger.error("Unexpected exception occured", ex);
			
			// if adding study_id to intermediate server failed
			if (!INTERMEDIATE_SERVER_CREATE_ID_SUCCESS_MESSAGE.equals(intermediateServerResult)) {
				resultMap.put("result", "Call to insert new study into intermediate database failed. Reason: " + ex.getMessage());				
			} else {				
				resultMap.put("result", "Added new study id to intermediate database but failed to add new patient to internal database. Reason: " + ex.getMessage());				
			}
			
		}
		
		return resultMap;
	}
	
	// Make request to intermediate server
	private String addNewStudyID_ToIntermediateServer(String newStudyID) throws ClientProtocolException, IOException {
		
		//build request
		HttpClient client = HttpClientBuilder.create().build();
		HttpPost post = new HttpPost(INTERMEDIATE_SERVER_ENDPOINT);

		String token = Jwts.builder().setSubject("superuser")
				.signWith(SignatureAlgorithm.HS512, LegacyInternalServerUtils.JWT_KEY).compact();
		post.setHeader("Content-Type", "application/json");
		post.setHeader("Authorization", token);
		String jsonRequestString = "{\"graphQL_Query\":\"{ CreatePatientID(p_id: \\\"?\\\", user:\\\"superuser\\\" ,password:\\\"password12\\\" ) { result } }\"}";
		jsonRequestString = jsonRequestString.replace("?", (CharSequence) newStudyID);
		post.setEntity(new StringEntity(jsonRequestString, "UTF-8"));

		//make request
		HttpResponse response = client.execute(post);
		
		//parse response
		String JSON_ResponseString = IOUtils.toString(response.getEntity().getContent());
		logger.debug("JSON_ResponseString: " + JSON_ResponseString);
		String resultMessage = (String) ((Map) new Gson().fromJson(JSON_ResponseString, Map.class).get("CreatePatientID")).get("result");
		logger.debug("resultMessage: " + resultMessage);
		
		return resultMessage;
		
	}

}
