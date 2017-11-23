package fivium.pat.datamodel.providers.fitbit;

import java.util.ArrayList;

import com.google.gson.annotations.SerializedName;

public class SleepActivity {

	 @SerializedName("sleep")
	  private ArrayList<SleepDetails> sleepDetails;

	 public ArrayList<SleepDetails> getSleepData() {
	 		return sleepDetails;
	 }

	 public void setSleepDetails(ArrayList<SleepDetails> sleepDetails) {
	 		this.sleepDetails = sleepDetails;
	 }
		
}
