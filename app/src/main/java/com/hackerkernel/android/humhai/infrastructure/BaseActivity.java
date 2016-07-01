package com.hackerkernel.android.humhai.infrastructure;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.hackerkernel.android.humhai.storage.MySharedPreferences;
import com.hackerkernel.android.humhai.util.Util;


/**
 * Class to check if user logged in send him to home activity
 * no need to display login or MainActivity
 */
public abstract class BaseActivity extends AppCompatActivity {
    private MySharedPreferences sp;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //init sp
        sp = MySharedPreferences.getInstance(this);

        if (sp.getLoginStatus()){
            Util.goToHomeActivity(this);
        }
    }
}
