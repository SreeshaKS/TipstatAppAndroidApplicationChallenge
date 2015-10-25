package com.hiringchallenge.hackerearth.tipstatapp.APIData.DetailedData;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;


public class FavouritesHandler  {

    private JSONObject favourites;

    public JSONObject getFavourites(File file){
        new AsyncTask<File, Void, String>() {
            @Override
            protected String doInBackground(File... params) {
                favourites = readFavouritesFromFile(params[0]);
                return null;
            }
        }.execute(file);
        return favourites;
    }
    public void saveFavourites(JSONObject favourites , final File file){
        new AsyncTask<JSONObject, Void, String>() {
            @Override
            protected String doInBackground(JSONObject... params) {
                writeFavouritesToFile(params[0], file);
                return null;
            }
        }.execute(favourites);
    }

    public JSONObject readFavouritesFromFile(File messageFile) {


        FileInputStream fileInputStream = null;
        String favouriteString = "{\"default_id\":\"0\"}";


            try {
                fileInputStream = new FileInputStream(messageFile);
                int read = -1;
                StringBuffer stringBuffer = new StringBuffer();
                while ((read = fileInputStream.read()) != -1) {
                    stringBuffer.append((char) read);
                }

                favouriteString = stringBuffer.toString();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (fileInputStream != null) {
                    try {
                        fileInputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

        try {
            JSONObject favouritesObject = new JSONObject(favouriteString);
            return favouritesObject;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void writeFavouritesToFile(JSONObject favourites , File favouritesFile) {

        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(favouritesFile);
            fileOutputStream.write(favourites.toString().getBytes());
            Log.d("Favourites Handler"
                    , favouritesFile.getAbsolutePath());

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
