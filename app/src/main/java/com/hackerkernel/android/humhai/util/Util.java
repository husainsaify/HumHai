package com.hackerkernel.android.humhai.util;


import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;


import com.hackerkernel.android.humhai.R;
import com.hackerkernel.android.humhai.activity.CartActivity;
import com.hackerkernel.android.humhai.activity.HomeActivity;
import com.hackerkernel.android.humhai.activity.OtpVerificationActivity;
import com.hackerkernel.android.humhai.constant.Constants;
import com.hackerkernel.android.humhai.infrastructure.MyApplication;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility methods
 */
public class Util {
    private static final String TAG = Util.class.getSimpleName();
    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;

    public static boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) MyApplication.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isAvailable() &&
                networkInfo.isConnected();
    }

    public static void showRedSnackbar(View layoutForSnacbar, String message) {
        Snackbar snack = Snackbar.make(layoutForSnacbar, message, Snackbar.LENGTH_LONG);
        ViewGroup group = (ViewGroup) snack.getView();
        group.setBackgroundColor(ContextCompat.getColor(MyApplication.getAppContext(), R.color.danger_color));
        snack.show();
    }

    public static void showParsingErrorAlert(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(context.getString(R.string.oops))
                .setMessage(context.getString(R.string.dont_worry_engineers_r_working))
                .setNegativeButton(context.getString(R.string.report_issue), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //TODO:: take user to report issue area
                    }
                })
                .setPositiveButton(context.getString(R.string.try_again), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /*
    * Method to send user to OTP verification activity
    * */
    public static void goToOtpVerificationActivity(Activity activity,String mobileNumber){
        Intent intent = new Intent(activity, OtpVerificationActivity.class);
        intent.putExtra(Constants.COM_MOBILE,mobileNumber);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
        activity.finish();
    }


    /*
    * Method to generate api key
    * */
    public static String generateApiKey(String text) {
        //generate Key
        ApiEncrypter encrypter = new ApiEncrypter();
        String key = "";
        try {
            key = ApiEncrypter.bytesToHex(encrypter.encrypt(text));
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "HUS: generateApiKey: " + e.getMessage());
        }
        return key;
    }

    public static void noInternetSnackBar(final Activity activity, View snackBarLayout) {
        final Snackbar snackbar = Snackbar.make(snackBarLayout, R.string.no_internet_connection, Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction(R.string.retry, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // activity.startActivity(new Intent(activity, NoInternetActivity.class));
            }
        });
        snackbar.setActionTextColor(ContextCompat.getColor(activity, R.color.primary));
        snackbar.show();
    }

    public static void showSimpleDialog(Context context, String title, String message) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton(context.getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /*
    * Method to get post data Url
    * from
    * HashMap
    * */
    public static String getPostDataFromHashmap(HashMap<String, String> param) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, String> entry : param.entrySet()) {
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }
        return result.toString();
    }


    public static String getTimeAgo(long time) {
        if (time < 1000000000000L) {
            // if timestamp given in seconds, convert to millis
            time *= 1000;
        }

        long now = System.currentTimeMillis();
        if (time > now || time <= 0) {
            return null;
        }

        final long diff = now - time;
        if (diff < MINUTE_MILLIS) {
            return "just now";
        } else if (diff < 2 * MINUTE_MILLIS) {
            return "1 minute ago";
        } else if (diff < 50 * MINUTE_MILLIS) {
            return diff / MINUTE_MILLIS + " minutes ago";
        } else if (diff < 90 * MINUTE_MILLIS) {
            return "an hour ago";
        } else if (diff < 24 * HOUR_MILLIS) {
            return diff / HOUR_MILLIS + " hours ago";
        } else if (diff < 48 * HOUR_MILLIS) {
            return "yesterday";
        } else {
            return diff / DAY_MILLIS + " days ago";
        }
    }

    public static String makeFirstLetterUpercase(String myString) {
        return myString.substring(0, 1).toUpperCase() + myString.substring(1);
    }

    public static void goToHomeActivity(Activity activity) {
        Intent intent = new Intent(activity, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
        activity.finish();
    }

    public static void goToCartActivity(Activity activity) {
        Intent intent = new Intent(activity, CartActivity.class);
        activity.startActivity(intent);
    }

    /*
    * Method to show progressbar and hide view
    * */
    public static void showProgressbarAndHideView(ProgressBar pb,View view){
            //show pb hide rc
            pb.setVisibility(View.VISIBLE);
            view.setVisibility(View.GONE);
    }

    /*
    * Method to hide progressbar and show view
    * */
    public static void hideProgressbarAndShowView(ProgressBar pb,View view){
        //hide pb show rc
        view.setVisibility(View.VISIBLE);
        pb.setVisibility(View.GONE);
    }
}
