package com.hackerkernel.android.humhai.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.bumptech.glide.Glide;
import com.hackerkernel.android.humhai.R;
import com.hackerkernel.android.humhai.constant.EndPoints;
import com.hackerkernel.android.humhai.network.MyVolley;
import com.hackerkernel.android.humhai.pojo.CartItemListPojo;
import com.hackerkernel.android.humhai.storage.MySharedPreferences;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by husain on 7/2/2016.
 */
public class CartItemListAdapter extends RecyclerView.Adapter<CartItemListAdapter.MyViewHolder> {
    private static final String TAG = CartItemListAdapter.class.getSimpleName();
    private Context context;
    private List<CartItemListPojo> list;
    private RequestQueue mRequestQueue;
    private MySharedPreferences sp;
    private ProgressDialog pd;

    public CartItemListAdapter(Context context, List<CartItemListPojo> list){
        this.context = context;
        this.list = list;
        mRequestQueue = MyVolley.getInstance().getRequestQueue();
        sp = MySharedPreferences.getInstance(context);
        //init pd
        pd = new ProgressDialog(context);
        pd.setMessage(context.getString(R.string.processing));
        pd.setCancelable(true);
    }

    @Override
    public CartItemListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.restaurant_food_list_row,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CartItemListAdapter.MyViewHolder holder, int position) {
        CartItemListPojo current = list.get(position);
        holder.name.setText(current.getName());
        holder.unit.setText(current.getUnit()+" Unit");
        holder.price.setText(context.getString(R.string.rupee_sign)+" "+current.getPrice());

        //set image
        if (!current.getImage().isEmpty()){
            String url = EndPoints.IMAGE_BASE_URL + current.getImage();
            Glide.with(context)
                    .load(url)
                    .thumbnail(0.5f)
                    .placeholder(R.drawable.default_icon)
                    .into(holder.image);
        }else {
            holder.image.setImageResource(R.drawable.default_icon);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.foodImage) ImageView image;
        @BindView(R.id.foodName) TextView name;
        @BindView(R.id.foodUnit) TextView unit;
        @BindView(R.id.foodPrice) TextView price;
        @BindView(R.id.tablelayout) TableLayout mTableLayout;
        @BindView(R.id.cart_delete_button) ImageButton mDeleteBtn;

        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);

            mTableLayout.setVisibility(View.GONE);
            mDeleteBtn.setVisibility(View.VISIBLE);
        }
    }




}
