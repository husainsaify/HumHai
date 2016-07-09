package com.hackerkernel.android.humhai.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hackerkernel.android.humhai.R;
import com.hackerkernel.android.humhai.activity.RestaurantFoodListActivity;
import com.hackerkernel.android.humhai.constant.Constants;
import com.hackerkernel.android.humhai.constant.EndPoints;
import com.hackerkernel.android.humhai.pojo.RestaurantFoodCategoryListPojo;
import com.hackerkernel.android.humhai.pojo.RestaurantFoodListPojo;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by husain on 7/2/2016.
 */
public class RestaurantFoodListAdapter extends RecyclerView.Adapter<RestaurantFoodListAdapter.MyViewHolder> {
    private Context context;
    private List<RestaurantFoodListPojo> list;

    public RestaurantFoodListAdapter(Context context, List<RestaurantFoodListPojo> list){
        this.context = context;
        this.list = list;
    }

    @Override
    public RestaurantFoodListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.restaurant_food_list_row,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RestaurantFoodListAdapter.MyViewHolder holder, int position) {
        RestaurantFoodListPojo current = list.get(position);
        holder.name.setText(current.getName());
        holder.unit.setText(current.getUnit()+" Unit");
        holder.price.setText(context.getString(R.string.rupee_sign)+" "+current.getPrice());

        //set image
        if (!current.getImageUrl().isEmpty()){
            String url = current.getImageUrl();
            Glide.with(context)
                    .load(url)
                    .thumbnail(0.5f)
                    .into(holder.image);
        }else {
            holder.image.setImageResource(R.drawable.default_icon);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.foodImage) ImageView image;
        @BindView(R.id.foodName) TextView name;
        @BindView(R.id.foodUnit) TextView unit;
        @BindView(R.id.foodPrice) TextView price;
        @BindView(R.id.addButton) ImageButton addButton;
        @BindView(R.id.minusButton) ImageButton minusButton;
        @BindView(R.id.counter) TextView counter;

        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);

            addButton.setOnClickListener(this);
            minusButton.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.addButton:
                    addFoodToCart();
                    break;
                case R.id.minusButton:
                    minusFoodToCart();
                    break;
            }
        }

        private void minusFoodToCart() {
            int pos = getAdapterPosition();
            String foodId = list.get(pos).getId();
            String counterValue = counter.getText().toString();
            int counterValueInt = Integer.parseInt(counterValue);
            if (counterValueInt > 0){
                int newCounterValue = counterValueInt - 1;
                counter.setText(newCounterValue+"");
            }
        }

        private void addFoodToCart() {
            int pos = getAdapterPosition();
            String foodId = list.get(pos).getId();
            String counterValue = counter.getText().toString();
            int counterValueInt = Integer.parseInt(counterValue);
            int newCounterValue = counterValueInt + 1;
            counter.setText(newCounterValue+"");
        }
    }
}
