package fivium.pat.datamodel.providers.fitbit;

import java.util.ArrayList;

public class HeartValue {

    private ArrayList<String> customHeartRateZones;
    private ArrayList<HeartRateZone> heartRateZones;
    private int restingHeartRate;

    public ArrayList<String> getCustomHeartRateZones() { return customHeartRateZones; }
    public void setCustomHeartRateZones(ArrayList<String> customHeartRateZones) { this.customHeartRateZones = customHeartRateZones; }

    public ArrayList<HeartRateZone> getHeartRateZones() { return heartRateZones; }
    public void setHeartRateZones(ArrayList<HeartRateZone> heartRateZones) { this.heartRateZones = heartRateZones; }

    public int getRestingHeartRate() { return restingHeartRate; }
    public void setRestingHeartRate(int restingHeartRate) { this.restingHeartRate = restingHeartRate; }

}
