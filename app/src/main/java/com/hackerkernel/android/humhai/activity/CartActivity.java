package com.hackerkernel.android.humhai.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.hackerkernel.android.humhai.adapter.CartItemListAdapter;
import com.hackerkernel.android.humhai.adapter.DividerItemDecoration;
import com.hackerkernel.android.humhai.constant.Constants;
import com.hackerkernel.android.humhai.constant.EndPoints;
import com.hackerkernel.android.humhai.network.MyVolley;
import com.hackerkernel.android.humhai.parser.JsonParser;
import com.hackerkernel.android.humhai.pojo.CartItemListPojo;
import com.hackerkernel.android.humhai.pojo.SimplePojo;
import com.hackerkernel.android.humhai.storage.MySharedPreferences;
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

public class CartActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = CartActivity.class.getSimpleName();
    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.recyclerview) RecyclerView mRecyclerView;
    @BindView(R.id.placeholder) TextView mPlaceholder;
    @BindView(R.id.proceed_to_checkout) Button mProceedToCheckout;
    static TextView mTotalItemCost;
    static TextView mTotalItemCount;

    private RequestQueue mRequestQueue;
    private MySharedPreferences sp;
    private ProgressDialog pd;
    private List<CartItemListPojo> list;
    private LayoutInflater inflater;

    //checkout dialog view
    private EditText mobileView;
    private EditText nameView;
    private EditText addressView;
    private ProgressBar progressView;
    private TextView alertMessageView;
    private AlertDialog checkoutDialog;

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

        list = new ArrayList<>();

        //init volley and sp
        mRequestQueue = MyVolley.getInstance().getRequestQueue();
        sp = MySharedPreferences.getInstance(this);

        //init inflater
        inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);

        //init pd
        pd = new ProgressDialog(this);
        pd.setMessage(getString(R.string.processing));
        pd.setCancelable(false);

        //init rc
        LinearLayoutManager manager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(manager);

        //total cost & count
        mTotalItemCost = (TextView) findViewById(R.id.total_item_cost);
        mTotalItemCount = (TextView) findViewById(R.id.total_item_count);

        //when proceed to checkout btn is pressed
        mProceedToCheckout.setOnClickListener(this);

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
                mTotalItemCost.setText(getString(R.string.rupee_sign)+" "+totalItemCost);
                mTotalItemCount.setText("Total: "+totalItemCount+" Items");

                //parse CartItem list
                JSONArray jsonArray = jsonObject.getJSONArray(Constants.COM_DATA);
                list = JsonParser.CartItemListParser(jsonArray);
                setupCartItemRecyclerView();
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
    private void setupCartItemRecyclerView() {
        CartItemListAdapter adapter = new CartItemListAdapter(this,list);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        mRecyclerView.setAdapter(adapter);
    }

    /*
    * Method to update total item count
    *
    * This method is used by `CartItemListAdapter.java` to update item count
    * */
    public static void updateTotalItemCount(String string){
        mTotalItemCount.setText(string);
    }

    /*
    * Method to update total item cost
    *
    * This method is used by `CartItemListAdapter.java` to update item cost
    * */
    public static void updateTotalItemCost(String string){
        mTotalItemCost.setText(string);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.proceed_to_checkout:
                //this method is called when someone click proceed to checkout btn
                checkAndShowCheckoutDialog();
                break;
        }
    }

    /*
    * Method to validation and then launch checkout dialog
    * */
    private void checkAndShowCheckoutDialog() {
        //check internet
        if (Util.isNetworkAvailable()){
            //check cart item is greater then 0
            if(list.size() > 0){
                createCheckoutDialog();
            }else {
                //empty cart
                Util.showSimpleDialog(this,"Empty cart!","Please select some food before preceding to checkout");
            }
        }else {
            Toast.makeText(getApplicationContext(),R.string.no_internet_connection,Toast.LENGTH_SHORT).show();
        }
    }

    /*
    * Method to create checkout dialog
    * */
    private void createCheckoutDialog() {
        //inflate alertdialog layout
        View view = inflater.inflate(R.layout.checkout_layout,null);
        //get reference of name,mobile & address
        mobileView = (EditText) view.findViewById(R.id.checkout_mobile);
        nameView = (EditText) view.findViewById(R.id.checkout_name);
        addressView = (EditText) view.findViewById(R.id.checkout_address);
        progressView = (ProgressBar) view.findViewById(R.id.checkout_progressbar);
        Button checkoutBtn = (Button) view.findViewById(R.id.checkout_confirm_btn);
        alertMessageView = (TextView) view.findViewById(R.id.checkout_alert_message);

        //set user stored mobile number to edittext
        mobileView.setText(sp.getUserMobile());

        //set user address
        addressView.setText(sp.getUserAddress());

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);

        checkoutDialog = builder.create();
        checkoutDialog.show();

        //when Confirm checkout btn is clicked
        checkoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAndConfirmOrder();
            }
        });
    }

    /*
    * Method to check internet & validate input from user
    * & book order
    * */
    private void checkAndConfirmOrder() {
        //check internet connection
        if (!Util.isNetworkAvailable()){
            alertMessageView.setText(R.string.no_internet_connection);
            alertMessageView.setVisibility(View.VISIBLE);
            alertMessageView.setTextColor(ContextCompat.getColor(this,R.color.danger_color));
            return;
        }

        //validation
        final String name = nameView.getText().toString().trim();
        final String mobile = mobileView.getText().toString().trim();
        final String address = addressView.getText().toString().trim();

        //fields are not empty
        if (name.isEmpty() || mobile.isEmpty() || address.isEmpty()){
            alertMessageView.setText("Fill in all the fields");
            alertMessageView.setVisibility(View.VISIBLE);
            alertMessageView.setTextColor(ContextCompat.getColor(this,R.color.danger_color));
            return;
        }

        //check mobile number
        if (mobile.length() != 10){
            alertMessageView.setText("Invalid mobile number");
            alertMessageView.setVisibility(View.VISIBLE);
            alertMessageView.setTextColor(ContextCompat.getColor(this,R.color.danger_color));
            return;
        }

        //hide alertMessage and show progressbar
        alertMessageView.setVisibility(View.GONE);
        progressView.setVisibility(View.VISIBLE);
        StringRequest request = new StringRequest(Request.Method.POST, EndPoints.CHECKOUT, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //hide progressbar
                progressView.setVisibility(View.GONE);
                parseCheckoutResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //show alertMessage and hide progressbar
                alertMessageView.setVisibility(View.VISIBLE);
                alertMessageView.setTextColor(ContextCompat.getColor(getApplication(),R.color.danger_color));
                progressView.setVisibility(View.GONE);
                error.printStackTrace();
                Log.e(TAG,"HUS: fetchRestaurantInBackground "+error.getMessage());
                String errorString = MyVolley.handleVolleyError(error);
                if (errorString != null){
                    alertMessageView.setText(errorString);
                }
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put(Constants.COM_APIKEY,Util.generateApiKey(sp.getUserMobile()));
                params.put(Constants.COM_MOBILE,sp.getUserMobile());
                params.put(Constants.COM_NAME,name);
                params.put(Constants.COM_ADDRESS,address);
                params.put(Constants.COM_GIVEN_MOBILE,mobile);
                return params;
            }
        };
        mRequestQueue.add(request);
    }

    /*
    * Method to parse checkout response
    * */
    private void parseCheckoutResponse(String response) {
        try {
            SimplePojo pojo = JsonParser.SimpleParser(response);
            if (pojo.isReturned()){
                //close dialog
                checkoutDialog.dismiss();

                //clear list
                list.clear();

                //refresh cart item list
                checkInternetAndFetchCartItem();

                //show success dialog
                Util.showSimpleDialog(this,"Order Confirmed!!",pojo.getMessage());

                //clear count & cost
                mTotalItemCost.setText(" ");
                mTotalItemCount.setText(" ");
            }else {
                alertMessageView.setVisibility(View.VISIBLE);
                alertMessageView.setTextColor(ContextCompat.getColor(getApplication(),R.color.danger_color));
                alertMessageView.setText(pojo.getMessage());
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Util.showParsingErrorAlert(this);
        }
    }
}
