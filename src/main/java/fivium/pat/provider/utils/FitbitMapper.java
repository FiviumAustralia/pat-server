package fivium.pat.provider.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;

import fivium.pat.datamodel.providers.fitbit.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import fivium.pat.provider.data.ActivityData;
import fivium.pat.provider.data.AppData;
import fivium.pat.provider.data.AppDataBackendPortal;
import fivium.pat.provider.data.UserActivityData;
import fivium.pat.utils.PatUtils;

public class FitbitMapper {

	private static Log logger = LogFactory.getLog(FitbitMapper.class);

	public static AppData mapMobileAppDataStructure(String pId, ActivitiesSteps activityData,
			List<Device> deviceInformation, SleepActivity sleepData) {
		AppData appdata = new AppData();
		List<UserActivityData> userActivityData = new ArrayList<UserActivityData>();
		// go through steps retrieved from Fitbit
		for (DailySteps steps : activityData.getSteps()) {
			UserActivityData activity = new UserActivityData();
			activity.setTotalSteps(steps.getValue());
			activity.setActivityDate(steps.getDateTime());
			// find matching sleep for this date
			for (SleepDetails thissleep : sleepData.getSleepData()) {
				if (thissleep.getDateOfSleep().equals(steps.getDateTime())) {
					activity.setTotalSleepTime((int) Math.round(thissleep.getDuration()));
					activity.setSleepEfficiency(thissleep.getEfficiency());
					break;
				}
			}
			userActivityData.add(activity);
		}
		ActivityData activity_data = new ActivityData();
		activity_data.setUserActivityData(userActivityData);
		appdata.setActivityData(activity_data);
		return appdata;
	}

	/*
	 * This method creates a data structure from the Fitbit data.
	 */
	public static List<AppDataBackendPortal> mapCliniciansAppDataStructure(String pId, ActivitiesSteps activityData,
																		   List<Device> deviceInformation, SleepActivity sleepData, ActivitiesHeart heartData) {
		logger.info("Entering mapCliniciansAppDataStructure... ");
		// The data is only accepted if a device is associated with the account.

		if (deviceInformation.size() == 0) {
			logger.error("The user " + pId + " does not have any devices associated with their account.");
			return null;
		} else {
			List<AppDataBackendPortal> appDataBackendPortal = new ArrayList<>();
			AppDataBackendPortal dataForTheBackend = new AppDataBackendPortal();
			ArrayList<DailySteps> steps = activityData.getSteps();
			ArrayList<SleepDetails> sleep = sleepData.getSleepData();
			ArrayList<DailyHeart> heartRate = heartData.getHeart();
			Iterator<DailySteps> dailyStepsIterator = steps.iterator();
			Iterator<DailyHeart> dailyHeartIterator = heartRate.iterator();
			Iterator<SleepDetails> sleepDetailsIterator = sleep.iterator();
			// activity list is iterated and the compiled into the structure for the
			// database.
			while (dailyStepsIterator.hasNext()) {
				DailySteps dailySteps = dailyStepsIterator.next();
				dataForTheBackend.setpId(pId);
				dataForTheBackend.setActivityDate(dailySteps.getDateTime());
				dataForTheBackend.setDailyStepData(dailySteps);
				// if any sleep data exists for that date, it is inserted. Otherwise
				// sleep data for the date is null
				while (sleepDetailsIterator.hasNext()) {
					SleepDetails dailySleepData = sleepDetailsIterator.next();
					if (dailySteps.getDateTime().equalsIgnoreCase(dailySleepData.getDateOfSleep())) {
						dataForTheBackend.setDailySleepData(dailySleepData);
					}
				}
				// if heart rate data exists for date, it is inserted. Otherwise
				// heart data for date is null
				while(dailyHeartIterator.hasNext()) {
					DailyHeart dailyHeartData = dailyHeartIterator.next();
					if(dailySteps.getDateTime().equalsIgnoreCase(dailyHeartData.getDateTime())) {
						dataForTheBackend.setDailyHeartData(dailyHeartData);
					}
				}

				dataForTheBackend.setDeviceData(deviceInformation);
				/*
				 * Fitbit provides the last sync time in the format
				 * "Date - T- TimeStamp", since we only need the date, we are splitting
				 * the string returned and formatting the date as per our requirements.
				 * Time Received.
				 * 2017-10-24T17:00:44.000
				 * Time Split
				 * 2017-10-24
				 */
				String lastSyncDate = "";
				for (Device device : deviceInformation) {
					if ("Tracker".equalsIgnoreCase(device.getDeviceType()) && "".equalsIgnoreCase(lastSyncDate)) {
						lastSyncDate = device.getLastSyncTime();
					} else {
						// check which is the more recent timestamp
						lastSyncDate = PatUtils.returnLatestTimeStamp(lastSyncDate.replace("T", " "),
								device.getLastSyncTime().replace("T", " "));
					}
				}
				dataForTheBackend
				.setLastSyncDate(PatUtils.getFormattedDate(lastSyncDate));
				appDataBackendPortal.add(dataForTheBackend);
				dataForTheBackend = new AppDataBackendPortal();
			}
			return appDataBackendPortal;

		}
	}
}
