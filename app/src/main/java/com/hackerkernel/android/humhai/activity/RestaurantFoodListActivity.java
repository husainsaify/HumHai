package com.hackerkernel.android.humhai.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.hackerkernel.android.humhai.R;
import com.hackerkernel.android.humhai.adapter.DividerItemDecoration;
import com.hackerkernel.android.humhai.adapter.RestaurantFoodCategoryListAdapter;
import com.hackerkernel.android.humhai.adapter.RestaurantFoodListAdapter;
import com.hackerkernel.android.humhai.constant.Constants;
import com.hackerkernel.android.humhai.constant.EndPoints;
import com.hackerkernel.android.humhai.network.MyVolley;
import com.hackerkernel.android.humhai.parser.JsonParser;
import com.hackerkernel.android.humhai.pojo.RestaurantFoodCategoryListPojo;
import com.hackerkernel.android.humhai.pojo.RestaurantFoodListPojo;
import com.hackerkernel.android.humhai.storage.MySharedPreferences;
import com.hackerkernel.android.humhai.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RestaurantFoodListActivity extends AppCompatActivity {
    private static final String TAG = RestaurantFoodListActivity.class.getSimpleName();
    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.toolbar_cart_button) Button mToolbarButton;
    @BindView(R.id.progressbar) ProgressBar mProgressbar;
    @BindView(R.id.recycleView) RecyclerView mRecyclerview;
    @BindView(R.id.layout_for_snackbar) View mLayoutForSnackbar;
    @BindView(R.id.placeholder) TextView mPlaceholder;

    private String mHotelId;
    private String mFoodCategoryId;
    private RequestQueue mRequestQueue;
    private MySharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_food_list);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setTitle("Add Food");

        //init volley & sp
        sp = MySharedPreferences.getInstance(this);
        mRequestQueue = MyVolley.getInstance().getRequestQueue();

        mRecyclerview.setLayoutManager(new LinearLayoutManager(this));

        //init toolbar cart button
        mToolbarButton.setVisibility(View.VISIBLE);
        mToolbarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.goToCartActivity(RestaurantFoodListActivity.this);
            }
        });

        //get hotel & food category id
        if (getIntent().hasExtra(Constants.COM_FOOD_CATEGORY_ID) &&
                getIntent().hasExtra(Constants.COM_HOTEL_ID)) {
            mFoodCategoryId = getIntent().getExtras().getString(Constants.COM_FOOD_CATEGORY_ID);
            mHotelId = getIntent().getExtras().getString(Constants.COM_HOTEL_ID);
        } else {
            Toast.makeText(getApplicationContext(), "Unable to open food List. Try again latter", Toast.LENGTH_LONG).show();
            finish();
        }

        checkInternetAndFetchFoodList();
    }


    private void checkInternetAndFetchFoodList() {
        if (Util.isNetworkAvailable()) {
            fetchFoodListInBackground();
        } else {
            Util.noInternetSnackBar(this, mLayoutForSnackbar);
        }
    }

    private void fetchFoodListInBackground() {
        //show pb hide rc
        Util.showProgressbarAndHideView(mProgressbar, mRecyclerview);
        StringRequest request = new StringRequest(Request.Method.POST, EndPoints.GET_FOOD_LIST, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //hide pb show rc
                Util.hideProgressbarAndShowView(mProgressbar, mRecyclerview);
                parseFoodListResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //hide pb show rc
                Util.hideProgressbarAndShowView(mProgressbar, mRecyclerview);
                error.printStackTrace();
                Log.e(TAG, "HUS: fetchFoodListInBackground: " + error.getMessage());
                String errorString = MyVolley.handleVolleyError(error);
                if (errorString != null) {
                    Util.showRedSnackbar(mLayoutForSnackbar, errorString);
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put(Constants.COM_APIKEY, Util.generateApiKey(sp.getUserMobile()));
                params.put(Constants.COM_MOBILE, sp.getUserMobile());
                params.put(Constants.COM_HOTEL_ID, mHotelId);
                params.put(Constants.COM_FOOD_CATEGORY_ID, mFoodCategoryId);
                return params;
            }
        };

        mRequestQueue.add(request);
    }

    private void parseFoodListResponse(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            boolean returned = jsonObject.getBoolean(Constants.COM_RETURN);
            String message = jsonObject.getString(Constants.COM_MESSAGE);

            /*
            * If returned is true check count of the resturatn
            * */
            if (returned){
                //true
                int count = jsonObject.getInt(Constants.COM_COUNT);
                /*
                * Check restaurant count
                * if count is more then 0 we have hotel
                * */
                if (count <= 0){
                    //hide progressbar & recyclerview
                    mProgressbar.setVisibility(View.GONE);
                    mRecyclerview.setVisibility(View.GONE);
                    mPlaceholder.setVisibility(View.VISIBLE);
                    mPlaceholder.setText(message);
                }else {
                    JSONArray dataArray = jsonObject.getJSONArray(Constants.COM_DATA);
                    // parse hotel data
                    List<RestaurantFoodListPojo> list = JsonParser.RestaurantFoodListParser(dataArray);
                    setupAdapter(list);
                }
            }else {
                Util.showRedSnackbar(mLayoutForSnackbar,message);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG,"HUS: parseFoodListResponse: "+e.getMessage());
            Util.showParsingErrorAlert(this);
        }
    }

    private void setupAdapter(List<RestaurantFoodListPojo> list) {
        RestaurantFoodListAdapter adapter = new RestaurantFoodListAdapter(this,list);
        Util.hideProgressbarAndShowView(mProgressbar,mRecyclerview);
        mRecyclerview.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        mRecyclerview.setAdapter(adapter);
    }
}
