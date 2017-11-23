package fivium.pat.datamodel.providers.fitbit;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class Levels {

	@SerializedName("data")
	private List<SleepData> sleepData;
	@SerializedName("summary")
	private SleepSummary sleepSummary;

	public List<SleepData> getSleepData() {
		return sleepData;
	}

	public void setSleepData(List<SleepData> sleepData) {
		this.sleepData = sleepData;
	}

	public SleepSummary getSleepSummary() {
		return sleepSummary;
	}

	public void setSleepSummary(SleepSummary sleepSummary) {
		this.sleepSummary = sleepSummary;
	}

}
