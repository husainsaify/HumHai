package com.hackerkernel.android.humhai.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.hackerkernel.android.humhai.R;
import com.hackerkernel.android.humhai.adapter.DividerItemDecoration;
import com.hackerkernel.android.humhai.adapter.RestaurantListAdapter;
import com.hackerkernel.android.humhai.constant.Constants;
import com.hackerkernel.android.humhai.constant.EndPoints;
import com.hackerkernel.android.humhai.infrastructure.BaseAuthActivity;
import com.hackerkernel.android.humhai.network.MyVolley;
import com.hackerkernel.android.humhai.parser.JsonParser;
import com.hackerkernel.android.humhai.pojo.RestaurantListPojo;
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

public class RestaurantListActivity extends BaseAuthActivity{
    private static final String TAG = RestaurantListActivity.class.getSimpleName();
    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.hotel_progressbar) ProgressBar mHotelProgressbar;
    @BindView(R.id.hotel_recyclerview) RecyclerView mHotelRecyclerview;
    @BindView(R.id.layout_for_snackbar) View mLayoutForSnackbar;

    private RequestQueue mRequestQueue;
    private MySharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotel_list);

        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setTitle("Select Restaurant");
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //init volley and sp
        mRequestQueue = MyVolley.getInstance().getRequestQueue();
        sp = MySharedPreferences.getInstance(this);

        //init rc
        LinearLayoutManager manager = new LinearLayoutManager(this);
        mHotelRecyclerview.setLayoutManager(manager);

        checkInternetAndFetchRestaurant();
    }

    private void checkInternetAndFetchRestaurant() {
        if (Util.isNetworkAvailable()){
            fetchRestaurantInBackground();
        }else{
            Util.noInternetSnackBar(this,mLayoutForSnackbar);
        }
    }

    private void fetchRestaurantInBackground() {
        //show pb hide rc
        Util.showProgressbarAndHideView(mHotelProgressbar,mHotelRecyclerview);
        StringRequest request = new StringRequest(Request.Method.POST, EndPoints.GET_RESTAURANT_LIST, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //hide pb show rc
                Util.hideProgressbarAndShowView(mHotelProgressbar,mHotelRecyclerview);
                parseRestaurantResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //hide pb show rc
                Util.hideProgressbarAndShowView(mHotelProgressbar,mHotelRecyclerview);
                error.printStackTrace();
                Log.e(TAG,"HUS: fetchRestaurantInBackground "+error.getMessage());
                String errorString = MyVolley.handleVolleyError(error);
                if (errorString != null){
                    Util.showRedSnackbar(mLayoutForSnackbar,errorString);
                }
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put(Constants.COM_APIKEY,Util.generateApiKey(sp.getUserMobile()));
                params.put(Constants.COM_MOBILE,sp.getUserMobile());
                return params;
            }
        };

        mRequestQueue.add(request);
    }

    /*
    * Method to parse restaurant response
    * */
    private void parseRestaurantResponse(String response) {
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
                if (count > 0){
                    JSONArray dataArray = jsonObject.getJSONArray(Constants.COM_DATA);
                    // parse hotel data
                    List<RestaurantListPojo> list = JsonParser.RestaurantListParser(dataArray);
                    setupAdapter(list);
                }else {
                    //no hotel present currently
                    Util.showSimpleDialog(this,"OOPS!!",message);
                }
            }else {
                Util.showRedSnackbar(mLayoutForSnackbar,message);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG,"HUS: parseRestaurantResponse: "+e.getMessage());
            Util.showParsingErrorAlert(this);
        }
    }

    private void setupAdapter(List<RestaurantListPojo> list) {
        RestaurantListAdapter adapter = new RestaurantListAdapter(this,list);
        Util.hideProgressbarAndShowView(mHotelProgressbar,mHotelRecyclerview);
        mHotelRecyclerview.addItemDecoration(new DividerItemDecoration(this,LinearLayoutManager.VERTICAL));
        mHotelRecyclerview.setAdapter(adapter);
    }
}
