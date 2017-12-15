package fivium.pat.datamodel.providers.fitbit;

public class DailyHeart {
    private String dateTime;
    private HeartValue value;

    public void setDateTime(String dateTime) { this.dateTime = dateTime; }
    public String getDateTime() { return dateTime; }

    public HeartValue getValue() { return value; }
    public void setValue() { this.value = value; }
}
