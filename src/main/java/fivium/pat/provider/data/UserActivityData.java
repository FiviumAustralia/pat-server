package fivium.pat.provider.data;

import java.util.Date;

public class UserActivityData {

	private String activityDate;
	private int totalSteps;
	private int totalSleepTime;
	private int sleepEfficiency;
	private float averageHeartRate;

	public String getActivityDate() {
		return activityDate;
	}

	public void setActivityDate(String activityDate) {
		this.activityDate = activityDate;
	}

	public int getTotalSteps() {
		return totalSteps;
	}

	public void setTotalSteps(int totalSteps) {
		this.totalSteps = totalSteps;
	}

	public int getTotalSleepTime() {
		return totalSleepTime;
	}

	public void setTotalSleepTime(int totalSleepTime) {
		this.totalSleepTime = totalSleepTime;
	}

	public int getSleepEfficiency() {
		return sleepEfficiency;
	}

	public void setSleepEfficiency(int sleepEfficiency) {
		this.sleepEfficiency = sleepEfficiency;
	}

	public float getAverageHeartRate() {
		return averageHeartRate;
	}

	public void setAverageHeartRate(float averageHeartRate) {
		this.averageHeartRate = averageHeartRate;
	}

}
