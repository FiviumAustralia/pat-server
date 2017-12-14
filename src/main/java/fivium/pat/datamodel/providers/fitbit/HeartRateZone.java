package fivium.pat.datamodel.providers.fitbit;

public class HeartRateZone {
    private float caloriesOut;
    private int max;
    private int min;
    private int minutes;
    private String name;

    public void setCaloriesOut(float caloriesOut) { this.caloriesOut = caloriesOut; }
    public float getCaloriesOut() { return caloriesOut; }

    public void setMax(int max) { this.max = max; }
    public int getMax() { return max; }

    public void setMin(int min) { this.min = min; }
    public int getMin() { return min; }

    public void setMinutes(int minutes) { this.minutes = minutes; }
    public int getMinutes() { return minutes; }

    public void setName(String name) { this.name = name; }
    public String getName() { return name; }
}
