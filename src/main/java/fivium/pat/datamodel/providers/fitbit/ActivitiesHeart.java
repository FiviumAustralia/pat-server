package fivium.pat.datamodel.providers.fitbit;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ActivitiesHeart {
    @SerializedName("activities-heart")
    private ArrayList<DailyHeart> heart;

    public void setHeart(ArrayList<DailyHeart> heart) {
        this.heart = heart;
    }

    public ArrayList<DailyHeart> getHeart() {
        return heart;
    }
}
