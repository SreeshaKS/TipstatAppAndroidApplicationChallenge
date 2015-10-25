package com.hiringchallenge.hackerearth.tipstatapp.APIData.DetailedData;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.hiringchallenge.hackerearth.tipstatapp.Network.VolleySingleton;
import com.hiringchallenge.hackerearth.tipstatapp.R;

import org.json.JSONException;

import java.util.ArrayList;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {
    private static final String TAG = "CustomAdapter";
    private String[] mDataSet;

    private ImageLoader imageLoader;
    private VolleySingleton volleySingleton;

    static Context context;
    public ArrayList<member> memberList;

    public ArrayList<member> getMemberList() {
        return memberList;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView textView;
        private ImageView profilePicture;

        public ViewHolder(View v) {
            super(v);
            v.setOnClickListener(this);
            profilePicture = (ImageView) v.findViewById(R.id.profile_image);
            textView = (TextView) v.findViewById(R.id.memberIdTextView);

        }

        public TextView getTextView() {
            return textView;
        }

        @Override
        public void onClick(View v) {
            Log.i("CustomAdapter", "ItemClicked : " + getAdapterPosition());
            Intent detailedDataActivityIntent = new Intent(context,
                    DetailedDataActivity.class);
            detailedDataActivityIntent.putExtra(
                    "memberDetails"
                    , memberList.get(getAdapterPosition()).getMemberDetails().toString()
            );

            context.startActivity(detailedDataActivityIntent);
        }
    }


    public CustomAdapter(
            ArrayList<member> memberList
            , Context context) {

        this.memberList = memberList;
        this.context = context;

        volleySingleton = new VolleySingleton(context);
        volleySingleton = VolleySingleton.getsInstance(context);

        imageLoader = volleySingleton.getImageLoader();
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.result_row_item, viewGroup, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
        Log.d(TAG, "Element " + position + " set.");
        APIParameters parameters = new APIParameters();

        try {
            viewHolder.getTextView().setText(
                    memberList.get(position).getMemberDetails()
                            .get(parameters.STATUS)
                            .toString()

            );
            imageLoader.get(
                    memberList.get(position).getMemberDetails()
                            .get(parameters.IMAGE_URL)
                            .toString()
                    , new ImageLoader.ImageListener() {
                        @Override
                        public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {

                            viewHolder.profilePicture.setImageBitmap(response.getBitmap());
                        }

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            viewHolder.profilePicture.setVisibility(View.GONE);
                            Log.e("ImageError", "" + error.getMessage());

                        }
                    });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return memberList.size();
    }
}


