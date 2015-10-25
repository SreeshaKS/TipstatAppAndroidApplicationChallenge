package com.hiringchallenge.hackerearth.tipstatapp.APIData;

public class APIURLList {

    public static final String FETCH_MEMBER_DETAILS_URL = "http://tipstat.0x10.info/api/tipstat?type=json&query=list_status";
    public static final String API_HITS_URL = "http://tipstat.0x10.info/api/tipstat?type=json&query=api_hits";

    public static class parameters{
        public static final String ID="id";
        public static final String DOB="dob";
        public static final String STATUS="status";
        public static final String ETHNICITY="ethnicity";
        public static final String WEIGHT="weight";
        public static final String HEIGHT="height";
        public static final String IS_VEG="is_veg";
        public static final String DRINK="drink";
        public static final String IMAGE_URL="image";
    }

}
