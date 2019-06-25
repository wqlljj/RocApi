package com.cloudminds.rocapi.constant;

import android.util.Log;

import com.cloudminds.rocapi.ROCApi;
import com.cloudminds.rocapi.util.ThreadPoolUtils;

/**
 * Created by cloud on 2018/7/12.
 */

public class Constants {
    private static String TAG = "Constants";

    public static String MQTT_IP = "";
    public static int MQTT_PORT = -1;
    private static String MQTT_URL = "tcp://%s:%d";
    public static String CLIENTID = "aface";
    public static  String USERNAME = "cloud";
    public static  String PASSWORD = "123456";
    public static  String TOPIC = "";
    public static String TENANTCODE = "cloudmind";
    public static String USERCODE = "mang";
    public static String SERVICECODE = "2000";
    public static String ROBOTTYPE  = "";

    public static long DELAY_HEARTBEAT = 600000;


    public static String ROCIP = "10.155.0.135:31802";
    public static  String ROCURL="https://"+ ROCIP;

    public static final String KEY_MQTT_IP = "com.arcsoft.cloudminds.hangingfacerecognition.KEY_MQTT_IP";
    public static final String KEY_MQTT_PORT = "com.arcsoft.cloudminds.hangingfacerecognition.KEY_MQTT_PORT";
    public static final String KEY_USER_NAME = "com.arcsoft.cloudminds.hangingfacerecognition.KEY_USER_NAME";
    public static final String KEY_PASSWORD = "com.arcsoft.cloudminds.hangingfacerecognition.KEY_PASSWORD";
    public static final String KEY_TOPIC = "com.arcsoft.cloudminds.hangingfacerecognition.KEY_TOPIC";
    public static final String KEY_TENANT_CODE= "com.arcsoft.cloudminds.hangingfacerecognition.KEY_TENANT_CODE";
    public static final String KEY_HEARTBEAT_DELAY= "com.arcsoft.cloudminds.hangingfacerecognition.KEY_HEARTBEAT_DELAY";
    public static final String KEY_USER_CODE= "com.arcsoft.cloudminds.hangingfacerecognition.KEY_USER_CODE";
    public static final String KEY_SERVICE_CODE= "com.arcsoft.cloudminds.hangingfacerecognition.KEY_SERVICE_CODE";
    public static final String KEY_ROBOT_TYPE= "com.arcsoft.cloudminds.hangingfacerecognition.KEY_ROBOT_TYPE";
    public static final String KEY_HEARTBEAT_DELAY_ROC= "com.arcsoft.cloudminds.hangingfacerecognition.KEY_HEARTBEAT_DELAY_ROC";



    public static final String BROADCAST_MQTT_RECONNECT = "com.arcsoft.cloudminds.hangingfacerecognition.BROADCAST_MQTT_RECONNECT";

    public static void init() {
        ThreadPoolUtils.init();
        ThreadPoolUtils.execute(new Runnable() {
            @Override
            public void run() {
                getMqttUrl();
                ROCApi.getInstance().getPassword();
                ROCApi.getInstance().getUserName();
                ROCApi.getInstance().getTopic();
            }
        });
    }

    public static String getMqttUrl() {
        String url = String.format(MQTT_URL, ROCApi.getInstance().getPushServerIP(), ROCApi.getInstance().getPushServerPort());
        Log.e(TAG, "getMQTT_URL: " + url);
        return url;
    }

}
