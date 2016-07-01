package com.hackerkernel.android.humhai.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.hackerkernel.android.humhai.R;
import com.hackerkernel.android.humhai.constant.Constants;
import com.hackerkernel.android.humhai.constant.EndPoints;
import com.hackerkernel.android.humhai.infrastructure.BaseActivity;
import com.hackerkernel.android.humhai.network.MyVolley;
import com.hackerkernel.android.humhai.parser.JsonParser;
import com.hackerkernel.android.humhai.pojo.SimplePojo;
import com.hackerkernel.android.humhai.util.Util;


import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RegisterActivity extends BaseActivity {
    private static final String TAG = RegisterActivity.class.getSimpleName();
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.layout_for_snackbar) View mLayoutForSnackbar;
    @BindView(R.id.register_btn) Button mRegisterBtn;
    @BindView(R.id.register_fullname) EditText mFullnameView;
    @BindView(R.id.register_email) EditText mEmailView;
    @BindView(R.id.register_mobile) EditText mMobileView;
    @BindView(R.id.register_password) EditText mPasswordView;

    private RequestQueue mRequestQueue;
    private ProgressDialog pd;
    private String mMobile;
    private String mPassword;
    private String mEmail;
    private String mFullname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setTitle(R.string.register);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //init volley & pd
        mRequestQueue = MyVolley.getInstance().getRequestQueue();

        pd = new ProgressDialog(this);
        pd.setMessage(getString(R.string.processing));
        pd.setCancelable(false);

        mRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkNetworkNdFetchData();
            }
        });
    }

    private void checkNetworkNdFetchData() {
        if (Util.isNetworkAvailable()){

            //set view
            mMobile = mMobileView.getText().toString().trim();
            mPassword = mPasswordView.getText().toString().trim();
            mFullname = mFullnameView.getText().toString().trim();
            mEmail = mEmailView.getText().toString().trim();

            //check mobile or password is not empty
            if (mMobile.isEmpty() || mPassword.isEmpty() || mFullname.isEmpty() || mEmail.isEmpty()){
                Util.showRedSnackbar(mLayoutForSnackbar,"Fill in all the fields");
                return;
            }

            fetchDataInBackground();
        }else {
            Util.noInternetSnackBar(this,mLayoutForSnackbar);
        }
    }

    private void fetchDataInBackground() {
        pd.show();
        StringRequest request = new StringRequest(Request.Method.POST, EndPoints.REGISTER, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                pd.dismiss();
                parseLoginResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pd.dismiss();
                Log.e(TAG,"HUS: fetchDataInBackground "+error.getMessage());
                String errorString = MyVolley.handleVolleyError(error);
                if (errorString != null){
                    Util.showRedSnackbar(mLayoutForSnackbar,errorString);
                }
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put(Constants.COM_APIKEY,Util.generateApiKey(mMobile));
                params.put(Constants.COM_MOBILE,mMobile);
                params.put(Constants.COM_PASSWORD,mPassword);
                params.put(Constants.COM_NAME,mFullname);
                params.put(Constants.COM_EMAIL,mEmail);
                return params;
            }
        };

        mRequestQueue.add(request);
    }

    /*
    * Method parse login response
    * */
    private void parseLoginResponse(String response) {
        try {
            SimplePojo pojo = JsonParser.SimpleParser(response);
            if (pojo.isReturned()){
                Util.goToOtpVerificationActivity(this,mMobile);
            }else {
                Util.showRedSnackbar(mLayoutForSnackbar,pojo.getMessage());
                Toast.makeText(getApplicationContext(),pojo.getMessage(),Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG,"HUS: parseLoginResponse: "+e.getMessage());
            Util.showParsingErrorAlert(this);
        }
    }
}
