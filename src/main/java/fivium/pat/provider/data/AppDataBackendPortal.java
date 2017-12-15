package fivium.pat.provider.data;

import java.util.List;

import fivium.pat.datamodel.providers.fitbit.DailyHeart;
import fivium.pat.datamodel.providers.fitbit.DailySteps;
import fivium.pat.datamodel.providers.fitbit.Device;
import fivium.pat.datamodel.providers.fitbit.SleepDetails;

public class AppDataBackendPortal {

	private String pId;
	private String activityDate;
	private DailySteps dailyStepData;
	private SleepDetails dailySleepData;
	private DailyHeart dailyHeartData;
	private List<Device> deviceData;
	private String lastSyncDate;

	public String getLastSyncDate() {
		return lastSyncDate;
	}

	public void setLastSyncDate(String lastSyncDate) {
		this.lastSyncDate = lastSyncDate;
	}

	public DailySteps getDailyStepData() {
		return dailyStepData;
	}

	public void setDailyStepData(DailySteps dailyStepData) {
		this.dailyStepData = dailyStepData;
	}

	public SleepDetails getDailySleepData() {
		return dailySleepData;
	}

	public String getpId() {
		return pId;
	}

	public void setpId(String pId) {
		this.pId = pId;
	}

	public String getActivityDate() {
		return activityDate;
	}

	public void setActivityDate(String activityDate) {
		this.activityDate = activityDate;
	}

	public void setDailySleepData(SleepDetails dailySleepData) {
		this.dailySleepData = dailySleepData;
	}

	public DailyHeart getDailyHeartData() { return dailyHeartData; };

	public void setDailyHeartData(DailyHeart dailyHeartData) { this.dailyHeartData = dailyHeartData; }

	public List<Device> getDeviceData() {
		return deviceData;
	}

	public void setDeviceData(List<Device> deviceData) {
		this.deviceData = deviceData;
	}

}
