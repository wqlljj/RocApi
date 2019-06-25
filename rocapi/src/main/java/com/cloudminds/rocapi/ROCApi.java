package com.cloudminds.rocapi;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.util.Log;

import com.cloudminds.rocapi.bean.ActivationBean;
import com.cloudminds.rocapi.bean.ReportAlarmBean;
import com.cloudminds.rocapi.bean.ReportAppInstallDataBean;
import com.cloudminds.rocapi.bean.ReportBaseBean;
import com.cloudminds.rocapi.bean.ReportHeartbeatBean;
import com.cloudminds.rocapi.callback.ROCApiCallBack;
import com.cloudminds.rocapi.constant.Constants;
import com.cloudminds.rocapi.http.HCApiClient;
import com.cloudminds.rocapi.http.RespCallBack;
import com.cloudminds.rocapi.push.PushService;
import com.cloudminds.rocapi.util.SharePreferenceUtils;
import com.cloudminds.rocapi.util.SystemUtil;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.BIND_AUTO_CREATE;
import static com.cloudminds.rocapi.constant.Constants.DELAY_HEARTBEAT;
import static com.cloudminds.rocapi.constant.Constants.KEY_HEARTBEAT_DELAY;
import static com.cloudminds.rocapi.constant.Constants.KEY_HEARTBEAT_DELAY_ROC;

/**
 * Created by cloud on 2018/12/17.
 */

public class ROCApi {

    private static ROCApi rocApi;
    private Context context;
    private Intent intentService;
    private String TAG = "ROCApi";
    private HandlerThread thread;
    private Handler handler;
    public static boolean ISHEARTBEAT=false;
    private PushService.PushBinder pushService;
    private ServiceConnection serviceConnection;
    private ROCApiCallBack callBack;

    Runnable uploadHeartbeat = new Runnable() {
        @Override
        public void run() {
            if(PushService.type == PushService.ServiceType.CONNECTED) {
                pushHeartbeat();
            }else{
                Log.i(TAG, "run: Heartbeat"+PushService.type);
            }
            if (ISHEARTBEAT && handler != null) {
                handler.postDelayed(uploadHeartbeat, DELAY_HEARTBEAT);
            }
        }
    };
    public static ROCApi getInstance() {
        if (rocApi == null) {
            rocApi = new ROCApi();
        }
        return rocApi;
    }

    public void init(Context context, final ROCApiCallBack callBack) {
        Log.e(TAG, "init: " );
        this.context = context;
        this.callBack = callBack;
        SharePreferenceUtils.setContext(context);
        DELAY_HEARTBEAT = SharePreferenceUtils.getPrefLong(KEY_HEARTBEAT_DELAY,DELAY_HEARTBEAT);
        SystemUtil.init(context);
        HCApiClient.init();
        Constants.init();
        thread = new HandlerThread("uploadHeartbeat");
        thread.start();
        handler = new Handler(thread.getLooper());
        initPushService();
    }

    private void initPushService() {
        intentService = new Intent();
        intentService.setClassName(context, "com.cloudminds.rocapi.push.PushService");
        context.startService(intentService);
        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                Log.i(TAG, "onServiceConnected: ");
                pushService = ((PushService.PushBinder) service);
                pushService.setCallBack(callBack);
                callBack.initFinish();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                Log.i(TAG, "onServiceDisconnected: ");
                pushService = null;
            }
        };
        context.bindService(intentService, serviceConnection,BIND_AUTO_CREATE);
    }
    public void connect(){
        pushService.connect();
    }

    public void disconnect(){
        stopHeartbeat();
        pushService.disconnect();
    }

    public boolean isConnect(){
        return pushService.isConnect();
    }

    public PushService.ServiceType getServiceState(){
        return pushService.getServiceState();
    }

    public void startHeartbeat(){
        Log.i(TAG, "startHeartbeat: "+DELAY_HEARTBEAT);
        handler.removeCallbacks(uploadHeartbeat);
        handler.post(uploadHeartbeat);
        ISHEARTBEAT=true;
    }

    public void stopHeartbeat(){
        Log.i(TAG, "stopHeartbeat: ");
        ISHEARTBEAT=false;
        handler.removeCallbacks(uploadHeartbeat);
    }

    private boolean setHeartbeatInterval(long interval){
        if(!SharePreferenceUtils.getPrefBoolean(KEY_HEARTBEAT_DELAY_ROC,false)) {
            DELAY_HEARTBEAT = interval;
            SharePreferenceUtils.setSettingLong(KEY_HEARTBEAT_DELAY, DELAY_HEARTBEAT);
            return true;
        }else{
            return false;
        }
    }
    public long getHeartbeatInterval(){
        return DELAY_HEARTBEAT;
    }

    public void pushAppInstall(ReportAppInstallDataBean dataBean) {
        PushService.pushSticky(new ReportBaseBean("reportAppInstall",dataBean));
    }

    public void pushAlarm(List<ReportAlarmBean.DataBean> data){
        PushService.pushSticky(new ReportAlarmBean(data));
    }

    public void pushAttribute(Object dataBean) {
        PushService.pushSticky((new ReportBaseBean("reportAttribute",dataBean)));
    }

    public void pushConfig(Object dataBean) {
        PushService.pushSticky((new ReportBaseBean("reportConfig",dataBean)));
    }
    public void pushMetrics(Object dataBean) {
        PushService.pushSticky((new ReportBaseBean("reportMetrics",dataBean)));
    }

    public void activation(String robotId,String rcuId,final RespCallBack<String> callBack) {
        List<ActivationBean.Module> moduleList = new ArrayList<>();
        moduleList.add(new ActivationBean.Module(SystemUtil.IMEI()));
        HCApiClient.activation(robotId, rcuId, moduleList, callBack);
    }

    public void offline(String robotId,String rcuId, String userCode,String userPwd,String lang,final RespCallBack<String> callBack){
        HCApiClient.offline(robotId, rcuId, userCode, userPwd, lang, new RespCallBack<String>() {
            @Override
            public void onResponse(String data) {
                //点开PushServer
                disconnect();
                callBack.onResponse(data);
            }

            @Override
            public void onFailure(String msg) {
                callBack.onFailure(msg);
            }
        });
    }


    public void reconnect(){
        pushService.connect();
    }

    private void pushHeartbeat() {
        Log.i(TAG, "pushHeartbeat: ");
        PushService.push(new ReportHeartbeatBean());
    }

    public  String getUserName() {
        return Constants.USERNAME = SharePreferenceUtils.getPrefString(Constants.KEY_USER_NAME, Constants.USERNAME);
    }

    public  boolean setUserName(String userName) {
        if( !Constants.USERNAME.equals(userName)) {
            Constants.USERNAME = userName;
            SharePreferenceUtils.setPrefString(Constants.KEY_USER_NAME, Constants.USERNAME);
            return true;
        }
        return false;
    }

    public  String getPassword() {
        return Constants.PASSWORD = SharePreferenceUtils.getPrefString(Constants.KEY_PASSWORD, Constants.PASSWORD);
    }

    public  boolean setPassword(String pw) {
        if(!Constants.PASSWORD.equals(pw)) {
            Constants.PASSWORD = pw;
            SharePreferenceUtils.setPrefString(Constants.KEY_PASSWORD, pw);
            return true;
        }
        return false;
    }

    public  String getTopic() {
        return Constants.TOPIC = SharePreferenceUtils.getPrefString(Constants.KEY_TOPIC, Constants.TOPIC);
    }

    public  boolean setTopic(String topic) {
        if(!Constants.TOPIC .equals( topic)) {
            Constants.TOPIC = topic;
            SharePreferenceUtils.setPrefString(Constants.KEY_TOPIC, topic);
            return true;
        }
        return false;
    }

    public  String getTenantCode() {
        return Constants.TENANTCODE = SharePreferenceUtils.getPrefString(Constants.KEY_TENANT_CODE, Constants.TENANTCODE);
    }

    public  boolean setTenantCode(String tenantCode) {
        if(!Constants.TENANTCODE.equals(tenantCode)) {
            Constants.TENANTCODE = tenantCode;
            SharePreferenceUtils.setPrefString(Constants.KEY_TENANT_CODE, tenantCode);
            return true;
        }
        return false;
    }

    public  String getUserCode() {
        return Constants.USERCODE = SharePreferenceUtils.getPrefString(Constants.KEY_USER_CODE, Constants.USERCODE);
    }

    public  boolean setUserCode(String userCode) {
        if(!Constants.USERCODE.equals(userCode)) {
            Constants.USERCODE = userCode;
            SharePreferenceUtils.setPrefString(Constants.KEY_USER_CODE, userCode);
            return true;
        }
        return false;
    }

    public  String getServiceCode() {
        return Constants.SERVICECODE = SharePreferenceUtils.getPrefString(Constants.KEY_SERVICE_CODE, Constants.SERVICECODE);
    }

    public  boolean setServiceCode(String serviceCode) {
        if(!Constants.SERVICECODE.equals(serviceCode)) {
            Constants.SERVICECODE = serviceCode;
            SharePreferenceUtils.setPrefString(Constants.KEY_SERVICE_CODE, serviceCode);
            return true;
        }
        return false;
    }

    public  String getRobotType() {
        return Constants.SERVICECODE = SharePreferenceUtils.getPrefString(Constants.KEY_ROBOT_TYPE, Constants.SERVICECODE);
    }

    public  boolean setRobotType(String robotType) {
        if(!Constants.ROBOTTYPE.equals(robotType)) {
            Constants.ROBOTTYPE = robotType;
            SharePreferenceUtils.setPrefString(Constants.KEY_ROBOT_TYPE, Constants.ROBOTTYPE);
            return true;
        }
        return false;
    }

    public  String getPushServerIP() {
        return Constants.MQTT_IP = SharePreferenceUtils.getPrefString(Constants.KEY_MQTT_IP, Constants.MQTT_IP);
    }

    public  boolean setPushServerIP(String ip) {
        if(!Constants.MQTT_IP.equals(ip)) {
            Constants.MQTT_IP = ip;
            SharePreferenceUtils.setPrefString(Constants.KEY_MQTT_IP, ip);
            return true;
        }
        return false;
    }

    public  int getPushServerPort() {
        return Constants.MQTT_PORT = SharePreferenceUtils.getPrefInt(Constants.KEY_MQTT_PORT,Constants. MQTT_PORT);
    }

    public  boolean setPushServerPort(int port) {
        if(Constants.MQTT_PORT!=port) {
            Constants.MQTT_PORT = port;
            SharePreferenceUtils.setPrefInt(Constants.KEY_MQTT_PORT, port);
            return true;
        }
        return false;
    }

    public  void setRocHost(String host){
        HCApiClient.setRocHost(host);
    }

    public  String getRocHost(){
        return HCApiClient.getRocHost();
    }

    public  void onDestory(){
        context.unbindService(serviceConnection);
        context.stopService(intentService);
        thread.quitSafely();
        handler=null;
        SharePreferenceUtils.setContext(null);
        SystemUtil.clear();
        context=null;
    }
}
