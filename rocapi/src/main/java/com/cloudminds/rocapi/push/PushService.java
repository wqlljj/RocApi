package com.cloudminds.rocapi.push;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.cloudminds.rocapi.ROCApi;
import com.cloudminds.rocapi.bean.MQTTPushBean;
import com.cloudminds.rocapi.bean.ReportStateCallbackBean;
import com.cloudminds.rocapi.callback.ROCApiCallBack;
import com.cloudminds.rocapi.constant.Constants;
import com.cloudminds.rocapi.util.PingNet;
import com.cloudminds.rocapi.util.SystemUtil;
import com.google.gson.Gson;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.DisconnectedBufferOptions;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import static com.cloudminds.rocapi.constant.Constants.BROADCAST_MQTT_RECONNECT;
import static com.cloudminds.rocapi.push.PushService.ServiceType.DISCONNECTED;


public class PushService extends Service {
    public static final String TAG = "ROCApi/PushService";

    private static MqttAndroidClient client;
    private MqttConnectOptions conOpt;
    public static ServiceType type = ServiceType.EXCEPTION;
    static ArrayList<Object> pushMsg=new ArrayList<>();
    private ROCApiCallBack callBack;


    public enum ServiceType {EXCEPTION, INIT, CONNECTING, CONNECTED, DISCONNECTED}



    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "onStartCommand: " );
        // 服务器地址（协议+地址+端口号）
        Log.e(TAG, "onStartCommand: initTask" );
        if(!TextUtils.isEmpty(Constants.MQTT_IP)) {
            mHandler.post(initTask);
        }
        getPackageManager().setComponentEnabledSetting(
                new ComponentName(getPackageName(), PushService.class.getName()),
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
        return super.onStartCommand(intent, flags, startId);
    }
    public void setCallBack(ROCApiCallBack callBack){
        this.callBack=callBack;
    }
    public static boolean push(Object messageBean) {
        MQTTPushBean mqttPushBean = new MQTTPushBean("push", "" + getSeq(), UUID.randomUUID().toString(), SystemUtil.IMEI() + "|" + SystemUtil.IMEI(), "roc",
                System.currentTimeMillis(), Constants.TENANTCODE, messageBean);
        return publish(new Gson().toJson(mqttPushBean));
    }
    public static void pushSticky(Object messageBean) {
        if(PushService.type== ServiceType.CONNECTED){
            if(!PushService.push(messageBean)){
                Log.i(TAG, "pushSticky: add");
                pushMsg.add(messageBean);
            }else{
                Log.i(TAG, "pushSticky: success");
            }
        }else{
            Log.i(TAG, "pushSticky: add");
            pushMsg.add(messageBean);
        }
    }

    private static int seq = 0;

    private static int getSeq() {
        return (seq++) % Integer.MAX_VALUE;
    }


    public static boolean publish(String msg,String...topic) {
//        String topic = Constants.TOPIC;
        if(type!= ServiceType.CONNECTED){
            Log.e(TAG, "publish: 发送失败 type = "+type +"  "+msg);
            return false;
        }
        Log.e(TAG, "publish: " + msg);
        Integer qos = 0;
        Boolean retained = false;
        try {
            if (client.publish(topic.length<=0?"appPush2Roc":topic[0], msg.getBytes(), qos.intValue(), retained.booleanValue()) == null) {
                Log.e(TAG, "publish: 发送失败");
                return false;
            } else {
//                Log.e(TAG, "publish: 发送成功");
                return true;
            }
        } catch (MqttException e) {
            e.printStackTrace();
        }
        return false;
    }
    Runnable initTask = new Runnable() {
        @Override
        public void run() {
            Log.e(TAG, "run: type = "+type );
                init();
        }
    };

    private void init() {
        if (client != null) {
            disconnect();
        }
        type = ServiceType.INIT;
        Constants.CLIENTID = UUID.randomUUID().toString();
        client = new MqttAndroidClient(this, Constants.getMqttUrl(), Constants.CLIENTID);
        // 设置MQTT监听并且接受消息
        client.setCallback(mqttCallback);

        conOpt = new MqttConnectOptions();
        // 清除缓存
        conOpt.setCleanSession(false);
        conOpt.setAutomaticReconnect(true);
        // 设置超时时间，单位：秒
        conOpt.setConnectionTimeout(10);
        // 心跳包发送间隔，单位：秒
        conOpt.setKeepAliveInterval(5);
        // 用户名
        conOpt.setUserName(Constants.USERNAME);
        // 密码
        conOpt.setPassword(Constants.PASSWORD.toCharArray());
        conOpt.setMqttVersion(MqttConnectOptions.MQTT_VERSION_3_1_1);
        try {
            Log.d(TAG, "onCreate: Connecting to " + Constants.getMqttUrl());
            //开始连接
            client.connect(conOpt, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.d(TAG, "onSuccess: Success to connect to " + Constants.getMqttUrl());
                    DisconnectedBufferOptions disconnectedBufferOptions = new DisconnectedBufferOptions();
                    disconnectedBufferOptions.setBufferEnabled(true);
                    disconnectedBufferOptions.setBufferSize(100);
                    disconnectedBufferOptions.setPersistBuffer(false);
                    disconnectedBufferOptions.setDeleteOldestMessages(false);
                    client.setBufferOpts(disconnectedBufferOptions);
                    type = ServiceType.CONNECTING;
                    //成功连接以后开始订阅
//                    if(!isSubscribe) {
                        isSubscribe = true;
                        subscribeTopic();
//                    }
//                    callBack.onConnectSuccess();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    //连接失败
                    Log.d(TAG, "onFailure: Failed to connect to " + Constants.getMqttUrl());
                    if(exception instanceof MqttException) {
                        int reasonCode = ((MqttException) exception).getReasonCode();
                        Log.e(TAG, "onFailure: " + reasonCode);
                        if(!client.isConnected()){
                            reconnect();
                        }
                    }
                    callBack.onConnectFail(exception.getMessage());
                    exception.printStackTrace();
                    type= ServiceType.EXCEPTION;
                }
            });
        } catch (MqttException ex) {
            ex.printStackTrace();
        }
    }
    private void reconnect() {
        if(type == DISCONNECTED||client ==null){
            Log.e(TAG, "reconnect: type = "+type );
            return;
        }
        Log.d(TAG, "reconnect: ");
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                if(!client.isConnected()&& checkNetWork()){
                    if(checkNetWork()) {
                        sendBroadcast(new Intent(BROADCAST_MQTT_RECONNECT));
                    }else{
                        reconnect();
                    }
                }else{
                    Log.d(TAG, " ignore reconnect  "+client.isConnected()+"  " + checkNetWork());
                }
            }
        };
        Timer timer=new Timer("reconnect");
        timer.schedule(timerTask,5000);
    }

    Handler mHandler = new Handler();

    @Override
    public void onDestroy() {
        Log.e(TAG, "onDestroy: ");
        disconnect();
        callBack = null;
        super.onDestroy();
    }

    private void disconnect() {
        if(type==DISCONNECTED)return;
        try {
            type = DISCONNECTED;
            isSubscribe = false;
            if (client!=null) {
                if(client.isConnected()) {
                    client.disconnect();
                }
                client.unregisterResources();
            }
            client = null;
            callBack.onClose();
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }


    void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
    boolean isSubscribe=false;
    private void subscribeTopic() {
        try {
            Log.i(TAG, "subscribeTopic:  "+Constants.TOPIC);
            // 订阅myTopic话题
            IMqttToken subscribe = client.subscribe(Constants.TOPIC, 1);
            Log.e(TAG, "onSuccess:subscribe  " + subscribe);
            type = ServiceType.CONNECTED;
            Iterator<Object> iterator = pushMsg.iterator();
            while (iterator.hasNext()){
                Object messageBean = iterator.next();
                if(push(messageBean)){
                    pushMsg.remove(messageBean);
                }
            }
            callBack.onConnectSuccess();
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "onFailure: Failed to subscribe");
        }
    }

    // MQTT监听并且接受消息
    private MqttCallbackExtended mqttCallback = new MqttCallbackExtended() {


        @Override
        public void connectComplete(boolean reconnect, String serverURI) {
            if (reconnect||!isSubscribe) {
                subscribeTopic();
            }
        }

        @Override
        public void messageArrived(String topic, MqttMessage message) throws Exception {

            String str1 = new String(message.getPayload());
            String str2 = topic + ";qos:" + message.getQos() + ";retained:" + message.isRetained();
            Log.i(TAG, "messageArrived:" + str1);
            parseOrder(str1);
        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
            try {
                Log.e(TAG, "deliveryComplete: " + iMqttDeliveryToken.getMessage());
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void connectionLost(Throwable arg0) {
            Log.e(TAG, "connectionLost: "+type);
            callBack.onConnectLost();
            if (type != DISCONNECTED) {
                type = ServiceType.EXCEPTION;
                // 失去连接，重连
            }
        }
    };
    public static void pushStateCallback(String seq) {
        Log.e(TAG, "pushStateCallback: "+seq );
        if(type != ServiceType.CONNECTED){
            Log.e(TAG, "pushStateCallback: "+type );
            return;
        }
        MQTTPushBean mqttPushBean = new MQTTPushBean("push", seq, UUID.randomUUID().toString(), SystemUtil.IMEI() + "|" + SystemUtil.IMEI(), "roc",
                System.currentTimeMillis(), Constants.TENANTCODE, new ReportStateCallbackBean());
        publish(new Gson().toJson(mqttPushBean));
    }
    //
    private void parseOrder(String json){
        if(callBack!=null) {
            try {
                JSONObject jsonObject = new JSONObject(json);
                String seq = jsonObject.getString("seq");
                JSONObject message = jsonObject.getJSONObject("message");
                String msg = message.toString();
                Log.i(TAG, "parseOrder: "+msg);
                if(ROCApi.getInstance().getRobotType().equalsIgnoreCase("hamitao")) {
                    MessageHandler.handle(msg);
                }else{
                    Log.i(TAG, "parseOrder: "+ROCApi.getInstance().getRobotType()+"  hamitao");
                }
                callBack.message(msg);
//                MQTTPullBean mqttPullBean = new Gson().fromJson(json, MQTTPullBean.class);
                pushStateCallback(seq);
//                callBack.message(mqttPullBean.getMessage());
            } catch (Exception e) {
                callBack.parseFail(json, e);
            }
        }else{
            Log.e(TAG, "parseOrder: callBack == null" );
        }
    }

    /**
     * 判断网络是否连接
     */
    public boolean checkNetWork() {
        PingNet.PingNetEntity pingNetEntity = new PingNet.PingNetEntity(ROCApi.getInstance().getPushServerIP(), 1, 5, new StringBuffer());
        pingNetEntity = PingNet.ping(pingNetEntity);
        Log.i("testPing", pingNetEntity.getIp());
        Log.i("testPing", "time=" + pingNetEntity.getPingTime());
        Log.i("testPing", pingNetEntity.isResult() + "");
        return pingNetEntity.isResult();
//        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
//        if (info != null && info.isAvailable()) {
//            String name = info.getTypeName();
//            Log.i(TAG, "MQTT当前网络名称：" + name);
//            return true;
//        } else {
//            Log.i(TAG, "MQTT 没有可用网络");
//            return false;
//        }
    }
    PushBinder pushBinder = new PushBinder();
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return pushBinder;
    }

    public class PushBinder extends Binder {
        public void connect(){
            if(!TextUtils.isEmpty(Constants.MQTT_IP)) {
                mHandler.post(initTask);
            }else{
                callBack.onConnectFail("未配置PushServer的地址");
            }
        }
        public void disconnect(){
            PushService.this.disconnect();
        }
        public void setCallBack(ROCApiCallBack callBack){
            PushService.this.setCallBack(callBack);
        }
        public boolean isConnect(){
            return type==ServiceType.CONNECTED;
        }
        public ServiceType getServiceState(){
            return type;
        }
    }

}
