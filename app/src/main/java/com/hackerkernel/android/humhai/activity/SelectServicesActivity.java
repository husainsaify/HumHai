package com.hackerkernel.android.humhai.activity;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.hackerkernel.android.humhai.R;
import com.hackerkernel.android.humhai.adapter.DiscountOfferListAdapter;
import com.hackerkernel.android.humhai.adapter.RestaurantListAdapter;
import com.hackerkernel.android.humhai.adapter.SelectServiceAdapter;
import com.hackerkernel.android.humhai.constant.Constants;
import com.hackerkernel.android.humhai.constant.EndPoints;
import com.hackerkernel.android.humhai.infrastructure.BaseAuthActivity;
import com.hackerkernel.android.humhai.network.MyVolley;
import com.hackerkernel.android.humhai.parser.JsonParser;
import com.hackerkernel.android.humhai.pojo.DiscountOffersListPojo;
import com.hackerkernel.android.humhai.pojo.RestaurantListPojo;
import com.hackerkernel.android.humhai.pojo.SelectServicePojo;
import com.hackerkernel.android.humhai.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SelectServicesActivity extends BaseAuthActivity {
    private static final String TAG = SelectServicesActivity.class.getSimpleName();
    @BindView(R.id.layout_for_snackbar) View mLayoutForSnackbar;
    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.service_recyclerview) RecyclerView mServiceRecyclerView;
    //discount offers
    @BindView(R.id.discount_recyclerview) RecyclerView mDiscountRecyclerView;
    @BindView(R.id.progressbar) ProgressBar mProgressbar;
    @BindView(R.id.placeholder) TextView mPlaceholder;

    private List<SelectServicePojo> list;
    private RequestQueue mRequestQueue;
    private String[] mServiceName = {
            "Food",
            "Pick & Drop",
            "Flowers & Gift",
            "Cakes",
            "Medicines",
            "MoBaxi",
            "Roadside Assistance",
            "Emergency"
    };
    private int[] mServiceIcon = {
            R.drawable.groceries,
            R.drawable.pickup,
            R.drawable.flower,
            R.drawable.birthday_cake,
            R.drawable.pills,
            R.drawable.taxi_driver,
            R.drawable.telemarketer,
            R.drawable.siren,
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_services);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setTitle("Select Services");
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        list = new ArrayList<>();

        //setup select service list
        setUpSelectServiceList();

        //init volley and sp
        mRequestQueue = MyVolley.getInstance().getRequestQueue();

        //init recycler view with grid
        LinearLayoutManager layoutManager =
                new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        mServiceRecyclerView.setLayoutManager(layoutManager);
        SelectServiceAdapter adapter = new SelectServiceAdapter(this,list);
        mServiceRecyclerView.setAdapter(adapter);

        //init discount recyclerview
        mDiscountRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        checkInternetAndFetchDiscountOffer();
    }

    private void setUpSelectServiceList() {
        //set select service list
        for (int i = 0; i < mServiceName.length; i++) {
            SelectServicePojo current = new SelectServicePojo();
            current.setId(i);
            current.setIcon(mServiceIcon[i]);
            current.setName(mServiceName[i]);
            list.add(current);
        }
    }

    /*
    * Check internet and fetch discount offers list
    * */
    private void checkInternetAndFetchDiscountOffer() {
        if (Util.isNetworkAvailable()){
            fetchDiscountOfferInBackground();
        }else{
            Util.noInternetSnackBar(this,mLayoutForSnackbar);
        }
    }

    private void fetchDiscountOfferInBackground() {
        //show pb hide rc
        Util.showProgressbarAndHideView(mProgressbar,mDiscountRecyclerView);
        StringRequest request = new StringRequest(Request.Method.GET, EndPoints.GET_DISCOUNT_OFFER_LIST, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //hide pb show rc
                Util.hideProgressbarAndShowView(mProgressbar,mDiscountRecyclerView);
                parseDiscountOfferResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //hide pb show rc
                Util.hideProgressbarAndShowView(mProgressbar,mDiscountRecyclerView);
                error.printStackTrace();
                Log.e(TAG,"HUS: fetchDiscountOfferInBackground "+error.getMessage());
                String errorString = MyVolley.handleVolleyError(error);
                if (errorString != null){
                    Util.showRedSnackbar(mLayoutForSnackbar,errorString);
                }
            }
        });

        mRequestQueue.add(request);
    }

    /*
    * Method to parse discount offer response response
    * */
    private void parseDiscountOfferResponse(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            boolean returned = jsonObject.getBoolean(Constants.COM_RETURN);
            String message = jsonObject.getString(Constants.COM_MESSAGE);

            /*
            * If returned is true check count of the discount offer
            * */
            if (returned){
                //true
                int count = jsonObject.getInt(Constants.COM_COUNT);
                /*
                * Check count
                * if count is more then 0 we have discount offers
                * */
                if (count > 0){
                    JSONArray dataArray = jsonObject.getJSONArray(Constants.COM_DATA);
                    // parse hotel data
                    List<DiscountOffersListPojo> list = JsonParser.DiscountOfferListParser(dataArray);
                    setupAdapter(list);
                }else {
                    //no discount offer present
                    mDiscountRecyclerView.setVisibility(View.GONE);
                    mProgressbar.setVisibility(View.GONE);
                    mPlaceholder.setVisibility(View.VISIBLE);
                    mPlaceholder.setText(message);
                }
            }else {
                Util.showRedSnackbar(mLayoutForSnackbar,message);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG,"HUS: parseDiscountOfferResponse: "+e.getMessage());
            Util.showParsingErrorAlert(this);
        }
    }

    private void setupAdapter(List<DiscountOffersListPojo> list) {
        DiscountOfferListAdapter adapter = new DiscountOfferListAdapter(this,list);
        Util.hideProgressbarAndShowView(mProgressbar,mDiscountRecyclerView);
        //mHotelRecyclerview.addItemDecoration(new DividerItemDecoration(this,LinearLayoutManager.VERTICAL));
        mDiscountRecyclerView.setAdapter(adapter);
    }
}
