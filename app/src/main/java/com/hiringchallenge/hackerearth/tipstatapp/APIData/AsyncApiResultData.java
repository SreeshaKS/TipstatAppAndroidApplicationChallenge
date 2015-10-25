package com.hiringchallenge.hackerearth.tipstatapp.APIData;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Sreesha on 03-10-2015.
 */
public interface AsyncApiResultData {

    void onMemberListResult(JSONObject memberListArray);
}
