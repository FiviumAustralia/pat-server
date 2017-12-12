package fivium.pat.utils;

import java.io.File;
import java.security.SecureRandom;
import java.util.Random;

public final class Constants {

	public static final String FITBIT_USERS = "SELECT p_id, provider_refresh_token FROM patient WHERE provider = 'fitbit' AND provider_refresh_token !=''";
	
	public static final String GET_SINGLE_FITBIT_USER = "SELECT provider_refresh_token FROM patient WHERE provider = 'fitbit' AND p_id = ?";

	public static final String GET_CLINICIAN_COMPANY = "SELECT Company FROM clinicians WHERE Email = ?";

	public static final String LAST_FETCHED_DATE = "SELECT p_id, MAX(last_sync_date) AS \"date\" FROM fitness_data WHERE p_id=?";

	public static final String SAVE_FITBIT_DATA = "INSERT INTO fitness_data VALUES(?, ?, ?, ?) ON DUPLICATE KEY UPDATE data=?";
	
	public static final String SAVE_FITBIT_SLEEP = "INSERT INTO sleepdata values (?,?,?,?,?,?,?,?,?) ON DUPLICATE KEY UPDATE duration=?, efficiency=?, start_time=?, end_time=?, minutes_asleep=?, minutes_awake=?, minutes_restless=?;";
	
	public static final String SAVE_FITBIT_STEPS = "INSERT INTO stepdata VALUES(?, ?, ?) ON DUPLICATE KEY UPDATE steps=?";
	
	public static final String SAVE_FITBIT_NOTIFICATION_DATA = "INSERT INTO notifications VALUES(?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE p_id=?";
	
	public static final String FITBIT_REFRESH_TOKEN_ENDPOINT = "https://api.fitbit.com/oauth2/token";
	
	public final static String FITBIT_ACTIVITY_TIME_SERIES_ENDPOINT = "https://api.fitbit.com/1/user/-/activities/steps/date/${baseDate}/${endDate}.json";
	
	public final static String FITBIT_DEVICE_INFO_ENDPOINT = "https://api.fitbit.com/1/user/-/devices.json";
	
	public final static String FITBIT_SLEEP_LOG_LIST_ENDPOINT = "https://api.fitbit.com/1.2/user/-/sleep/date/${startDate}/${endDate}.json";
	
	public static final String STORE_REFRESH_TOKEN = "UPDATE patient SET provider_refresh_token=? WHERE p_id=?";
	
	public static final String GET_NOTIFICATION_USER_LIST = "SELECT notifications.p_id, notification_text, notification_date, firebase_device_token"
																													+ " FROM notifications INNER JOIN patient ON notifications.p_id = patient.p_id "
																													+ "WHERE notification_delivery_status LIKE 'false'";

	public static final String FITBIT_ACCESS_ERROR_NOTIFICATION_USER_LIST = "SELECT p_id, firebase_device_token FROM patient WHERE p_id = ?";

	public static final String UPDATE_NOTIFICATION_DELIVERY_STATUS = "UPDATE notifications SET notification_delivery_status = 'true' WHERE p_id = ? AND notification_text=? AND notification_date= ?";
	public static final String DECODED_PATIENT_ID_KEY = "__DECDOED__p_id";
	
	public static final byte[] JWT_KEY = "RNS".getBytes();
	public static final String JWT_GRAPHQL_QUERY_PARAM = "jwt_token";
	
	public static final Random RANDOM = new SecureRandom();
	
	public static final int ITERATIONS = 10000;
	
	public static final int KEY_LENGTH = 256;

	// System property: user.home = /root (when logged in as root)
	public static final String RNS_ROOT_PATH = System.getProperty("user.home") + File.separatorChar + "rns"
			+ File.separatorChar;
	//"C:\Users\[username]\rns\props"
	public static final String RNS_PROPS_PATH = RNS_ROOT_PATH + "props" + File.separatorChar;
	
	public static final String FITBIT_CLIENT_SECRET = "MjJDS0NXOmQ5MjU4NzZmMTk1YzAyYjdjMzJiYjM3YTY4YzAyM2Q2";
	public static final String FITBIT_CLIENT_ID = "22CKCW";
	public static final String FITBIT_REDIRECT_URI = "mppy%3A%2F%2Ffit";
	
	public static final String FIREBASE_NOTIFICATION_API = "https://fcm.googleapis.com/fcm/send";
		
	public static final String MOBILE_APP_LOGIN_CONTEXT = "mobile_app_login";
	public static final String CLINICIAN_PORTAL_LOGIN_CONTEXT = "clinician_portal_login";
	
}
