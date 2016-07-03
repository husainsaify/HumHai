package com.hackerkernel.android.humhai.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.hackerkernel.android.humhai.R;
import com.hackerkernel.android.humhai.activity.RestaurantFoodCategoryListActivity;
import com.hackerkernel.android.humhai.constant.Constants;
import com.hackerkernel.android.humhai.constant.EndPoints;
import com.hackerkernel.android.humhai.pojo.RestaurantFoodCategoryListPojo;
import com.hackerkernel.android.humhai.pojo.RestaurantListPojo;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by husain on 7/2/2016.
 */
public class RestaurantFoodCategoryListAdapter extends RecyclerView.Adapter<RestaurantFoodCategoryListAdapter.MyViewHolder> {
    private Context context;
    private List<RestaurantFoodCategoryListPojo> list;

    public RestaurantFoodCategoryListAdapter(Context context, List<RestaurantFoodCategoryListPojo> list){
        this.context = context;
        this.list = list;
    }

    @Override
    public RestaurantFoodCategoryListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.restaurant_food_category_list_row,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RestaurantFoodCategoryListAdapter.MyViewHolder holder, int position) {
        RestaurantFoodCategoryListPojo current = list.get(position);
        holder.name.setText(current.getName());
        holder.count.setText(current.getCount());

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

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.image) ImageView image;
        @BindView(R.id.name) TextView name;
        @BindView(R.id.count) TextView count;

        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int pos = getAdapterPosition();
            Toast.makeText(context,pos+"",Toast.LENGTH_LONG).show();
        }
    }
}
