package com.hackerkernel.android.humhai.adapter;

import android.app.ProgressDialog;
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
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.hackerkernel.android.humhai.R;
import com.hackerkernel.android.humhai.activity.RestaurantFoodListActivity;
import com.hackerkernel.android.humhai.constant.Constants;
import com.hackerkernel.android.humhai.constant.EndPoints;
import com.hackerkernel.android.humhai.network.MyVolley;
import com.hackerkernel.android.humhai.parser.JsonParser;
import com.hackerkernel.android.humhai.pojo.RestaurantFoodCategoryListPojo;
import com.hackerkernel.android.humhai.pojo.RestaurantFoodListPojo;
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
public class RestaurantFoodListAdapter extends RecyclerView.Adapter<RestaurantFoodListAdapter.MyViewHolder> {
    private static final String TAG = RestaurantFoodListAdapter.class.getSimpleName();
    private Context context;
    private List<RestaurantFoodListPojo> list;
    private RequestQueue mRequestQueue;
    private MySharedPreferences sp;
    private ProgressDialog pd;

    public RestaurantFoodListAdapter(Context context, List<RestaurantFoodListPojo> list){
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
            if (Integer.parseInt(counterValue) > 0){
                minusFoodItemFromCartInBackground(foodId);
            }
        }

        private void addFoodToCart() {
            int pos = getAdapterPosition();
            String foodId = list.get(pos).getId();
            addFoodItemToCartInBackground(foodId);
        }

        /*
    * Method to add food item to cart in background
    * */
        private void addFoodItemToCartInBackground(final String foodId) {
            if (Util.isNetworkAvailable()){
                pd.show();
                StringRequest request = new StringRequest(Request.Method.POST, EndPoints.ADD_FOOD_TO_CART, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        pd.dismiss();
                        parseAddFoodResponse(response);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        pd.dismiss();
                        error.printStackTrace();
                        Log.e(TAG,"HUS: addFoodItemToCartInBackground: "+error.getMessage());
                        String errorString = MyVolley.handleVolleyError(error);
                        Toast.makeText(context,errorString,Toast.LENGTH_SHORT).show();
                    }
                }){
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String,String> params = new HashMap<>();
                        params.put(Constants.COM_APIKEY,Util.generateApiKey(sp.getUserMobile()));
                        params.put(Constants.COM_MOBILE,sp.getUserMobile());
                        params.put(Constants.COM_ID,foodId);
                        return params;
                    }
                };

                mRequestQueue.add(request);
            }else {
                Toast.makeText(context, R.string.check_your_internet_connection,Toast.LENGTH_SHORT).show();
            }
        }

        private void parseAddFoodResponse(String response) {
            try {
                SimplePojo pojo = JsonParser.SimpleParser(response);
                if (pojo.isReturned()){
                    //add counter
                    String counterValue = counter.getText().toString();
                    int counterValueInt = Integer.parseInt(counterValue);
                    int newCounterValue = counterValueInt + 1;
                    counter.setText(newCounterValue+"");
                    Toast.makeText(context,pojo.getMessage(),Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(context,pojo.getMessage(),Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Util.showParsingErrorAlert(context);
            }

        }

        /*
    * Method to minus food item from cart in background
    * */
        private void minusFoodItemFromCartInBackground(final String foodId) {
            if (Util.isNetworkAvailable()){
                pd.show();
                StringRequest request = new StringRequest(Request.Method.POST, EndPoints.MINUS_FOOD_FROM_CART, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        pd.dismiss();
                        parseMinusFoodResponse(response);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        pd.dismiss();
                        error.printStackTrace();
                        Log.e(TAG,"HUS: minusFoodItemFromCartInBackground: "+error.getMessage());
                        String errorString = MyVolley.handleVolleyError(error);
                        Toast.makeText(context,errorString,Toast.LENGTH_SHORT).show();
                    }
                }){
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String,String> params = new HashMap<>();
                        params.put(Constants.COM_APIKEY,Util.generateApiKey(sp.getUserMobile()));
                        params.put(Constants.COM_MOBILE,sp.getUserMobile());
                        params.put(Constants.COM_ID,foodId);
                        return params;
                    }
                };

                mRequestQueue.add(request);
            }else {
                Toast.makeText(context, R.string.check_your_internet_connection,Toast.LENGTH_SHORT).show();
            }
        }

        private void parseMinusFoodResponse(String response) {
            try {
                SimplePojo pojo = JsonParser.SimpleParser(response);
                if (pojo.isReturned()){
                    //minus counter
                    String counterValue = counter.getText().toString();
                    int counterValueInt = Integer.parseInt(counterValue);
                    if (counterValueInt > 0){
                        int newCounterValue = counterValueInt - 1;
                        counter.setText(newCounterValue+"");
                        Toast.makeText(context,pojo.getMessage(),Toast.LENGTH_SHORT).show();
                    }
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
