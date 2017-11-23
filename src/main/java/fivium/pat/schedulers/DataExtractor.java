package fivium.pat.schedulers;

import static fivium.pat.utils.LegacyInternalServerUtils.RNS_PROPS_PATH;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.StringReader;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Map;
import java.util.Properties;

import javax.mail.Message;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.simplejavamail.email.Email;
import org.simplejavamail.mailer.Mailer;
import org.simplejavamail.mailer.config.TransportStrategy;

import com.google.gson.Gson;

import fivium.pat.utils.LegacyInternalServerUtils;
import fivium.pat.utils.PAT_DAO;
import graphql.GraphQLException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class DataExtractor implements Job {

	private static final String STEPS_QUERY = "INSERT INTO stepdata VALUES (?,?,?) ON DUPLICATE KEY UPDATE steps=?;";
	private static final String SLEEP_QUERY = "INSERT INTO sleepdata VALUES (?,?,?,?) ON DUPLICATE KEY UPDATE duration=? AND efficiency = ?;";
	private static final String WEIGHT_QUERY = "INSERT INTO weightdata VALUES (?,?,?) ON DUPLICATE KEY UPDATE weight=?;";
	private static final String SURVEY_QUERY = "INSERT INTO surveydata VALUES (?,?,?) ON DUPLICATE KEY UPDATE survey_data=?;";
	private static final String NOTIFICATION_QUERY = "INSERT INTO notifications VALUES (?,?,?);";

	private static final String INTERMEDIATE_SERVER_ENDPOINT = "https://rnsqs.fiviumdev.com:8443/rns-java-backend-war/internalServerActions";

	private static Log logger = LogFactory.getLog(DataExtractor.class);


	private static final String SYNC_DATES_QUERY =
			"SELECT p.study_id, p.email, lateststepsync.last_steps_sync_date , p.date_created " +
			"FROM patient p LEFT JOIN (select study_id, max(date) last_steps_sync_date " +
			"FROM stepdata group by study_id) lateststepsync ON p.study_id = lateststepsync.study_id " +
			"WHERE last_steps_sync_date < (curdate() - 2) || (last_steps_sync_date is null && DATEDIFF(curdate(), date_created) > 2)";
	private final String MAIL_SERVER;
	private final String EMAIL_ADDRESS;
	private final String SENDING_DISPLAY_NAME;
	private final String SENDING_EMAIL_PASSWORD;
	//TODO default subject required
	private final String SUBJECT;
	//TODO default body text required
	private final String BODY_TEXT;

	public DataExtractor() {
		super();
		Properties prop = new Properties();
		try {
			logger.info("Super user verified");
			String filePath = RNS_PROPS_PATH + "emailAccount.properties"; // "C:\Users\[username]\rns\props\emailpass.properties"
			FileInputStream fis = new FileInputStream(filePath);
			prop.load(fis);
			logger.info("content retrieved.");
		} catch (Exception ex) {
			logger.error("Execption occured during DataExtraction. "+ex);
		}
		MAIL_SERVER = prop.getProperty("mail_server");
		EMAIL_ADDRESS = prop.getProperty("email_address");
		SENDING_DISPLAY_NAME = prop.getProperty("display_name");
		SENDING_EMAIL_PASSWORD = prop.getProperty("password");
		SUBJECT = prop.getProperty("subject");
		BODY_TEXT = prop.getProperty("body_text");
	}

	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {

		logger.debug("Entering DataExtractor.execute()...");
		try {

			HttpClient client = HttpClientBuilder.create().build();
			HttpPost post = new HttpPost(INTERMEDIATE_SERVER_ENDPOINT);
			String token = Jwts.builder().setSubject("superuser").signWith(SignatureAlgorithm.HS512, LegacyInternalServerUtils.JWT_KEY)
					.compact();
			post.setHeader("Content-Type", "application/json");
			post.setHeader("Authorization", token);
			//TODO change the password before going live.
			String jsonRequestString = "{\"graphQL_Query\":\"{ RetrieveData (user:\\\"superuser\\\", password:\\\"password12\\\") { data } }\"}";
			post.setEntity(new StringEntity(jsonRequestString, "UTF-8"));

			HttpResponse response = client.execute(post);

			// check response is valid
			if (response.getStatusLine().getStatusCode() != HttpServletResponse.SC_OK) {
				throw new Exception("Encountered following HTTP error: " + response.getStatusLine().getStatusCode());
			}

			String resultAsJSON_String = IOUtils.toString(response.getEntity().getContent());
			logger.debug("resultAsJSON_String:  " + resultAsJSON_String);
			Map<String, Map<String, String>> resultAsJSON_Object = new Gson().fromJson(resultAsJSON_String, Map.class);

			String fileContent = resultAsJSON_Object.get("RetrieveData").get("data");

			logger.debug("Pulled Following Data:\n\n" + resultAsJSON_String);
			logger.debug("Extracted content:\n\n" + fileContent);

			// decrypt pulled data
			decryptFile(fileContent);
		} catch (Exception ex) {
			logger.error("Execption occured during DataExtraction. "+ex);
		}

	}

	
	public void decryptFile(String encyrptedFileData) throws Exception {
		// create output string for debugging purposes
		StringBuilder outputFile = new StringBuilder();
		BufferedReader reader = new BufferedReader(new StringReader(encyrptedFileData));
		String nextLine = reader.readLine();
		// read each line of given property file content
		while (nextLine != null) {
			// parse and convert each section of the current line
			try {
				String nextLineKey = nextLine.split("=")[0].trim();
				logger.trace("nextLineKey: " + nextLineKey);
				String nextLineValue = nextLine.split("=")[1].trim();
				logger.trace("nextLineValue: " + nextLineValue);
				String nextdecryptedData = LegacyInternalServerUtils.decrypt(nextLineValue);
				logger.trace("nextdecryptedData: " + nextdecryptedData);
				// append debug string with current decrypted line data
				outputFile.append(nextLineKey).append("=").append(nextdecryptedData).append(System.lineSeparator());
				// push data from each line to internal db
				pushDecryptedDataToDatabase(new Gson().fromJson(nextdecryptedData, Map.class), nextdecryptedData);
			} catch (Exception ex) {
				logger.error("Exception occured when handling encyrptedFileData", ex);
				ex.printStackTrace();
			}
			nextLine = reader.readLine();
		}
		logger.debug("Decrypted File Content:\n\n" + outputFile.toString());
		sendEmailNotifications();
	}
	
	private void pushDecryptedDataToDatabase(Map<String, Object> decryptedJSON_Object, String decryptedString) {
		logger.trace("Entering pushDecryptedDataToDatabase()...");
		logger.trace("decryptedJSON_Object.toString(): " + decryptedJSON_Object.toString());
		logger.trace("decryptedString: " + decryptedString);
		//{"day":"2017-10-18","steps":"11257","user_id":"GMK3Z3D"}

		try {

			// check if data object is valid
			if (!decryptedJSON_Object.containsKey("day") || !decryptedJSON_Object.containsKey("user_id")) {
				throw new Exception("Decrypted data is missing mandatory day or user_id");
			}

			// pull out the day and user_id values
			String day = (String) decryptedJSON_Object.get("day");
			String user_id = (String) decryptedJSON_Object.get("user_id");

			// check if decrypted data object contains step data
			if (decryptedJSON_Object.containsKey("steps")) {

				String steps = decryptedJSON_Object.get("steps").toString();
				Object[] queryArgs = new Object[] { user_id, day, steps, steps };
				//No More Sleep Data in RNSH_Development
//				String duration = decryptedJSON_Object.get("sleep").toString();
//				String efficiency = decryptedJSON_Object.get("efficiency").toString();
//				Object[] sleepQueryArgs = new Object[] { user_id, day, duration, efficiency, duration, efficiency };
				try {
					PAT_DAO.executeStatement(STEPS_QUERY, queryArgs);
//					RnsInternalDAO.executeStatement(SLEEP_QUERY, sleepQueryArgs);
				} catch (Exception e) {
					logger.error("Unexpected DAO error.", e);
					throw new GraphQLException("Unexpected execution error", e);
				}
			}

			// check if decrypted data object contains weight data
			if (decryptedJSON_Object.containsKey("weight")) {

				String weight = (String) decryptedJSON_Object.get("weight");
				Object[] queryArgs = new Object[] { user_id, day, weight, weight };

				try {
					PAT_DAO.executeStatement(WEIGHT_QUERY, queryArgs);
				} catch (Exception e) {
					logger.error("Unexpected DAO error.", e);
					throw new GraphQLException("Unexpected execution error", e);
				}
			}

			// check if decrypted data object contains survey data
			if (decryptedJSON_Object.containsKey("survey")) {

				String survey = (String) decryptedJSON_Object.get("survey");
				Object[] queryArgs = new Object[] { user_id, day, survey, survey };

				try {
					PAT_DAO.executeStatement(SURVEY_QUERY, queryArgs);
				} catch (Exception e) {
					logger.error("Unexpected DAO error.", e);
					throw new GraphQLException("Unexpected execution error", e);
				}

			}
			if(decryptedJSON_Object.containsKey("notificationType")) {
				String notificationType = (String) decryptedJSON_Object.get("notificationType");
				Object[] queryArgs = new Object[] { user_id, day, notificationType };

				try {
					PAT_DAO.executeStatement(NOTIFICATION_QUERY, queryArgs);
				} catch (Exception e) {
					logger.error("Unexpected DAO error.", e);
					throw new GraphQLException("Unexpected execution error", e);
				}
			}
		} catch (Exception ex) {
			logger.error("Error pushing data to Database. Error occurred with  " + decryptedString +"\n Exception "+ex);
		}
	}

	private void sendEmailNotifications() {
		Collection<Map<String, String>> result = null;
		try {
			result = PAT_DAO.executeStatement(SYNC_DATES_QUERY, new Object[]{});
		} catch (SQLException e) {
			logger.error("SQL Exception occurred: ", e);
		} catch (ClassNotFoundException e) {
			logger.error("ClassNotFound Exception Occurred", e);
		} catch (Error e) {
			logger.error("Unexpected exception occurred: ", e);
		}

		if (result != null) {
			for (Map<String, String> patient : result) {
				sendEmail(patient);
			}
		}
	}

	private void sendEmail(Map<String, String> patient) {
		String to_recipient = patient.get("email");

		if (to_recipient != null && !to_recipient.isEmpty()) {
			//set up mailer
			Email email = new Email();

			email.setFromAddress(SENDING_DISPLAY_NAME, EMAIL_ADDRESS);
			email.addRecipient(null, to_recipient, Message.RecipientType.TO);
			email.setSubject(SUBJECT);
			email.setText(BODY_TEXT);

			//send the email
			new Mailer(MAIL_SERVER, 25, EMAIL_ADDRESS, SENDING_EMAIL_PASSWORD, TransportStrategy.SMTP_TLS).sendMail(email);
		}
	}

}
