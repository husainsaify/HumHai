package com.hackerkernel.android.humhai.activity;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.hackerkernel.android.humhai.R;
import com.hackerkernel.android.humhai.adapter.SelectServiceAdapter;
import com.hackerkernel.android.humhai.infrastructure.BaseAuthActivity;
import com.hackerkernel.android.humhai.pojo.SelectServicePojo;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SelectServicesActivity extends BaseAuthActivity {
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.service_recyclerview)
    RecyclerView mServiceRecyclerView;

    private List<SelectServicePojo> list;
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

        //init recycler view with grid
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        mServiceRecyclerView.setHasFixedSize(true);
        mServiceRecyclerView.setLayoutManager(gridLayoutManager);

        SelectServiceAdapter adapter = new SelectServiceAdapter(this,list);
        mServiceRecyclerView.setAdapter(adapter);

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

}
