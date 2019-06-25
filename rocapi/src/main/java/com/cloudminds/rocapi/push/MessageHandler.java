package com.cloudminds.rocapi.push;

import android.util.Log;

import com.cloudminds.rocapi.ROCApi;
import com.cloudminds.rocapi.util.SharePreferenceUtils;

import org.json.JSONException;
import org.json.JSONObject;

import static com.cloudminds.rocapi.constant.Constants.DELAY_HEARTBEAT;
import static com.cloudminds.rocapi.constant.Constants.KEY_HEARTBEAT_DELAY;
import static com.cloudminds.rocapi.constant.Constants.KEY_HEARTBEAT_DELAY_ROC;

/**
 * Created by cloud on 2019/5/21.
 */

public class MessageHandler {

    private static String TAG = "MessageHandler";

    public static void handle(String msg){
        try {
            JSONObject jsonObject = new JSONObject(msg);
            if(jsonObject.getString("type").equals("operate")){
                JSONObject data = jsonObject.getJSONObject("data");
                if(data.getString("action").equals("configure")){
                    JSONObject param = data.getJSONObject("param");
                    if(param.has("onlinePing")) {
                        JSONObject onlinePing = param.getJSONObject("onlinePing");
                        String check = onlinePing.getString("check");
                        int period = onlinePing.getInt("period");
                        SharePreferenceUtils.setPrefBoolean(KEY_HEARTBEAT_DELAY_ROC, true);
                        DELAY_HEARTBEAT = period * 60 * 1000;
                        SharePreferenceUtils.setSettingLong(KEY_HEARTBEAT_DELAY, DELAY_HEARTBEAT);
                        Log.i(TAG, "handle: 心跳设置 ：" + DELAY_HEARTBEAT + "ms");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
