package com.hackerkernel.android.humhai.infrastructure;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.hackerkernel.android.humhai.R;
import com.hackerkernel.android.humhai.activity.CartActivity;
import com.hackerkernel.android.humhai.activity.MainActivity;
import com.hackerkernel.android.humhai.storage.MySharedPreferences;

/**
 * Class to check login status
 * if user is not logged in send him to login activity
 */
public abstract class BaseAuthActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //if user not login send him to Main activity
        if (!MySharedPreferences.getInstance(this).getLoginStatus()){
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }
}
