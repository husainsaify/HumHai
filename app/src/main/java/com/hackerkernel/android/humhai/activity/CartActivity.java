package com.hackerkernel.android.humhai.activity;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.hackerkernel.android.humhai.R;
import com.hackerkernel.android.humhai.adapter.CartItemListAdapter;
import com.hackerkernel.android.humhai.adapter.DividerItemDecoration;
import com.hackerkernel.android.humhai.constant.Constants;
import com.hackerkernel.android.humhai.constant.EndPoints;
import com.hackerkernel.android.humhai.network.MyVolley;
import com.hackerkernel.android.humhai.parser.JsonParser;
import com.hackerkernel.android.humhai.pojo.CartItemListPojo;
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

public class CartActivity extends AppCompatActivity {
    private static final String TAG = CartActivity.class.getSimpleName();
    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.recyclerview) RecyclerView mRecyclerView;
    @BindView(R.id.placeholder) TextView mPlaceholder;
    @BindView(R.id.proceed_to_checkout) Button mProceedToCheckout;
    @BindView(R.id.total_cost) TextView mTotalCost;
    @BindView(R.id.total_item) TextView mTotalItem;

    private RequestQueue mRequestQueue;
    private MySharedPreferences sp;
    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setTitle("My Cart");
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //init volley and sp
        mRequestQueue = MyVolley.getInstance().getRequestQueue();
        sp = MySharedPreferences.getInstance(this);

        //init pd
        pd = new ProgressDialog(this);
        pd.setMessage(getString(R.string.processing));
        pd.setCancelable(false);

        //init rc
        LinearLayoutManager manager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(manager);

        checkInternetAndFetchCartItem();
    }

    /*
    * Method to check internet and fetch cart item
    * */
    private void checkInternetAndFetchCartItem() {
        if (Util.isNetworkAvailable()){
            fetchCartItemInBackground();
        }else {
            mRecyclerView.setVisibility(View.GONE);
            mPlaceholder.setVisibility(View.VISIBLE);
            mPlaceholder.setText(getString(R.string.no_internet_connection));
        }
    }

    /*
    * Method to fetch cart item in background
    * */
    private void fetchCartItemInBackground() {
        pd.show();
        StringRequest request = new StringRequest(Request.Method.POST, EndPoints.GET_CART_LIST, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                pd.dismiss();
                parseCartItemResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pd.dismiss();
                error.printStackTrace();
                Log.e(TAG,"HUS: fetchCartItemInBackground "+error.getMessage());
                String errorString = MyVolley.handleVolleyError(error);
                if (errorString != null){
                    Toast.makeText(getApplicationContext(),errorString,Toast.LENGTH_LONG).show();
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
    * Method to parse cart item response
    * */
    private void parseCartItemResponse(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            boolean returned = jsonObject.getBoolean(Constants.COM_RETURN);
            String message = jsonObject.getString(Constants.COM_MESSAGE);

            /*
            * If returned is true parse cart item list
            * */

            if (returned){
                //save cart item count to member variable
                int totalItemCount = jsonObject.getInt(Constants.COM_COUNT);
                int totalItemCost = jsonObject.getInt(Constants.COM_PRICE);

                //set total item cost & price
                mTotalCost.setText(getString(R.string.rupee_sign)+" "+totalItemCost);
                mTotalItem.setText("Total: "+totalItemCount+" Items");
                //parse CartItem list
                JSONArray jsonArray = jsonObject.getJSONArray(Constants.COM_DATA);
                List<CartItemListPojo> list = JsonParser.CartItemListParser(jsonArray);
                setupCartItemRecyclerView(list);
            }else {
                //hide rc & show placeholder
                mRecyclerView.setVisibility(View.GONE);
                mPlaceholder.setVisibility(View.VISIBLE);
                mPlaceholder.setText(message);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Util.showParsingErrorAlert(this);
        }
    }

    /*
    * setup cart item recycler view
    * */
    private void setupCartItemRecyclerView(List<CartItemListPojo> list) {
        CartItemListAdapter adapter = new CartItemListAdapter(this,list);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        mRecyclerView.setAdapter(adapter);
    }
}
