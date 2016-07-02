package com.hackerkernel.android.humhai.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hackerkernel.android.humhai.R;
import com.hackerkernel.android.humhai.constant.EndPoints;
import com.hackerkernel.android.humhai.pojo.RestaurantListPojo;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by husain on 7/2/2016.
 */
public class RestaurantListAdapter extends RecyclerView.Adapter<RestaurantListAdapter.MyViewHolder> {
    private Context context;
    private List<RestaurantListPojo> list;

    public RestaurantListAdapter(Context context, List<RestaurantListPojo> list){
        this.context = context;
        this.list = list;
    }

    @Override
    public RestaurantListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.restaurant_list_row,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RestaurantListAdapter.MyViewHolder holder, int position) {
        RestaurantListPojo current = list.get(position);
        holder.name.setText(current.getName());
        holder.deliveryTime.setText(current.getDeliveryTime());

        //set image
        if (current.getImage() != null){
            String url = EndPoints.IMAGE_BASE_URL+ current.getImage();
            Glide.with(context)
                    .load(url)
                    .thumbnail(0.5f)
                    .into(holder.image);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.restaurant_image) ImageView image;
        @BindView(R.id.restaurant_name) TextView name;
        @BindView(R.id.restaurant_delivery_time) TextView deliveryTime;
        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
