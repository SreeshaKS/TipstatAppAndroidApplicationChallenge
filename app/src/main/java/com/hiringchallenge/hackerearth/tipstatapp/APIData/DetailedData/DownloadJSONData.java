package com.hiringchallenge.hackerearth.tipstatapp.APIData.DetailedData;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.hiringchallenge.hackerearth.tipstatapp.APIData.AsyncApiResultData;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class DownloadJSONData extends AsyncTask<String, Void, String> {

    AsyncApiResultData callback;
    ProgressDialog mProgressDialog;
    Context context;

    public DownloadJSONData(AsyncApiResultData callback, Context context) {
        this.callback = callback;
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mProgressDialog = new ProgressDialog(context);
        mProgressDialog.show();
        mProgressDialog.setMessage("FetchingData ......Please Wait !! ");
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setCancelable(true);
        mProgressDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                mProgressDialog.dismiss();
            }
        });
    }

    @Override
    protected String doInBackground(String... urls) {
        try {
            return downloadUrl(urls[0]);
        } catch (IOException e) {
            return "Unable to download the requested page.";
        }
    }

    @Override
    protected void onPostExecute(String s) {
        Log.e("INONPostExecute", s);
        try {
            JSONObject resultJSONData = new JSONObject(s);

            callback.onMemberListResult(resultJSONData);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        mProgressDialog.dismiss();
        super.onPostExecute(s);
    }

    private String downloadUrl(String urlString) throws IOException {
        InputStream is = null;

        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            // Starts the query
            conn.connect();
            int responseCode = conn.getResponseCode();
            is = conn.getInputStream();

            String contentAsString = convertStreamToString(is);
            Log.e("StringJSON", contentAsString);
            return contentAsString;
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    private String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
}
