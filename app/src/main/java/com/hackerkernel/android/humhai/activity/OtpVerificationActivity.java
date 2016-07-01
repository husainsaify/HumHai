package com.hackerkernel.android.humhai.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
import com.hackerkernel.android.humhai.storage.MySharedPreferences;
import com.hackerkernel.android.humhai.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class OtpVerificationActivity extends BaseActivity {
    private static final String TAG = OtpVerificationActivity.class.getSimpleName();
    private String mobile;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.button_sendOTP) Button checkButton;
    @BindView(R.id.editText_OTP) EditText editTextOTP;
    @BindView(R.id.layout_for_snackbar) View mLayot;
    @BindView(R.id.otp_textview) TextView mOtpTextView;

    private RequestQueue mRequestQueue;
    private MySharedPreferences sp;
    private ProgressDialog pd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_verification);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setTitle("Verify OTP");

        if (getIntent().hasExtra(Constants.COM_MOBILE)){
            mobile = getIntent().getExtras().getString(Constants.COM_MOBILE);
            mOtpTextView.append(" "+mobile);
        }else {
            Toast.makeText(getApplicationContext(),"OOPS!! unable to start activity",Toast.LENGTH_LONG).show();
            finish();
        }

        //init volley & pd
        pd = new ProgressDialog(this);
        pd.setMessage(getString(R.string.processing));
        pd.setCancelable(false);
        mRequestQueue = MyVolley.getInstance().getRequestQueue();
        sp = MySharedPreferences.getInstance(this);

        checkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkVerification();
            }
        });




    }

    public void  checkVerification(){
        if(Util.isNetworkAvailable()){
            proceed();
        }else{
            Util.noInternetSnackBar(OtpVerificationActivity.this,mLayot);
        }
    }


    void proceed(){
        if(editTextOTP.getText().toString().isEmpty()){
            Util.showRedSnackbar(mLayot,"Fill in all the fields");
        }else{
            final String otp_code = editTextOTP.getText().toString();
            pd.show();
            StringRequest checkOTPRequest=new StringRequest(Request.Method.POST, EndPoints.VERIFY_OTP, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    pd.dismiss();
                    Log.d("checkOTPRequest","Response : "+response);
                    parseResponse(response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    pd.dismiss();
                    error.printStackTrace();
                    Log.e(TAG,"HUS: proceed: "+error.getMessage());
                    String errorString = MyVolley.handleVolleyError(error);
                    if (errorString != null){
                        Util.showRedSnackbar(mLayot,errorString);
                    }
                }
            }){
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> map = new HashMap<>();
                    map.put(Constants.COM_APIKEY,Util.generateApiKey(mobile));
                    map.put(Constants.COM_OTP,otp_code);
                    map.put(Constants.COM_MOBILE,mobile);
                    return map;
                }
            };

            mRequestQueue.add(checkOTPRequest);
        }
    }

    /*
    * Method to parse otp response
    * */
    private void parseResponse(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            boolean returned = jsonObject.getBoolean(Constants.COM_RETURN);
            String message = jsonObject.getString(Constants.COM_MESSAGE);
            if (returned){
                //parse user data and store it
                JSONArray ja = jsonObject.getJSONArray(Constants.COM_DATA);
                for (int i = 0; i < ja.length(); i++) {
                    JSONObject jo = ja.getJSONObject(i);
                    sp.setUserMobile(jo.getString(Constants.COM_MOBILE));
                    sp.setUserEmail(jo.getString(Constants.COM_EMAIL));
                    sp.setUserFullname(jo.getString(Constants.COM_NAME));

                    //ssend user to home activ ity
                    Util.goToHomeActivity(this);
                }
            }else {
                Util.showRedSnackbar(mLayot,message);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Util.showParsingErrorAlert(this);
        }
    }

}
