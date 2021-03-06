package com.hackerkernel.android.humhai.storage;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Singleton class for sharedPreferences
 */
public class MySharedPreferences {
    //instance field
    private static SharedPreferences mSharedPreference;
    private static MySharedPreferences mInstance = null;
    private static Context mContext;


    //Shared Preference key
    private String KEY_PREFERENCE_NAME = "HumHai";

    //private keyS
    private String KEY_DEFAULT = null;

    //user details keys
    private String KEY_USER_ID = "id",
            KEY_FULL_NAME = "name",
            KEY_USER_MOBILE = "mobile",
            KEY_USER_EMAIL = "email",
            KEY_USER_LATITUDE = "latitude",
            KEY_USER_LONGITUDE ="longitude",
            KEY_USER_ADDRESS = "address";

    //boolean keys
    public static String BOL_KEY_APP_INTRO = "appintro";
    public static String KEY_SENT_GCM_TOKEN_TO_SERVER = "send_gcm_token";

    public MySharedPreferences() {
        mSharedPreference = mContext.getSharedPreferences(KEY_PREFERENCE_NAME, Context.MODE_PRIVATE);
    }

    public static MySharedPreferences getInstance(Context context) {
        mContext = context;
        if (mInstance == null) {
            mInstance = new MySharedPreferences();
        }
        return mInstance;
    }

    //Method to set boolean for (AppIntro)
    public void setBooleanKey(String keyname) {
        mSharedPreference.edit().putBoolean(keyname, true).apply();
    }

    public void setBooleanKey(String keyname,boolean state) {
        mSharedPreference.edit().putBoolean(keyname, state).apply();
    }

    /*
    * Method to get boolan key
    * true = means set
    * false = not set (show app intro)
    * */
    public boolean getBooleanKey(String keyname) {
        return mSharedPreference.getBoolean(keyname, false);
    }


    //Method to store user Mobile number
    public void setUserMobile(String mobile) {
        mSharedPreference.edit().putString(KEY_USER_MOBILE, mobile).apply();
    }

    //Method to get User mobile number
    public String getUserMobile() {
        return mSharedPreference.getString(KEY_USER_MOBILE, KEY_DEFAULT);
    }


    //USER FULLNAME
    public void setUserFullname(String name){
        mSharedPreference.edit().putString(KEY_FULL_NAME, name).apply();
    }

    public String getUserFullname(){
        return mSharedPreference.getString(KEY_FULL_NAME, KEY_DEFAULT);
    }

    //USER age
    public void setUserEmail(String email){
        mSharedPreference.edit().putString(KEY_USER_EMAIL, email).apply();
    }

    public String getUserEmail(){
        return mSharedPreference.getString(KEY_USER_EMAIL, KEY_DEFAULT);
    }

    //USER id
    public void setUserId(String id){
        mSharedPreference.edit().putString(KEY_USER_ID, id).apply();
    }

    public String getUserId(){
        return mSharedPreference.getString(KEY_USER_ID, KEY_DEFAULT);
    }


    //Method to check user is logged in or not
    public boolean getLoginStatus() {
        //logged in
        return mSharedPreference.getString(KEY_USER_MOBILE, KEY_DEFAULT) != null;
    }

    public void setUserLatitude(String latitude){
        mSharedPreference.edit().putString(KEY_USER_LATITUDE, latitude).apply();
    }

    public String getUserLatitude(){
        return mSharedPreference.getString(KEY_USER_LATITUDE, KEY_DEFAULT);
    }
    public void setUserLongitude(String longitude){
        mSharedPreference.edit().putString(KEY_USER_LONGITUDE,longitude).apply();
    }
    public String getUserLongitude(){
        return mSharedPreference.getString(KEY_USER_LONGITUDE,KEY_DEFAULT);
    }
    public void setUserAddress(String address){
        mSharedPreference.edit().putString(KEY_USER_ADDRESS,address).apply();
    }
    public String getUserAddress(){
        return mSharedPreference.getString(KEY_USER_ADDRESS,KEY_DEFAULT);
    }
}
