package fivium.pat.datamodel.providers.fitbit;

import com.google.gson.annotations.SerializedName;

public class SleepSummaryData {

	@SerializedName("count")
	private int count;
	@SerializedName("minutes")
	private int minutes;
	@SerializedName("thirtyDayAvgMinutes")
	private int thirtyDayAvgMinutes;

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public int getMinutes() {
		return minutes;
	}

	public void setMinutes(int minutes) {
		this.minutes = minutes;
	}

	public int getThirtyDayAvgMinutes() {
		return thirtyDayAvgMinutes;
	}

	public void setThirtyDayAvgMinutes(int thirtyDayAvgMinutes) {
		this.thirtyDayAvgMinutes = thirtyDayAvgMinutes;
	}

}
