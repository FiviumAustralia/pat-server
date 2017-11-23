package fivium.pat.datamodel.providers.fitbit;

import com.google.gson.annotations.SerializedName;

public class SleepSummary {

	@SerializedName("asleep")
	private SleepSummaryData sleepingTime;
	@SerializedName(value="wake", alternate={"awake"})
	private SleepSummaryData aWakeTime;
	@SerializedName("restless")
	private SleepSummaryData restlessTime;
	@SerializedName("deep")
	private SleepSummaryData deepSleep;
	@SerializedName("light")
	private SleepSummaryData lightSleep;
	@SerializedName("rem")
	private SleepSummaryData remSleep;
	
	public SleepSummaryData getSleepingTime() {
		return sleepingTime;
	}
	public void setSleepingTime(SleepSummaryData sleepingTime) {
		this.sleepingTime = sleepingTime;
	}
	public SleepSummaryData getaWakeTime() {
		return aWakeTime;
	}
	public SleepSummaryData getDeepSleep() {
		return deepSleep;
	}
	public void setDeepSleep(SleepSummaryData deepSleep) {
		this.deepSleep = deepSleep;
	}
	public SleepSummaryData getLightSleep() {
		return lightSleep;
	}
	public void setLightSleep(SleepSummaryData lightSleep) {
		this.lightSleep = lightSleep;
	}
	public SleepSummaryData getRemSleep() {
		return remSleep;
	}
	public void setRemSleep(SleepSummaryData remSleep) {
		this.remSleep = remSleep;
	}
	public void setaWakeTime(SleepSummaryData aWakeTime) {
		this.aWakeTime = aWakeTime;
	}
	public SleepSummaryData getRestlessTime() {
		return restlessTime;
	}
	public void setRestlessTime(SleepSummaryData restlessTime) {
		this.restlessTime = restlessTime;
	}
	
}
