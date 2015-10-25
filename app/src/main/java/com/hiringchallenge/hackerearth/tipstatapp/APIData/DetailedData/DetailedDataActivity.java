package com.hiringchallenge.hackerearth.tipstatapp.APIData.DetailedData;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.hiringchallenge.hackerearth.tipstatapp.Network.VolleySingleton;
import com.hiringchallenge.hackerearth.tipstatapp.R;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.File;
import java.util.Collections;

public class DetailedDataActivity extends AppCompatActivity {

    JSONObject memberObject;
    TextView statusTextView;
    APIParameters parameters;
    private ImageLoader imageLoader;
    private VolleySingleton volleySingleton;
    ImageView profileImage;

    TextView heightTextView;
    TextView weightTextView;
    TextView isVegTextView;
    TextView drinksTextView;
    TextView birthDayTextView;

    TextView ethnicityTextView;
    AppBarLayout appBar;
    CoordinatorLayout coOrdLayout;
    Toolbar toolbar;
    String[] ethnicityData = {
            "Asian"
            , "Indian"
            , "African American"
            , "Asian Americans"
            , "European"
            , "British"
            , "Jewish"
            , "Latino"
            , "Native American"
            , "Arabic"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_data_actvity);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        statusTextView = (TextView) findViewById(R.id.statusTextView);
        profileImage = (ImageView) findViewById(R.id.profileImage);
        appBar = (AppBarLayout) findViewById(R.id.app_bar);
        coOrdLayout = (CoordinatorLayout) findViewById(R.id.coordinator_layout);

        heightTextView = (TextView) findViewById(R.id.height);
        weightTextView = (TextView) findViewById(R.id.weight);
        isVegTextView = (TextView) findViewById(R.id.is_veg);
        drinksTextView = (TextView) findViewById(R.id.drinks);
        birthDayTextView = (TextView) findViewById(R.id.birthday);
        ethnicityTextView = (TextView) findViewById(R.id.ethnicityTextView);


        volleySingleton = new VolleySingleton(getApplicationContext());
        volleySingleton = VolleySingleton.getsInstance(getApplicationContext());

        imageLoader = volleySingleton.getImageLoader();

        getDataFromIntent();
        loadMemberDataIntoUI();
        loadProfileImage();

        try {
            statusTextView.setText(memberObject.get(parameters.STATUS).toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }


        setSupportActionBar(toolbar);
        toolbar.setTitle("");

        FloatingActionButton shareFab = (FloatingActionButton) findViewById(R.id.shareFab);
        FloatingActionButton favouriteFab = (FloatingActionButton) findViewById(R.id.favourite_fab);
        FloatingActionButton smsFab = (FloatingActionButton) findViewById(R.id.sms_fab);
        shareFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                String sharedOutput = "defaultStatus";
                try {
                    sharedOutput = memberObject.get(parameters.STATUS).toString();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                sendIntent.putExtra(Intent.EXTRA_TEXT, sharedOutput);
                sendIntent.setType("text/plain");
                startActivity(Intent.createChooser(
                        sendIntent, getResources().getText(R.string.send_to
                        )
                ));
                Snackbar.make(view, "SharingStatus", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        smsFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sendIntent = new Intent(Intent.ACTION_VIEW);
                sendIntent.setData(Uri.parse("sms:" + "0000000000"));
                try {
                    sendIntent.putExtra("sms_body", memberObject.get(parameters.STATUS).toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                startActivity(sendIntent);
            }
        });

        favouriteFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String path = Environment.getExternalStorageDirectory().getAbsolutePath();
                String favDirectoryPath = path + File.separator
                        + getString(R.string.favouritePreferenceDirectory)
                        + File.separator
                        + getString(R.string.favouritePreferenceTextFile) + ".txt";
                File favouritesStorageFile = new File(favDirectoryPath);
                FavouritesHandler handler = new FavouritesHandler();
                try {
                    if (favouritesStorageFile.exists()) {

                        JSONObject favourites = handler.readFavouritesFromFile(favouritesStorageFile);

                        if (favourites.toString()
                                .contains(memberObject.get("id").toString())) {
                            Snackbar.make(view, " Already A Favourite", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();

                        } else {
                            favourites.put(memberObject.get("id").toString(), "1");
                            handler.writeFavouritesToFile(favourites, favouritesStorageFile);
                            Snackbar.make(view, " Added to Favourites ", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        }

                    } else {


                        if (true) {
                            JSONObject favourites = new JSONObject();
                            favourites.put(memberObject.get("id").toString(), "1");
                            handler.writeFavouritesToFile(favourites, favouritesStorageFile);
                            Snackbar.make(view, "Favourites Created \n" +"and\n"+
                                    " Added to Favourites ", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        } else {
                            Toast.makeText(DetailedDataActivity.this
                                    , "Error oin creating textFile"
                                    , Toast.LENGTH_SHORT).show();
                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    imageLoader.get(
                            memberObject.get(parameters.IMAGE_URL)
                                    .toString()
                            , new ImageLoader.ImageListener() {
                                @Override
                                public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {

                                    profileImage.setImageBitmap(response.getBitmap());
                                }

                                @Override
                                public void onErrorResponse(VolleyError error) {

                                    Log.e("ImageError", "" + error.getMessage());

                                }
                            });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private void loadMemberDataIntoUI() {
        try {
            heightTextView.setText("HEIGHT : " + memberObject.get(parameters.HEIGHT).toString());
            weightTextView.setText("WEIGHT : " + memberObject.get(parameters.WEIGHT).toString());
            birthDayTextView.setText("BIRTHDAY : " + memberObject.get(parameters.DOB).toString());
            toolbar.setTitle(memberObject.get(parameters.ETHNICITY).toString());
            String is_veg = memberObject.get(parameters.IS_VEG).toString();
            String drinks = memberObject.get(parameters.DRINK).toString();

            int ethnicity = (int) Double.parseDouble(
                    memberObject.get(parameters.ETHNICITY).toString());
            ethnicityTextView.setText(ethnicityData[ethnicity]
            );

            if (is_veg.contains("1")) {
                isVegTextView.setText("Vegetarian ? : YES!");
                isVegTextView.setTextColor(getResources().getColor(R.color.md_teal_700));
            } else {
                isVegTextView.setText("Vegetarian ? : NO");
                isVegTextView.setTextColor(Color.RED);
            }
            if (drinks.contains("1")) {
                drinksTextView.setText("Drinks : YES!");
                drinksTextView.setTextColor(getResources().getColor(R.color.md_teal_700));
            } else {
                drinksTextView.setText("Drinks : NO");
                drinksTextView.setTextColor(Color.RED);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }catch(NullPointerException e){

        }
    }

    private void loadProfileImage() {

        try {
            imageLoader.get(
                    memberObject.get(parameters.IMAGE_URL)
                            .toString()
                    , new ImageLoader.ImageListener() {
                        @Override
                        public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {

                            profileImage.setImageBitmap(response.getBitmap());
                        }

                        @Override
                        public void onErrorResponse(VolleyError error) {

                            Log.e("ImageError", "" + error.getMessage());

                        }
                    });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detailed_data_actvity, menu);
        // Associate searchable configuration with the SearchView
//        SearchManager searchManager =
//                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
//        SearchView searchView =
//                (SearchView) menu.findItem(R.id.search).getActionView();
//        searchView.setSearchableInfo(
//                searchManager.getSearchableInfo(getComponentName()));
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

    public void getDataFromIntent() {
        Intent callingIntent = getIntent();
        String memberData = callingIntent.getStringExtra("memberDetails");
        try {
            memberObject = new JSONObject(memberData);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
