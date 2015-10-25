package com.hiringchallenge.hackerearth.tipstatapp.APIData.DetailedData;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Comparator;
import java.util.Date;

/**
 * Created by Sreesha on 24-10-2015.
 */
public class member  {

    private JSONObject memberDetails;

    public member(JSONObject memberDetails) {
        this.memberDetails = memberDetails;
    }

    public JSONObject getMemberDetails() {
        return memberDetails;
    }


    public static Comparator<member> compareByWeight = new Comparator<member>() {
        @Override
        public int compare(member lhs, member rhs) {
            int lhsWeight = 0;
            int rhsWeight = 0;
            try {
                lhsWeight = (int) Double.parseDouble(
                        lhs.getMemberDetails().get("weight").toString()
                );
                rhsWeight = (int) Double.parseDouble(
                        rhs.getMemberDetails().get("weight").toString()
                );
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return lhsWeight - rhsWeight;
        }
    };
    public static Comparator<member> compareByHeight = new Comparator<member>() {
        @Override
        public int compare(member lhs, member rhs) {
            int lhsHeight = 0;
            int rhsHeight = 0;
            try {
                lhsHeight = (int) Double.parseDouble(
                        lhs.getMemberDetails().get("height").toString()
                );
                rhsHeight = (int) Double.parseDouble(
                        rhs.getMemberDetails().get("height").toString()
                );
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return lhsHeight - rhsHeight;
        }
    };

}
