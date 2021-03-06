package com.hackerkernel.android.humhai.parser;


import com.hackerkernel.android.humhai.constant.Constants;
import com.hackerkernel.android.humhai.pojo.CartItemListPojo;
import com.hackerkernel.android.humhai.pojo.DiscountOffersListPojo;
import com.hackerkernel.android.humhai.pojo.RestaurantFoodCategoryListPojo;
import com.hackerkernel.android.humhai.pojo.RestaurantFoodListPojo;
import com.hackerkernel.android.humhai.pojo.RestaurantFoodTypeListPojo;
import com.hackerkernel.android.humhai.pojo.RestaurantListPojo;
import com.hackerkernel.android.humhai.pojo.SimplePojo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by husain on 6/24/2016.
 */
public class JsonParser {
    private static final String TAG = JsonParser.class.getSimpleName();

    public static SimplePojo SimpleParser(String response) throws JSONException {
        JSONObject jo = new JSONObject(response);
        SimplePojo simplePojo = new SimplePojo();
        simplePojo.setMessage(jo.getString(Constants.COM_MESSAGE));
        simplePojo.setReturned(jo.getBoolean(Constants.COM_RETURN));
        return simplePojo;
    }

    public static List<RestaurantListPojo> RestaurantListParser(JSONArray dataArray) throws JSONException {
        List<RestaurantListPojo> list = new ArrayList<>();
        for (int i = 0; i < dataArray.length(); i++) {
            JSONObject jo = dataArray.getJSONObject(i);
            RestaurantListPojo pojo = new RestaurantListPojo();
            pojo.setId(jo.getString(Constants.COM_ID));
            pojo.setName(jo.getString(Constants.COM_NAME));
            pojo.setImage(jo.getString(Constants.COM_IMAGE));
            pojo.setDeliveryTime(jo.getString(Constants.RES_LIST_DELIVERY_TIME));
            list.add(pojo);
        }
        return list;
    }

    public static List<RestaurantFoodTypeListPojo> RestaurantFoodTypeListParser(JSONArray dataArray) throws JSONException {
        List<RestaurantFoodTypeListPojo> list = new ArrayList<>();
        for (int i = 0; i < dataArray.length(); i++) {
            JSONObject jo = dataArray.getJSONObject(i);
            RestaurantFoodTypeListPojo pojo = new RestaurantFoodTypeListPojo();
            pojo.setId(jo.getString(Constants.COM_ID));
            pojo.setName(jo.getString(Constants.COM_NAME));
            pojo.setImage(jo.getString(Constants.COM_IMAGE));
            pojo.setHotelId(jo.getString(Constants.COM_HOTEL_ID));
            list.add(pojo);
        }
        return list;
    }

    public static List<RestaurantFoodCategoryListPojo> RestaurantFoodCategoryistParser(JSONArray dataArray) throws JSONException {
        List<RestaurantFoodCategoryListPojo> list = new ArrayList<>();
        for (int i = 0; i < dataArray.length(); i++) {
            JSONObject jo = dataArray.getJSONObject(i);
            RestaurantFoodCategoryListPojo pojo = new RestaurantFoodCategoryListPojo();
            pojo.setId(jo.getString(Constants.COM_ID));
            pojo.setName(jo.getString(Constants.COM_NAME));
            pojo.setImage(jo.getString(Constants.COM_IMAGE));
            pojo.setHotelId(jo.getString(Constants.COM_HOTEL_ID));
            pojo.setFoodTypeId(jo.getString(Constants.COM_FOOD_TYPE_ID));
            pojo.setCount(jo.getString(Constants.COM_COUNT));
            list.add(pojo);
        }
        return list;
    }

    public static List<DiscountOffersListPojo> DiscountOfferListParser(JSONArray dataArray) throws JSONException {
        List<DiscountOffersListPojo> list = new ArrayList<>();
        for (int i = 0; i < dataArray.length(); i++) {
            JSONObject jo = dataArray.getJSONObject(i);
            DiscountOffersListPojo pojo = new DiscountOffersListPojo();
            pojo.setId(jo.getString(Constants.COM_ID));
            pojo.setTitle(jo.getString(Constants.COM_TITLE));
            pojo.setDescription(jo.getString(Constants.COM_DESCRIPTION));
            pojo.setImageUrl(jo.getString(Constants.COM_IMAGE));
            pojo.setTimestamp(jo.getString(Constants.COM_TIMESTAMP));
            list.add(pojo);
        }
        return list;
    }

    /*
    * Method to parse food list
    * */
    public static List<RestaurantFoodListPojo> RestaurantFoodListParser(JSONArray dataArray) throws JSONException {
        List<RestaurantFoodListPojo> list = new ArrayList<>();
        for (int i = 0; i < dataArray.length(); i++) {
            JSONObject jo = dataArray.getJSONObject(i);
            RestaurantFoodListPojo pojo = new RestaurantFoodListPojo();
            pojo.setId(jo.getString(Constants.COM_ID));
            pojo.setHotelId(jo.getString(Constants.COM_HOTEL_ID));
            pojo.setFoodCategoryId(jo.getString(Constants.COM_FOOD_CATEGORY_ID));
            pojo.setName(jo.getString(Constants.COM_NAME));
            pojo.setImageUrl(jo.getString(Constants.COM_IMAGE));
            pojo.setUnit(jo.getString(Constants.COM_UNIT));
            pojo.setPrice(jo.getString(Constants.COM_PRICE));
            list.add(pojo);
        }
        return list;
    }

    /*
    * Method to parse cart item response
    * */
    public static List<CartItemListPojo> CartItemListParser(JSONArray jsonArray) throws JSONException {
        List<CartItemListPojo> list = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jo = jsonArray.getJSONObject(i);
            CartItemListPojo c = new CartItemListPojo();
            c.setCartId(jo.getString(Constants.COM_CART_ID));
            c.setFoodId(jo.getString(Constants.COM_FOOD_ID));
            c.setName(jo.getString(Constants.COM_NAME));
            c.setImage(jo.getString(Constants.COM_IMAGE));
            c.setPrice(jo.getString(Constants.COM_PRICE));
            c.setUnit(jo.getString(Constants.COM_UNIT));
            list.add(c);
        }
        return list;
    }
}

