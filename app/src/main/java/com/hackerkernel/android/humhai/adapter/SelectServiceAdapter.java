package com.hackerkernel.android.humhai.adapter;


import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hackerkernel.android.humhai.R;
import com.hackerkernel.android.humhai.activity.RestaurantListActivity;
import com.hackerkernel.android.humhai.pojo.SelectServicePojo;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class SelectServiceAdapter extends RecyclerView.Adapter<SelectServiceAdapter.MyViewHolder> {

    private Context context;
    private List<SelectServicePojo> list;


    public SelectServiceAdapter(Context context, List<SelectServicePojo> list) {
        this.context = context;
        this.list = list;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.service_image) ImageView mImage;
        @BindView(R.id.service_name) TextView mName;

        public MyViewHolder(View view) {
            super(view);
            ButterKnife.bind(this,view);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int pos = getAdapterPosition();
            if (pos == 0){
                context.startActivity(new Intent(context, RestaurantListActivity.class));
            }
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.select_service_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        SelectServicePojo pojo = list.get(position);
        holder.mImage.setImageResource(pojo.getIcon());
        holder.mName.setText(pojo.getName());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


}
