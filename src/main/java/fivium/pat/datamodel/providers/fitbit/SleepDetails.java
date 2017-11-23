package fivium.pat.datamodel.providers.fitbit;

import java.util.Date;
import java.util.List;

import com.google.gson.annotations.SerializedName;

public class SleepDetails {

	private String dateOfSleep;
	private int duration;
	private int efficiency;
	private boolean isMainSleep;
	private String endTime;
	private double logId;
	private Levels levels;
	private float minutesAfterWakeup;
	private float minutesAsleep;
	private float minutesAwake;
	private float minutesToFallAsleep;
	private String startTime;
	private float timeInBed;
	private String type;

	public String getDateOfSleep() {
		return dateOfSleep;
	}

	public void setDateOfSleep(String dateOfSleep) {
		this.dateOfSleep = dateOfSleep;
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public int getEfficiency() {
		return efficiency;
	}

	public void setEfficiency(int efficiency) {
		this.efficiency = efficiency;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public double setLogId() {
		return logId;
	}

	public void setLogId(double infoCode) {
		this.logId = infoCode;
	}

	public Levels getLevels() {
		return levels;
	}

	public void setLevels(Levels levels) {
		this.levels = levels;
	}

	public float getMinutesAfterWakeup() {
		return minutesAfterWakeup;
	}

	public void setMinutesAfterWakeup(float minutesAfterWakeup) {
		this.minutesAfterWakeup = minutesAfterWakeup;
	}

	public float getMinutesAsleep() {
		return minutesAsleep;
	}

	public void setMinutesAsleep(float minutesAsleep) {
		this.minutesAsleep = minutesAsleep;
	}

	public float getMinutesAwake() {
		return minutesAwake;
	}

	public void setMinutesAwake(float minutesAwake) {
		this.minutesAwake = minutesAwake;
	}

	public float getMinutesToFallAsleep() {
		return minutesToFallAsleep;
	}

	public void setMinutesToFallAsleep(float minutesToFallAsleep) {
		this.minutesToFallAsleep = minutesToFallAsleep;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public float getTimeInBed() {
		return timeInBed;
	}

	public void setTimeInBed(float timeInBed) {
		this.timeInBed = timeInBed;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public boolean isMainSleep() {
		return isMainSleep;
	}

	public void setMainSleep(boolean isMainSleep) {
		this.isMainSleep = isMainSleep;
	}

	public SleepDetails(String dateOfSleep, int duration, int efficiency, boolean isMainSleep, String endTime,
			double logId, Levels levels, float minutesAfterWakeup, float minutesAsleep, float minutesAwake,
			float minutesToFallAsleep, String startTime, float timeInBed, String type) {
		super();
		this.dateOfSleep = dateOfSleep;
		this.duration = duration;
		this.efficiency = efficiency;
		this.isMainSleep = isMainSleep;
		this.endTime = endTime;
		this.logId = logId;
		this.levels = levels;
		this.minutesAfterWakeup = minutesAfterWakeup;
		this.minutesAsleep = minutesAsleep;
		this.minutesAwake = minutesAwake;
		this.minutesToFallAsleep = minutesToFallAsleep;
		this.startTime = startTime;
		this.timeInBed = timeInBed;
		this.type = type;
	}
}
