package fivium.pat.datamodel.providers.fitbit;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Device {
  private String battery;
  private String deviceVersion;
  private String id;
  private String lastSyncTime;
  private String mac;
  @SerializedName("type")
  private String deviceType;
  private ArrayList<String> features;

  public String getBattery() {
    return battery;
  }

  public void setBattery(String battery) {
    this.battery = battery;
  }

  public String getDeviceVersion() {
    return deviceVersion;
  }

  public void setDeviceVersion(String deviceVersion) {
    this.deviceVersion = deviceVersion;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getLastSyncTime() {
    return lastSyncTime;
  }

  public void setLastSyncTime(String lastSyncTime) {
    this.lastSyncTime = lastSyncTime;
  }

  public String getMac() {
    return mac;
  }

  public void setMac(String mac) {
    this.mac = mac;
  }

  public String getDeviceType() {
    return deviceType;
  }

  public void setDeviceType(String deviceType) {
    this.deviceType = deviceType;
  }

  public ArrayList<String> getFeatures() {
    return features;
  }

  public void setFeatures(ArrayList<String> features) {
    this.features = features;
  }
}
