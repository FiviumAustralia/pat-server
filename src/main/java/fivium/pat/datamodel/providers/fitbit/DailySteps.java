package fivium.pat.datamodel.providers.fitbit;

public class DailySteps {
  private String dateTime;
  private int value;

  public String getDateTime() {
    return dateTime;
  }

  public void setDateTime(String dateTime) {
    this.dateTime = dateTime;
  }

  public int getValue() {
    return value;
  }

  public void setValue(int value) {
    this.value = value;
  }
}
