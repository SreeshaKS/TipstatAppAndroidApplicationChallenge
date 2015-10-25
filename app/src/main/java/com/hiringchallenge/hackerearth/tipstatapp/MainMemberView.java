package com.hiringchallenge.hackerearth.tipstatapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.hiringchallenge.hackerearth.tipstatapp.APIData.APIURLList;
import com.hiringchallenge.hackerearth.tipstatapp.APIData.AsyncApiResultData;
import com.hiringchallenge.hackerearth.tipstatapp.APIData.DetailedData.APIParameters;
import com.hiringchallenge.hackerearth.tipstatapp.APIData.DetailedData.CustomAdapter;
import com.hiringchallenge.hackerearth.tipstatapp.APIData.DetailedData.DownloadJSONData;
import com.hiringchallenge.hackerearth.tipstatapp.APIData.DetailedData.FavouritesHandler;
import com.hiringchallenge.hackerearth.tipstatapp.APIData.DetailedData.member;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class MainMemberView extends AppCompatActivity {
    /*RecyclerViewData*/

    private static final String KEY_LAYOUT_MANAGER = "layoutManager";
    private static final int SPAN_COUNT = 2;
    private static final int DATASET_COUNT = 60;
    private ArrayList<String> messageFileList;
    private ArrayList<JSONObject> finalParsedMessageList;
    SharedPreferences favouritesPreferences;

    private enum LayoutManagerType {
        GRID_LAYOUT_MANAGER,
        LINEAR_LAYOUT_MANAGER
    }

    TextView apiHitsTextView;
    TextView resultNumberTextView;

    protected LayoutManagerType mCurrentLayoutManagerType;
    protected RecyclerView mRecyclerView;
    protected CustomAdapter mAdapter;
    protected RecyclerView.LayoutManager mLayoutManager;
    protected String[] mDataSet;

    /*RecyclerViewDataEnds*/

    static Spinner ethnicityDataSpinner;

    String[] ethnicityData = {
            "Ethnicity : Asian"
            , "Ethnicity : Indian"
            , "Ethnicity : African American"
            , "Ethnicity : Asian Americans"
            , "Ethnicity : European"
            , "Ethnicity : British"
            , "Ethnicity : Jewish"
            , "Ethnicity : Latino"
            , "Ethnicity : Native American"
            , "Ethnicity : Arabic"
    };

    JSONObject resultJSONData;
    APIURLList urlList;
    ArrayList<member> globalMemberList;
    ArrayList<member> ethnicityMemberList;
    Context context;
    JSONObject favourites;
    EditText searchText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_member_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        apiHitsTextView = (TextView) findViewById(R.id.apiHitsTextView);
        resultNumberTextView = (TextView) findViewById(R.id.resultNumberTextView);
        toolbar.setTitle("");
        context = getApplicationContext();
        searchText = (EditText) findViewById(R.id.searchText);

        ethnicityDataSpinner = (Spinner) findViewById(R.id.ethnicityDataSpinner);


        downloadData();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        final FloatingActionButton sortByHeightFab = (FloatingActionButton) findViewById(R.id.sortByHeightFab);
        FloatingActionButton sortByWeightFab = (FloatingActionButton) findViewById(R.id.sortByWeightFab);
        FloatingActionButton favouritesFab = (FloatingActionButton) findViewById(R.id.favouritesFab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ethnicityMemberList = globalMemberList;
                initialiseRecyclerView(globalMemberList);
                resultNumberTextView.setText("\t" + globalMemberList.size() + " Members Found");
                Snackbar.make(view, "All members Refreshed", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                new DownloadJSONData(new AsyncApiResultData() {
                    @Override
                    public void onMemberListResult(JSONObject object) {


                        try {
                            apiHitsTextView.setText("\t" + object.get("api_hits").toString()
                                            + "  Api Hits"
                            );
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
                        , MainMemberView.this
                ).execute(
                        urlList.API_HITS_URL
                );
            }
        });
        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Log.e("beforeTExt", s + "\n" +
                        "start: " + start +
                        "\ncount : " + count
                        + "\nafter :" + after + "\n" + searchText.getText());
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.e("onTExt", s + "\n" +
                        "start: " + start +
                        "\ncount : " + count
                        + "\nbefore :" + before + "\n" + searchText.getText());
            }

            @Override
            public void afterTextChanged(final Editable s) {
                try {
                    new AsyncTask<Void, Void, Void>() {

                        ArrayList<member> asyncList = new ArrayList<member>();

                        @Override
                        protected void onPreExecute() {
                            Log.e("ASYNC TASK", "" + s);
                            super.onPreExecute();
                        }

                        @Override
                        protected Void doInBackground(Void... params) {
                            try {
                                Double.parseDouble(s.toString());
                                APIParameters parameter = new APIParameters();
                                for (member temp : globalMemberList) {

                                    if (temp.getMemberDetails().getString(
                                            parameter.WEIGHT
                                    ).contains(s.toString()) || temp.getMemberDetails().getString(
                                            parameter.HEIGHT
                                    ).contains(s.toString())) {
                                        try {
                                            asyncList.add(temp);
                                        } catch (NullPointerException ne) {

                                        }
                                    }
                                }
                                try {
                                    Log.e("Double", asyncList.size() + "");
                                } catch (NullPointerException ne) {

                                }


                            } catch (Exception e) {
                                APIParameters parameter = new APIParameters();
                                for (member temp : globalMemberList) {

                                    try {
                                        if (temp.getMemberDetails().getString(
                                                parameter.STATUS
                                        ).contains(s.toString())) {
                                            try {
                                                asyncList.add(temp);
                                            } catch (NullPointerException ne) {

                                            }
                                        }
                                    } catch (JSONException e1) {
                                        e1.printStackTrace();
                                    }
                                }
                                try {
                                    Log.e("Status", asyncList.size() + "");
                                } catch (NullPointerException lne) {

                                }
                            }
                            return null;
                        }

                        @Override
                        protected void onPostExecute(Void aVoid) {
                            try {
                                if (!asyncList.isEmpty()) {
                                    try {
                                        Log.e("onPost", asyncList.size() + "");
                                    } catch (NullPointerException lne) {

                                    }

                                    initialiseRecyclerView(asyncList);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            super.onPostExecute(aVoid);
                        }
                    }.execute();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Log.e("afterTExt", s + "\n" + searchText.getText());
            }
        });
        sortByHeightFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Snackbar.make(view, "SortingByHeight", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                Collections.sort(ethnicityMemberList, member.compareByHeight);
                initialiseRecyclerView(ethnicityMemberList);
                Snackbar.make(view, "Sorting By Height", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        sortByWeightFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Collections.sort(ethnicityMemberList, member.compareByWeight);
                initialiseRecyclerView(ethnicityMemberList);
                Snackbar.make(view, "Sorting By Weight", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        favouritesFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                final ArrayList<member> favouriteMembers = new ArrayList<member>();
                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... params) {
                        try {
                            if (favourites != null) {
                                String favouriteMemberString = favourites.toString();
                                for (member temp : globalMemberList) {
                                    String id = temp.getMemberDetails().get("id").toString();
                                    if (favouriteMemberString.contains(id)) {
                                        favouriteMembers.add(temp);
                                    }
                                }
                            } else {
                                Snackbar.make(v, "No Favourites", Snackbar.LENGTH_LONG)
                                        .setAction("Action", null).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        super.onPostExecute(aVoid);
                        ethnicityMemberList = favouriteMembers;
                        initialiseRecyclerView(favouriteMembers);
                    }
                }.execute();

            }
        });

        ethnicityDataSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                ArrayList<member> memberList = new ArrayList<member>();
                try {
                    for (int i = 0; i < globalMemberList.size(); i++) {

                        member temp = globalMemberList.get(i);

                        int ethnicity = (int) Double.parseDouble(
                                temp.getMemberDetails().get("ethnicity").toString()
                        );

                        if (ethnicity == position) {
                            memberList.add(temp);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    if (!memberList.isEmpty()) {
                        ethnicityMemberList = memberList;
                        initialiseRecyclerView(ethnicityMemberList);
                    } else
                        initialiseRecyclerView(globalMemberList);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        setUpFavouritesDirectory();
        readFavourites();
    }

    private void readFavourites() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                String path = Environment.getExternalStorageDirectory().getAbsolutePath();
                String favDirectoryPath = path + File.separator
                        + getString(R.string.favouritePreferenceDirectory)
                        + File.separator
                        + getString(R.string.favouritePreferenceTextFile) + ".txt";
                File favouritesStorageFile = new File(favDirectoryPath);
                FavouritesHandler handler = new FavouritesHandler();
                if (favouritesStorageFile.exists()) {
                    favourites = handler.readFavouritesFromFile(favouritesStorageFile);
                }
                return null;
            }
        }.execute();
    }

    private void setUpFavouritesDirectory() {
        favouritesPreferences = context
                .getSharedPreferences(
                        getString(R.string.favouritePreferenceDirectory)
                        , Context.MODE_PRIVATE
                );
        if (!favouritesPreferences.getBoolean(getString(R.string.favDirFlag), false)) {
            String path = Environment.getExternalStorageDirectory().getAbsolutePath();
            String favDirectoryPath = path + File.separator + getString(R.string.favouritePreferenceDirectory);
            File rootStorageFile = new File(favDirectoryPath);
            Log.e("FILE PATH", favDirectoryPath);

            if (!rootStorageFile.mkdir()) {
                Toast.makeText(context,
                        "Error in creating Student Directory : Please Insert SD Card", Toast.LENGTH_LONG).show();
            } else {
                Log.e("ERRORINDIR", "DirectoryCreatedAgain");
                Toast.makeText(MainMemberView.this, "FavouritesDirectoryCreated", Toast.LENGTH_SHORT).show();
                SharedPreferences.Editor editor = favouritesPreferences.edit();
                editor.putBoolean(getString(R.string.favDirFlag), true);
                editor.apply();
            }

        } else {

        }

    }


    private void downloadData() {
        new DownloadJSONData(new AsyncApiResultData() {
            @Override
            public void onMemberListResult(JSONObject object) {

                resultJSONData = object;
                globalMemberList = createMemberList(resultJSONData);
                resultNumberTextView.setText(globalMemberList.size() + " Members Found");
                ethnicityMemberList = globalMemberList;

                if (!globalMemberList.isEmpty()) {
                    initialiseRecyclerView(globalMemberList);
                }
                ArrayAdapter<String> ethnicityDataAdapter
                        = new ArrayAdapter<String>(
                        MainMemberView.this, android.R.layout.simple_spinner_item, ethnicityData
                );
                ethnicityDataSpinner.setAdapter(ethnicityDataAdapter);
            }
        }
                , MainMemberView.this
        ).execute(
                urlList.FETCH_MEMBER_DETAILS_URL
        );
        new DownloadJSONData(new AsyncApiResultData() {
            @Override
            public void onMemberListResult(JSONObject object) {


                try {
                    apiHitsTextView.setText(object.get("api_hits").toString()
                                    + "Api Hits"
                    );
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
                , MainMemberView.this
        ).execute(
                urlList.API_HITS_URL
        );

    }

    private ArrayList<member> createMemberList(JSONObject resultJSONData) {
        ArrayList<member> memberList = new ArrayList<>();
        try {
            member temp;
            JSONArray memberArray = resultJSONData.getJSONArray("members");

            for (int i = 0; i < memberArray.length(); i++) {
                temp = new member(memberArray.getJSONObject(i));
                //Log.e("memberDetails", temp.toString() + "\n");
                memberList.add(temp);
            }
        } catch (JSONException e) {

        }
        return memberList;
    }

    private void initialiseRecyclerView(ArrayList<member> memberList) {

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);


        mLayoutManager = new LinearLayoutManager(getApplicationContext());

        mCurrentLayoutManagerType = LayoutManagerType.LINEAR_LAYOUT_MANAGER;

        setRecyclerViewLayoutManager();

        mAdapter = new CustomAdapter(memberList, MainMemberView.this);
        mRecyclerView.setAdapter(mAdapter);
    }

    public void setRecyclerViewLayoutManager() {
        int scrollPosition = 0;


//        if (mRecyclerView.getLayoutManager() != null) {
//            scrollPosition = ((LinearLayoutManager) mRecyclerView.getLayoutManager())
//                    .findFirstCompletelyVisibleItemPosition();
//        }

        mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mCurrentLayoutManagerType = LayoutManagerType.LINEAR_LAYOUT_MANAGER;


        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.scrollToPosition(scrollPosition);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_member_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public CustomAdapter getmAdapter() {
        return mAdapter;
    }

    @Override
    protected void onResume() {
        super.onResume();
        readFavourites();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        readFavourites();
    }
}
