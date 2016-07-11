package com.hackerkernel.android.humhai.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.hackerkernel.android.humhai.R;
import com.hackerkernel.android.humhai.activity.CartActivity;
import com.hackerkernel.android.humhai.constant.Constants;
import com.hackerkernel.android.humhai.constant.EndPoints;
import com.hackerkernel.android.humhai.network.MyVolley;
import com.hackerkernel.android.humhai.parser.JsonParser;
import com.hackerkernel.android.humhai.pojo.CartItemListPojo;
import com.hackerkernel.android.humhai.pojo.SimplePojo;
import com.hackerkernel.android.humhai.storage.MySharedPreferences;
import com.hackerkernel.android.humhai.util.Util;

import org.json.JSONException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
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

            mDeleteBtn.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int pos = getAdapterPosition();

            //get id of cart item
            String cartId = list.get(pos).getCartId();
            deleteItemFromCart(cartId);
        }

        private void deleteItemFromCart(final String cartId) {
            if (Util.isNetworkAvailable()){
                pd.show();
                StringRequest request = new StringRequest(Request.Method.POST, EndPoints.MINUS_FOOD_FROM_CART, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        pd.dismiss();
                        parseDeleteItemResponse(response);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        pd.dismiss();
                        error.printStackTrace();
                        Log.e(TAG,"HUS: deleteItemFromCart: "+error.getMessage());
                        String errorString = MyVolley.handleVolleyError(error);
                        Toast.makeText(context,errorString,Toast.LENGTH_SHORT).show();
                    }
                }){
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String,String> params = new HashMap<>();
                        params.put(Constants.COM_APIKEY,Util.generateApiKey(sp.getUserMobile()));
                        params.put(Constants.COM_MOBILE,sp.getUserMobile());
                        params.put(Constants.COM_CART_ID,cartId);
                        return params;
                    }
                };
                mRequestQueue.add(request);
            }else {
                Toast.makeText(context, R.string.check_your_internet_connection,Toast.LENGTH_SHORT).show();
            }
        }

        private void parseDeleteItemResponse(String response) {
            try {
                SimplePojo pojo = JsonParser.SimpleParser(response);
                if (pojo.isReturned()){
                    //remove item from recyclerview
                    int pos = getAdapterPosition();
                    list.remove(pos);
                    notifyDataSetChanged();

                    //update item count on cart page
                    CartActivity.updateTotalItemCount("Total: "+list.size()+" Items");

                    //update items total cost on cart page
                    int currentcost = 0;
                    //calculate current cost
                    for (int i = 0; i < list.size(); i++) {
                        currentcost += Integer.parseInt(list.get(i).getPrice());
                    }
                    CartActivity.updateTotalItemCost(context.getString(R.string.rupee_sign)+" "+currentcost);

                    //show message
                    Toast.makeText(context,pojo.getMessage(),Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(context,pojo.getMessage(),Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Util.showParsingErrorAlert(context);
            }
        }
    }




}
