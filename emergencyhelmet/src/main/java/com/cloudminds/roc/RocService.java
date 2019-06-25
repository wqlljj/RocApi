package com.cloudminds.roc;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

import com.cloudminds.rocapi.ROCApi;
import com.cloudminds.rocapi.bean.ActivationResponse;
import com.cloudminds.rocapi.bean.ReportAlarmBean;
import com.cloudminds.rocapi.bean.ReportAppInstallDataBean;
import com.cloudminds.rocapi.callback.ROCApiCallBack;
import com.cloudminds.rocapi.http.RespCallBack;
import com.cloudminds.rocapi.util.SharePreferenceUtils;
import com.cloudminds.rocapi.util.SystemUtil;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static android.content.Intent.ACTION_BATTERY_CHANGED;

public class RocService extends Service implements ROCApiCallBack {

    private String TAG = "RocService";
    private CallBack callBack;
    private BatteryChangedReceiver batteryChangedReceiver;

    public RocService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.e(TAG, "onBind: ");
        return new RocBinder();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "onStartCommand: ");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.e(TAG, "onUnbind: ");
        return super.onUnbind(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "onCreate: ");
        batteryChangedReceiver = new BatteryChangedReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_BATTERY_CHANGED);
        registerReceiver(batteryChangedReceiver, intentFilter);
        SoundPoolPlayer.getInstance().initSound(this.getApplicationContext());
        ROCApi.getInstance().init(this, this);
        startHeartBeat();
    }

    private void startHeartBeat() {
        toast("开启心跳");
        ROCApi.getInstance().startHeartbeat();
    }

    private void stopHeartBeat() {
        toast("停止心跳");
        ROCApi.getInstance().stopHeartbeat();
    }

    private void reportAppInstall() {
        toast("更新上报");
        ROCApi.getInstance().pushAppInstall(new
                ReportAppInstallDataBean(SystemUtil.PackgeName(),
                SystemUtil.VersionName(), SystemUtil.VersionCode()));
    }

    private void reportAttribute() {
        toast("属性上报");
//        ROCApi.getInstance().pushAttribute(new ReportAttributeBean.DataBean(SystemUtil.PackgeName(),
//                SystemUtil.VersionName() + "test", SystemUtil.VersionCode()));
    }

    private void reportConfig() {
        toast("配置上报");
        ROCApi.getInstance().pushConfig(new Gson().fromJson(SharePreferenceUtils.getPrefString("ConfigDataBean", ""), ReportConfigDataBean.class));
    }

    private void reportMetrics() {
        toast("监控上报");
        ROCApi.getInstance().pushMetrics(BatteryChangedReceiver.dataBean);
//        ROCApi.getInstance().reportMetrics(new MetricsDataBean(3000,1500,"50%"));
    }

    private void pushAlarm() {
        toast("异常上报");
        List<ReportAlarmBean.DataBean> data = new ArrayList<>();
        ReportAlarmBean.DataBean dataBean = new ReportAlarmBean.DataBean("ee", 2, System.currentTimeMillis(),
                "camera", 1, "app异常重启"
        );
        data.add(dataBean);
        ROCApi.getInstance().pushAlarm(data);
    }

    private void register() {
        toast("请求激活");
        ROCApi.getInstance().activation(SystemUtil.IMEI(),SystemUtil.IMEI(),new RespCallBack<String>() {
            @Override
            public void onResponse(String data) {
                Log.e(TAG, "onResponse: " + data);
                ActivationResponse activationResponse = new Gson().fromJson(data, ActivationResponse.class);
                Log.e(TAG, "onResponse: " + activationResponse);
                if (activationResponse != null && activationResponse.getCode() == 0) {
                    ActivationResponse.DataBean dataBean = activationResponse.getData();
                    SharePreferenceUtils.setPrefString("ConfigDataBean", new Gson().toJson(dataBean));
                    ROCApi.getInstance().setUserName(dataBean.getUserCode());
                    ROCApi.getInstance().setPassword(dataBean.getUserPwd());
                    ROCApi.getInstance().setTopic(dataBean.getPushToken());
                    ROCApi.getInstance().setTenantCode(dataBean.getTenantCode());
//                    ROCApi.setUserCode(dataBean.getUserCode());
//                    ROCApi.setServiceCode(dataBean.getServiceCode());
                    ActivationResponse.DataBean.PushAddressBean pushAddress = dataBean.getPushAddress();
                    ROCApi.getInstance().setPushServerIP(pushAddress.getPrivateIp());
                    try {
                        ROCApi.getInstance().setPushServerPort(Integer.valueOf(pushAddress.getPrivatePort()));
                        ROCApi.getInstance().reconnect();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e(TAG, "onResponse: MQTT地址有误！");
                    }
                    Toast.makeText(RocService.this, "激活成功", Toast.LENGTH_SHORT).show();
                } else if (activationResponse != null) {
                    Toast.makeText(RocService.this, "错误码：" + activationResponse.getCode() + "\n错误信息" + activationResponse.getMessages(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(String msg) {
                Toast.makeText(RocService.this, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public void initFinish() {
        Log.i(TAG, "RocApi initFinish: ");
        register();
    }

    @Override
    public void onConnectSuccess() {
        Log.i(TAG, "onConnectSuccess: PushService Connect Success");
        toast("PushService Connect Success");
    }

    @Override
    public void onConnectFail(String msg) {
        Log.i(TAG, "onConnectFail: PushService Connect failed  "+msg);
        toast("PushService Connect failed");
    }

    @Override
    public void onConnectLost() {
        toast("PushService连接异常");
        Log.i(TAG, "onConnectLost: ");
    }

    @Override
    public void onClose() {
        Log.i(TAG, "onClose: ");
        toast("PushService连接正常断开");
    }

    @Override
    public void message(String message) {
        Log.e(TAG, "message: " + message);
        String msg = "未知指令";
        try {
            JSONObject jsonObject = new JSONObject(message);
            String type = jsonObject.getString("type");
            switch (type) {
                case "operate":
                    switch (jsonObject.getJSONObject("data").getString("action")) {
                        case "rcu_reboot"://重启RCU
                            msg = "重启RCU";
//                            remoteContorl("remoteControlReboot");
                            break;
                        case "rcu_shutdown"://关闭RCU
                            msg = "关闭RCU";
//                            remoteContorl("remoteControlShutdown");
                            break;
                        case "robot_shutdown"://关闭Robot
                            msg = "关闭Robot";
                            break;
                        case "robot_wakeup"://唤醒Robot
                            msg = "唤醒Robot";
                            break;
                        case "robot_rest"://休眠Robot
                            msg = "休眠Robot";
                            break;
                        case "robot_reboot"://重启Robot
                            msg = "重启Robot";
                            break;
                        case "deactivate"://取消激活
                            msg = "取消激活";
                            break;
                        case "app_restart"://重启APP
                            msg = "重启APP";
                            break;
                    }
                    break;
                case "queryAttribute"://获取属性
                    msg = "获取属性";
                    SoundPoolPlayer.getInstance().playSound(1, 0);
                    reportAttribute();
                    break;
                case "queryMetrics"://获取监控
                    msg = "获取监控";
                    SoundPoolPlayer.getInstance().playSound(3, 0);
                    reportMetrics();
                    break;
                case "queryConfig"://获取配置
                    msg = "获取配置";
                    SoundPoolPlayer.getInstance().playSound(2, 0);
                    reportConfig();
                    break;
                case "queryLog"://获取日志
                    msg = "获取日志";
                    break;
            }
            toast(msg);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void remoteContorl(String methodName) {
        Class<PowerManager> powerManagerClass = PowerManager.class;
        try {
            Log.d(TAG, "remoteContorl: " + methodName);
            Method contorlM = powerManagerClass.getDeclaredMethod(methodName);
            contorlM.setAccessible(true);
            PowerManager pm = (PowerManager) this.getSystemService(Context.POWER_SERVICE);
            if (contorlM != null && pm != null) {
                contorlM.invoke(pm);
            } else {
                Log.e(TAG, "message: " + (contorlM != null) + "   " + (pm != null));
            }

        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void parseFail(String message, Exception e) {

    }

    private void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ROCApi.getInstance().onDestory();
        unregisterReceiver(batteryChangedReceiver);
    }

    public class RocBinder extends Binder {

        public void setRocIp(String ip) {
            ROCApi.getInstance().setRocHost(ip);
        }

        public void activateRoc() {
            register();
        }

        public void connectPushService() {
            ROCApi.getInstance().connect();
        }

        public void disconnectPushService() {
            ROCApi.getInstance().disconnect();
        }

        public void reportAttribute() {
            RocService.this.reportAttribute();
        }

        public void reportError() {
            RocService.this.pushAlarm();
        }

        public void reportAppInstall() {
            RocService.this.reportAppInstall();
        }

        public void startHeartBeat() {
            RocService.this.startHeartBeat();
        }

        public void stopHeartBeat() {
            RocService.this.stopHeartBeat();
        }

        public void reportConfig() {
            RocService.this.reportConfig();
        }

        public void reportMetrics() {
            RocService.this.reportMetrics();
        }

        public void setCallBack(CallBack callBack) {
            RocService.this.callBack = callBack;
        }
    }

    interface CallBack {
    }
}
