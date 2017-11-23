package fivium.pat.datamodel.providers.fitbit;

import com.google.gson.annotations.SerializedName;

public class SleepData {

	private String dateTime;
	private String level;
	@SerializedName("seconds")
	private double secondsAsleep;

	public String getDateTime() {
		return dateTime;
	}
	public void setDateTime(String dateTime) {
		this.dateTime = dateTime;
	}
	public String getLevel() {
		return level;
	}
	public void setLevel(String level) {
		this.level = level;
	}
	public double getSecondsASleep() {
		return secondsAsleep;
	}
	public void setSecondsASleep(double secondsASleep) {
		this.secondsAsleep = secondsASleep;
	}
	
	
	
}
