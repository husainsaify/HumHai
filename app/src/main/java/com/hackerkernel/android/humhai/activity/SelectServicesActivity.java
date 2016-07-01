package com.hackerkernel.android.humhai.activity;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.hackerkernel.android.humhai.R;
import com.hackerkernel.android.humhai.infrastructure.BaseAuthActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SelectServicesActivity extends BaseAuthActivity {
    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.service_recyclerview) RecyclerView mServiceRecyclerView;

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
    }
}
