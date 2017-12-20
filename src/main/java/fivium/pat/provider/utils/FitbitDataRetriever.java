package fivium.pat.provider.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import fivium.pat.datamodel.providers.fitbit.ActivitiesSteps;
import fivium.pat.datamodel.providers.fitbit.Device;
import fivium.pat.datamodel.providers.fitbit.SleepActivity;
import fivium.pat.provider.data.AppData;
import fivium.pat.provider.data.AppDataBackendPortal;
import fivium.pat.utils.Constants;
import fivium.pat.utils.PAT_DAO;
import fivium.pat.utils.PatUtils;

public class FitbitDataRetriever {
	private static Log logger = LogFactory.getLog(FitbitDataRetriever.class);

	/*
	 * This method takes the old refresh token, requests Fitbit for a new access
	 * token. In the response Fitbit responds with an access token and a refresh
	 * token. The access token is returned and the new refresh token is stored in
	 * the database.
	 */
	public static String getAccessToken(String p_id, String refresh_token, boolean sendNotificationOnFailure) {
		HttpClient client = HttpClientBuilder.create().build();
		HttpPost post = new HttpPost(Constants.FITBIT_REFRESH_TOKEN_ENDPOINT);
		post.setHeader("Authorization", "Basic " + Constants.FITBIT_CLIENT_SECRET);
		post.setHeader("Content-Type", "application/x-www-form-urlencoded");
		String refreshString = "grant_type=refresh_token&refresh_token=" + refresh_token;
		post.setEntity(new StringEntity(refreshString, "UTF-8"));

		String access_token = "", new_refresh_token = "";

		logger.info("Retrieving Fitbit refresh token for user_id: " + p_id);
		try {
			HttpResponse response = client.execute(post);
			checkResponseHeader(response);
			String resultAsJSON_String = IOUtils.toString(response.getEntity().getContent());
			Map<String, String> resultAsJSON_Object = new Gson().fromJson(resultAsJSON_String, Map.class);

			access_token = resultAsJSON_Object.get("access_token");
			new_refresh_token = resultAsJSON_Object.get("refresh_token");

		} catch (Exception e) {
			logger.error("Unable to get refresh_token from Fitbit for " + p_id + ": " + e);
			if(sendNotificationOnFailure) {
				sendFitbitAccessErrorNotification(p_id);
			}
			return null;
		}
		Object[] queryArgs = new Object[] { new_refresh_token, p_id };
		Collection<Map<String, String>> sqlResult;
		logger.info("Storing refresh_token for " + p_id + " to database");
		try {
			sqlResult = PAT_DAO.executeStatement(Constants.STORE_REFRESH_TOKEN, queryArgs);
			if (sqlResult.isEmpty()) {
				logger.info("Successfully stored new refresh_token for user " + p_id);
			}
		} catch(SQLException | ClassNotFoundException e) {
			logger.error("SQL Exception occurred while running query "+Constants.STORE_REFRESH_TOKEN+" - Following exception resulted" + e);
		}	catch (Exception e) {
			logger.error("Error occured fetching list of users registered with Fitbit: " + e);
		}

		return access_token;
	}


	public static ActivitiesSteps getActivityData(String p_id, String base_date, String access_token) {
		// get today's date
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Date date = new Date();
		String end_date = df.format(date);

		logger.info("Getting activity data for user " + p_id + " from " + base_date + " to " + end_date);

		String endpoint = Constants.FITBIT_ACTIVITY_TIME_SERIES_ENDPOINT.replace("${baseDate}", base_date)
				.replace("${endDate}", end_date);
		HttpClient client = HttpClientBuilder.create().build();
		HttpGet get = new HttpGet(endpoint);
		get.setHeader("Authorization", "Bearer " + access_token);

		try {
			HttpResponse response = client.execute(get);
			checkResponseHeader(response);

			String resultAsJSON_String = IOUtils.toString(response.getEntity().getContent());
			logger.info("Fitbit fitness data response as a string " + resultAsJSON_String);
			ActivitiesSteps resultAsJSON_Object = new Gson().fromJson(resultAsJSON_String, ActivitiesSteps.class);
			return resultAsJSON_Object;
		} catch (Exception e) {
			logger.error("Unable to get fitness data from fitbit: " + e);
			return null;
		}
	}

	public static ArrayList<Device> getDeviceInformation(String p_id, String access_token) {
		logger.info("Getting device information for " + p_id);

		HttpClient client = HttpClientBuilder.create().build();
		HttpGet get = new HttpGet(Constants.FITBIT_DEVICE_INFO_ENDPOINT);
		get.setHeader("Authorization", "Bearer " + access_token);

		try {
			HttpResponse response = client.execute(get);
			checkResponseHeader(response);

			String resultAsJSON_String = IOUtils.toString(response.getEntity().getContent());
			logger.info("Fitbit device info response as a string " + resultAsJSON_String);

			Type deviceListType = new TypeToken<ArrayList<Device>>() {
			}.getType();
			ArrayList<Device> resultAsJSON_Object = new Gson().fromJson(resultAsJSON_String, deviceListType);
			return resultAsJSON_Object;
		} catch (Exception e) {
			logger.error("Unable to fetch device information from fitbit: " + e);
			return null;
		}
	}

	public static SleepActivity getSleepData(String p_id, String base_date, String access_token) {
		logger.info("Getting sleep data for " + p_id);
		// get today's date
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Date today = new Date();
		String end_date = df.format(today);
		String sleepHttpQuery = Constants.FITBIT_SLEEP_LOG_LIST_ENDPOINT.replace("${endDate}", end_date)
				.replace("${startDate}", base_date);
		HttpClient client = HttpClientBuilder.create().build();
		HttpGet get = new HttpGet(sleepHttpQuery);
		get.setHeader("Authorization", "Bearer " + access_token);

		try {
			HttpResponse response = client.execute(get);
			checkResponseHeader(response);

			String resultAsJSON_String = IOUtils.toString(response.getEntity().getContent());
			logger.info("Fitbit sleep data response as a string " + resultAsJSON_String);
			SleepActivity sleepActivity = new Gson().fromJson(resultAsJSON_String, SleepActivity.class);
			return sleepActivity;
		} catch (Exception e) {
			logger.error("Unable to fetch sleep from fitbit: " + e);
			return null;
		}
	}


	/*
	 * This method downloads the 
	 * 1. Activity Data
	 * 2. Device Information
	 * 3. Sleep Data
	 * from the Fitbit server.
	 * Provides the downloaded data to the mapper to map it into the required structure.
	 */
	private static List<AppDataBackendPortal> pullFitbitData(String pId, String lastDate, String accessToken) {
		List<AppDataBackendPortal> appData = new ArrayList<AppDataBackendPortal>();
		ActivitiesSteps activityData = FitbitDataRetriever.getActivityData(pId, lastDate, accessToken);
		List<Device> deviceInformation = FitbitDataRetriever.getDeviceInformation(pId, accessToken);
		SleepActivity sleepData = FitbitDataRetriever.getSleepData(pId, lastDate, accessToken);
		if (activityData != null && deviceInformation != null && sleepData != null) {
			appData = FitbitMapper.mapCliniciansAppDataStructure(pId, activityData, deviceInformation, sleepData);
			return appData;
		}
		return null;
	}

	private static void checkResponseHeader(HttpResponse response) throws Exception {
		if (response.getStatusLine().getStatusCode() != HttpServletResponse.SC_OK) {
			String resultAsJSON_String = IOUtils.toString(response.getEntity().getContent());
			throw new Exception("Encountered following HTTP error: " + response.getStatusLine().getStatusCode() + " :: "
					+ resultAsJSON_String);
		}
	}

	/*
	 * This method is used to fetch Fitbit users from the database. 
	 */
	
	private static Collection<Map<String, String>> getFitbitUsers() {
		try {
			return PAT_DAO.executeStatement(Constants.FITBIT_USERS, new Object[] {});
		} catch(SQLException | ClassNotFoundException e) {
			logger.error("SQL Exception or ClassNotFoundException occurred while running query "+Constants.FITBIT_USERS+" - Following exception resulted" + e);
		} catch (Exception e) {
			logger.error("Error occured fetching list of users registered with Fitbit: " + e);
		}
		return null;
	}

	private static String getLastFetchedDate(String p_id) {
		Collection<Map<String, String>> result = null;
		String lastDate;
		try {
			result = PAT_DAO.executeStatement(Constants.LAST_FETCHED_DATE, new Object[] { p_id });
			if(!result.isEmpty()) {
				Iterator<Map<String, String>> iterator = result.iterator();
				return lastDate = iterator.next().get("date");	
			} else {
				return PatUtils.getCurrentFormattedDate();
			}
		} catch(SQLException | ClassNotFoundException e) {
			logger.error("SQL Exception or ClassNotFoundException occurred while running query "+Constants.FITBIT_USERS+" - Following exception resulted" + e);
		} catch (Exception e) {
			logger.error("Error occured fetching list of users registered with Fitbit: " + e);
		}
		return null;
	}

	/*
	 * Polls Fitbit for user's activity data, sleep data and device information.
	 */
	public static void pollFitbitForData(boolean analyzeFitBitData) {
		Collection<Map<String, String>> result = FitbitDataRetriever.getFitbitUsers();
		if (null == result) {
			logger.error("Fitbit users list is null. ");
		} else {
			for (Map<String, String> user : result) {
				String refresh_token = user.get("provider_refresh_token");
				String p_id = user.get("p_id");
				boolean sendAccessErrorNotifications = Calendar.getInstance().get(Calendar.HOUR_OF_DAY) % 3 == 0;
				String access_token = FitbitDataRetriever.getAccessToken(p_id, refresh_token, sendAccessErrorNotifications);
				if (null != access_token && !"".equalsIgnoreCase(access_token)) {
					String lastDate = FitbitDataRetriever.getLastFetchedDate(p_id);
					if (null == lastDate || lastDate.equalsIgnoreCase("null")) {
						lastDate = PatUtils.getCurrentFormattedDate();
					}
					List<AppDataBackendPortal> appData = FitbitDataRetriever.pullFitbitData(p_id, lastDate, access_token);
					if (appData != null) {
						FitbitDataRetriever.storeCollectedData(p_id, appData);
						if(analyzeFitBitData) {
							FitbitDataRetriever.analyzeCollectedData(p_id, appData);
							sendNotifications();
						}
					}
				}
			}
		}
	}

	private static void sendNotifications() {
		Collection<Map<String, String>> sqlResult;
		try {
			String filePath = Constants.RNS_PROPS_PATH + "firebase.properties"; // "C:\Users\[username]\rns\props\firebase.properties"
			FileInputStream fis = new FileInputStream(filePath);
			Properties prop = new Properties();
			prop.load(fis);
			sqlResult = PAT_DAO.executeStatement(Constants.GET_NOTIFICATION_USER_LIST, null);
			List<Object> updatedNotificationList = new ArrayList<Object>();
			Map<String, List<Object>> dataToBeStored = new HashMap<String,List<Object>>();
			int counter = 0;
			for (Map<String, String> user : sqlResult) {
				updatedNotificationList.clear();
				sendNotificationsToFireBase(prop, user);
				updatedNotificationList.add(user.get("p_id"));
				updatedNotificationList.add(user.get("notification_text"));
				updatedNotificationList.add(user.get("notification_date"));
				dataToBeStored.put(Integer.toString(counter), updatedNotificationList);
				counter++;
			}
			//Updating the delivered notifications
			PAT_DAO.executeSingleSQLStatementInBulk(Constants.UPDATE_NOTIFICATION_DELIVERY_STATUS , dataToBeStored);
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void sendNotificationsToFireBase(Properties prop, Map<String, String> user)
			throws MalformedURLException, IOException, ProtocolException {
		URL url = new URL(Constants.FIREBASE_NOTIFICATION_API);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setDoOutput(true);
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Content-Type", "application/json");
		conn.setRequestProperty("Authorization", "key=" + prop.getProperty("firebaseKey"));
		conn.setDoOutput(true);
		String input = "{\"notification\" : {\"title\" : \"PAT App Notification\", \"body\": \""+user.get("notification_text")+"\"}, \"to\": \""+user.get("firebase_device_token")+"\"}";
		OutputStream os = conn.getOutputStream();
		os.write(input.getBytes());
		os.flush();
		os.close();
		if (conn.getResponseCode() != 200) {
			logger.error("Tried pushing a firebase notification to the user " + user.get("p_id")
					+ " but encountered the following error");
		}
	}

	private static void sendFitbitAccessErrorNotification(String pId) {
		Collection<Map<String, String>> sqlResult;
		String filePath = Constants.RNS_PROPS_PATH + "firebase.properties"; // "C:\Users\[username]\rns\props\firebase.properties"
		try {
			FileInputStream fis = new FileInputStream(filePath);
			Properties prop = new Properties();
			prop.load(fis);
			sqlResult = PAT_DAO.executeStatement(Constants.FITBIT_ACCESS_ERROR_NOTIFICATION_USER_LIST, new Object[] { pId });
			Map<String, String> userToBeNotified = sqlResult.iterator().next();
			userToBeNotified.put("notification_text", "Unable to access fibit. Please re-authorize");
			sendNotificationsToFireBase(prop, userToBeNotified);
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void analyzeCollectedData(String p_id, List<AppDataBackendPortal> appData){
		String currentDate = PatUtils.getCurrentFormattedDate();
		List<Object> notificationList = new ArrayList<Object>();
		Map<String, List<Object>> notificationDataToBeStoredMap = new HashMap<String,List<Object>>();
		int counter = 0;
		for (AppDataBackendPortal data : appData) {
			notificationList.clear();
			//We only analyze the data for the current day.
			if (currentDate.equalsIgnoreCase(data.getActivityDate())) {
				//We check if the user has synced their tracker with the Fitbit App today.
				if (!currentDate.equalsIgnoreCase(data.getLastSyncDate())) {
					notificationList.add(data.getpId());
					notificationList.add(currentDate);
					notificationList.add("Please sync with Fitbit");
					notificationList.add("false");
					notificationList.add("false");
					notificationList.add(data.getpId());
					notificationDataToBeStoredMap.put(Integer.toString(counter), notificationList);
					counter++;
				} else {
					//If the user has synced today, then we check the battery level for all their
					// registered fitbit trackers. If low, we provide a notification
					for (Device device : data.getDeviceData()) {
						if ("Tracker".equalsIgnoreCase(device.getDeviceType()) && "Low".equalsIgnoreCase(device.getBattery())) {
							notificationList.add(data.getpId());
							notificationList.add(currentDate);
							notificationList.add("Please charge your Fitbit");
							notificationList.add("false");
							notificationList.add("false");
							notificationList.add(data.getpId());
							notificationDataToBeStoredMap.put(Integer.toString(counter), notificationList);
							//the counter is used as a key in the maps.
							counter++;
						}
					}
				}
			}
		}
		try {
			PAT_DAO.executeSingleSQLStatementInBulk(Constants.SAVE_FITBIT_NOTIFICATION_DATA, notificationDataToBeStoredMap);
		} catch (Exception e) {
			logger.error("Exception occurred while storing notification data into the database. Exception details " + e.getLocalizedMessage());
		}
	}

	private static void storeCollectedData(String p_id, List<AppDataBackendPortal> appData) {
		try {
			Map<String, List<Object>> dataToBeStoredSteps = prepareDataToBeStoredForSteps(appData);
			Map<String, List<Object>> dataToBeStoredSleep = prepareDataToBeStoredForSleep(appData);
			PAT_DAO.executeSingleSQLStatementInBulk(Constants.SAVE_FITBIT_STEPS, dataToBeStoredSteps);
			PAT_DAO.executeSingleSQLStatementInBulk(Constants.SAVE_FITBIT_SLEEP, dataToBeStoredSleep);
		} catch (Exception e) {
			logger.error("Exception occurred while storing fitbit data into the database for user id " + p_id
					+ " Exception details " + e.getLocalizedMessage());
		}
	}

	private static Map<String, List<Object>> prepareDataToBeStoredForSleep(List<AppDataBackendPortal> appData) {
		Map<String, List<Object>> dataToBeStoredMap = new HashMap<String, List<Object>>();
		int i = 0;
		String query = "";
		List<Object> sqlArgumentsList = new ArrayList<>();
		for (AppDataBackendPortal entity : appData) {
			if (null != entity.getDailySleepData()) {
				sqlArgumentsList.add(entity.getpId());
				sqlArgumentsList.add(entity.getActivityDate());
				sqlArgumentsList.add(entity.getDailySleepData().getDuration() / 60); //Minutes
				sqlArgumentsList.add(entity.getDailySleepData().getEfficiency());
				sqlArgumentsList.add(entity.getDailySleepData().getStartTime());
				sqlArgumentsList.add(entity.getDailySleepData().getEndTime());
				sqlArgumentsList.add(entity.getDailySleepData().getLevels().getSleepSummary().getSleepingTime().getMinutes());
				sqlArgumentsList.add(entity.getDailySleepData().getLevels().getSleepSummary().getaWakeTime().getMinutes());
				sqlArgumentsList.add(entity.getDailySleepData().getLevels().getSleepSummary().getRestlessTime().getMinutes());
				sqlArgumentsList.add(entity.getDailySleepData().getDuration() / 60); //Minutes
				sqlArgumentsList.add(entity.getDailySleepData().getEfficiency());
				sqlArgumentsList.add(entity.getDailySleepData().getStartTime());
				sqlArgumentsList.add(entity.getDailySleepData().getEndTime());
				sqlArgumentsList.add(entity.getDailySleepData().getLevels().getSleepSummary().getSleepingTime().getMinutes());
				sqlArgumentsList.add(entity.getDailySleepData().getLevels().getSleepSummary().getaWakeTime().getMinutes());
				sqlArgumentsList.add(entity.getDailySleepData().getLevels().getSleepSummary().getRestlessTime().getMinutes());
				query += Integer.toString(i);
				dataToBeStoredMap.put(query, sqlArgumentsList);
				i++;
			}
		}
		return dataToBeStoredMap;
	}
	
	private static Map<String, List<Object>> prepareDataToBeStoredForSteps(List<AppDataBackendPortal> appData) {
		Map<String, List<Object>> dataToBeStoredMap = new HashMap<String, List<Object>>();
		int i = 0;
		String query = "";
		List<Object> sqlArgumentsList = new ArrayList<>();
		for (AppDataBackendPortal entity : appData) {
			sqlArgumentsList = new ArrayList<>();
			sqlArgumentsList = new ArrayList<>();
			sqlArgumentsList.clear();
			sqlArgumentsList.add(entity.getpId());
			sqlArgumentsList.add(entity.getActivityDate());
			sqlArgumentsList.add(entity.getDailyStepData().getValue());
			sqlArgumentsList.add(entity.getDailyStepData().getValue());
			query += Integer.toString(i);
			dataToBeStoredMap.put(query, sqlArgumentsList);
		}
		return dataToBeStoredMap;
	}

	public static AppData pollFitbitForAUser(String p_id, String access_token) {
		String lastDate = PatUtils.getCurrentFormattedDate();
		return FitbitDataRetriever.pullFitbitDataForMobileApp(p_id, lastDate, access_token);

	}

	public static AppData pullFitbitDataForMobileApp(String pId, String lastDate, String accessToken) {
		AppData appData = new AppData();
		ActivitiesSteps activityData = FitbitDataRetriever.getActivityData(pId, lastDate, accessToken);
		List<Device> deviceInformation = FitbitDataRetriever.getDeviceInformation(pId, accessToken);
		SleepActivity sleepData = FitbitDataRetriever.getSleepData(pId, lastDate, accessToken);
		if (activityData != null && deviceInformation != null && sleepData != null) {
			appData = FitbitMapper.mapMobileAppDataStructure(pId, activityData, deviceInformation, sleepData);
			return appData;
		}
		return null;
	}
}
