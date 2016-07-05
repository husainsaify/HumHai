package com.hackerkernel.android.humhai.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.hackerkernel.android.humhai.R;
import com.hackerkernel.android.humhai.constant.EndPoints;
import com.hackerkernel.android.humhai.pojo.DiscountOffersListPojo;
import com.hackerkernel.android.humhai.pojo.RestaurantFoodCategoryListPojo;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by husain on 7/2/2016.
 */
public class DiscountOfferListAdapter extends RecyclerView.Adapter<DiscountOfferListAdapter.MyViewHolder> {
    private Context context;
    private List<DiscountOffersListPojo> list;

    public DiscountOfferListAdapter(Context context, List<DiscountOffersListPojo> list){
        this.context = context;
        this.list = list;
    }

    @Override
    public DiscountOfferListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.discount_offer_list_row,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DiscountOfferListAdapter.MyViewHolder holder, int position) {
        DiscountOffersListPojo current = list.get(position);
        holder.title.setText(current.getTitle());
        holder.description.setText(current.getDescription());

        //set image
        if (current.getImageUrl() != null){
            //String url = EndPoints.IMAGE_BASE_URL+ current.getImage();
            Glide.with(context)
                    .load(current.getImageUrl())
                    .thumbnail(0.5f)
                    .into(holder.image);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.discount_image) ImageView image;
        @BindView(R.id.discount_title) TextView title;
        @BindView(R.id.discount_description) TextView description;

        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
