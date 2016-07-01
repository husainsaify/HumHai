package com.hackerkernel.android.humhai.parser;



import com.hackerkernel.android.humhai.constant.Constants;
import com.hackerkernel.android.humhai.pojo.SimplePojo;

import org.json.JSONException;
import org.json.JSONObject;

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
}
