package fivium.pat.datamodel.providers.fitbit;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ActivitiesSteps {
  @SerializedName("activities-steps")
  private ArrayList<DailySteps> steps;

  public void setSteps(ArrayList<DailySteps> steps) {
    this.steps = steps;
  }

  public ArrayList<DailySteps> getSteps() {
    return steps;
  }


}
