package com.cloudminds.roc;

import android.Manifest;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.cloudminds.rocapi.ROCApi;
import com.cloudminds.rocapi.bean.ActivationResponse;
import com.cloudminds.rocapi.bean.ReportAlarmBean;
import com.cloudminds.rocapi.bean.ReportAppInstallDataBean;
import com.cloudminds.rocapi.callback.ROCApiCallBack;
import com.cloudminds.rocapi.http.RespCallBack;
import com.cloudminds.rocapi.push.PushService;
import com.cloudminds.rocapi.util.SharePreferenceUtils;
import com.cloudminds.rocapi.util.SystemUtil;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import static android.content.Intent.ACTION_BATTERY_CHANGED;

public class MainActivity extends AppCompatActivity implements PermissionFragment.CallBack, ROCApiCallBack {

    private String TAG = "MainActivity";
    private boolean isRocInit = false;
    private TextView url;
    private TextView robotId;
    private TextView heartbeatDelay;
    private ROCApi rocApi;

    private BatteryChangedReceiver batteryChangedReceiver;
    private TextView rcuId;
    private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            onClick(position, view);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG, "onCreate: ");
        setContentView(R.layout.activity_main);
        url = ((TextView) findViewById(R.id.url));
        heartbeatDelay = ((TextView) findViewById(R.id.heartbeat_delay));
        robotId = ((TextView) findViewById(R.id.robotId));
        rcuId = ((TextView) findViewById(R.id.rcuId));
    }

    public static boolean hasPermissions(Context context, String... perms) {
        for (String perm : perms) {
            boolean hasPerm = (ContextCompat.checkSelfPermission(context, perm) == PackageManager.PERMISSION_GRANTED);
            if (!hasPerm) {
                return false;
            }
        }
        return true;
    }

    boolean isInit = false;

    @Override
    protected void onResume() {
        super.onResume();
        if (!isInit) {
            isInit = true;
            if (!hasPermissions(this, Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_PHONE_STATE, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                requestPermission();
            } else {
                initData();
            }
        }
        Log.e(TAG, "onResume: ");
    }

    private void requestPermission() {
        findViewById(R.id.requestPer).setVisibility(View.VISIBLE);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.requestPer, PermissionFragment.newInstance("", "")).commitNowAllowingStateLoss();
    }

    private void initData() {
        GridView gridView = (GridView) findViewById(R.id.gridView);
        String[] array = getResources().getStringArray(R.array.bts);
        gridView.setAdapter(new ArrayAdapter<String>(this.getApplicationContext(), R.layout.item_tv, array));
        gridView.setOnItemClickListener(onItemClickListener);
        batteryChangedReceiver = new BatteryChangedReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_BATTERY_CHANGED);
        registerReceiver(batteryChangedReceiver, intentFilter);
        rocApi = ROCApi.getInstance();
        rocApi.init(this, this);
        url.setText(rocApi.getRocHost());
        robotId.setText(SystemUtil.IMEI());
        rcuId.setText(SystemUtil.IMEI());
        heartbeatDelay.setText("" + rocApi.getHeartbeatInterval());
    }

    private void startHeartBeat() {
        toast("开启心跳");
//        if(!ROCApi.getInstance().setHeartbeatInterval(Long.valueOf(heartbeatDelay.getText().toString()))){
//            toast("心跳间隔不可需改");
//        }
        ROCApi.getInstance().startHeartbeat();
    }

    private void stopHeartBeat() {
        toast("停止心跳");
        ROCApi.getInstance().stopHeartbeat();
    }

    private void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    public void onClick(int position, View view) {
        switch (position) {
            case 0:
                if (isRocInit) {
                    rocApi.setRocHost(url.getText().toString());
                    register();
                } else {
                    toast("ROCApi 初始化未完成");
                }
                break;
            case 1:
                connectPushService();
                break;
            case 2:
                disconnectPushService();
                break;
            case 3:
                offline();
                break;
            case 4:
                pushAttribute();
                break;
            case 5:
                pushAppInstall();
                break;
            case 6:
                pushAlarm();
                break;
            case 7:
                if (!ROCApi.ISHEARTBEAT) {
                    startHeartBeat();
                    ((TextView) view).setText("停止心跳");
                } else {
                    stopHeartBeat();
                    ((TextView) view).setText("开启心跳");
                }
                break;
            case 8:
                pushConfig();
                break;
            case 9:
                pushMetrics();
                break;
            case 10:
                String json="{" +
                            "\"id\": \"push\"," +
                            "\"seq\": 85," +
                            "\"sid\": \"c4514018-45b0-4973-b031-672fa7ceaa4f\"," +
                            "\"from\": \"ROC\"," +
                            "\"to\": \"App\"," +
                            "\"message\": {" +
                                "\"type\": \"operate\"," +
                                "\"data\": {" +
                                    "\"action\": \"configure\"," +
                                    "\"id\": \"035628bf-45b6-4f2f-8b97-7e2c1ecff33a\"," +
                                    "\"param\": {" +
                                        "\"onlinePing\":{" +
                                            "\"check\":\"true\","+
                                            "\"period\":10"+
                                        "}"+
                                    "}" +
                                "}" +
                            "}" +
                        "}";
                PushService.publish(json,rocApi.getTopic());
                break;
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        rocApi.onDestory();
        unregisterReceiver(batteryChangedReceiver);
        Log.e(TAG, "onDestroy: ");
    }

    @Override
    public void onSuccess() {
        List<Fragment> fragmentList = getSupportFragmentManager().getFragments();
        for (Fragment fragment : fragmentList) {
            Log.e(TAG, "onSuccess: " + fragment);
            this.getSupportFragmentManager().beginTransaction().remove(fragment).commitNow();
        }
        findViewById(R.id.requestPer).setVisibility(View.GONE);
        initData();

    }

    @Override
    public void onFailure() {
        Toast.makeText(this, "权限申请失败", Toast.LENGTH_LONG).show();
    }

    public void connectPushService() {
        ROCApi.getInstance().connect();
    }

    public void disconnectPushService() {
        ROCApi.getInstance().disconnect();
    }

    private void offline() {
        ROCApi.getInstance().offline(robotId.getText().toString(), rcuId.getText().toString(), rocApi.getUserCode(), rocApi.getPassword(), "zh_CN", new RespCallBack<String>() {
            @Override
            public void onResponse(String data) {
                Log.i(TAG, "onResponse: " + data);
                toast("已下线");
            }

            @Override
            public void onFailure(String msg) {
                Log.i(TAG, "onFailure: " + msg);
                toast(msg);
            }
        });
    }

    public static String md5(String string) {
        if (TextUtils.isEmpty(string)) {
            return "";
        }
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
            byte[] bytes = md5.digest(string.getBytes());
            String result = "";
            for (byte b : bytes) {
                String temp = Integer.toHexString(b & 0xff);
                if (temp.length() == 1) {
                    temp = "0" + temp;
                }
                result += temp;
            }
            return result;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    @Override
    public void parseFail(String message, Exception e) {

    }

    @Override
    public void initFinish() {
        Log.i(TAG, "RocApi initFinish: ");
        isRocInit = true;
    }

    @Override
    public void onConnectSuccess() {
        Log.i(TAG, "onConnectSuccess: PushService Connect Success");
        toast("PushService Connect Success");
    }

    @Override
    public void onConnectFail(String msg) {
        Log.i(TAG, "onConnectSuccess: PushService Connect failed  "+msg);
        toast("PushService Connect failed\n"+msg);
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
                        case "configure":
                            msg = "配置策略";
                            break;
                    }
                    break;
                case "queryAttribute"://获取属性
                    msg = "获取属性";
                    pushAttribute();
                    break;
                case "queryMetrics"://获取监控
                    msg = "获取监控";
                    pushMetrics();
                    break;
                case "queryConfig"://获取配置
                    msg = "获取配置";
                    pushConfig();
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

    private void pushAppInstall() {
        toast("更新上报");
        ROCApi.getInstance().pushAppInstall(new
                ReportAppInstallDataBean(SystemUtil.PackgeName(),
                SystemUtil.VersionName(), SystemUtil.VersionCode()));
    }

    private void pushAttribute() {
        toast("属性上报");
        ROCApi.getInstance().pushAttribute(new AttributeBean(SystemUtil.PackgeName(),
                SystemUtil.VersionName() + "test_2", SystemUtil.VersionCode()));
    }

    private void pushConfig() {
        toast("配置上报");
        ROCApi.getInstance().pushConfig(new Gson().fromJson(SharePreferenceUtils.getPrefString("ConfigDataBean", ""), ReportConfigDataBean.class));
    }

    private void pushMetrics() {
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
        rocApi.activation(robotId.getText().toString(), rcuId.getText().toString(), new RespCallBack<String>() {
            @Override
            public void onResponse(String data) {
                Log.e(TAG, "onResponse: " + data);
                ActivationResponse activationResponse = new Gson().fromJson(data, ActivationResponse.class);
                Log.e(TAG, "onResponse: " + activationResponse);
                if (activationResponse != null && activationResponse.getCode() == 0) {
                    ActivationResponse.DataBean dataBean = activationResponse.getData();
                    SharePreferenceUtils.setPrefString("ConfigDataBean", new Gson().toJson(dataBean));
                    rocApi.setUserName(dataBean.getUserCode());
                    rocApi.setPassword(dataBean.getUserPwd());
                    rocApi.setTopic(dataBean.getPushToken());
                    rocApi.setTenantCode(dataBean.getTenantCode());
                    rocApi.setUserCode(dataBean.getUserCode());
                    rocApi.setServiceCode(dataBean.getServiceCode());
                    rocApi.setRobotType(dataBean.getRobotType());
                    ActivationResponse.DataBean.PushAddressBean pushAddress = dataBean.getPushAddress();
                    rocApi.setPushServerIP(pushAddress.getPrivateIp());
                    try {
                        rocApi.setPushServerPort(Integer.valueOf(pushAddress.getPrivatePort()));
                        ROCApi.getInstance().reconnect();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e(TAG, "onResponse: MQTT地址有误！");
                    }
                    toast("激活成功");
                } else if (activationResponse != null) {
                    toast("错误码：" + activationResponse.getCode() + "\n错误信息" + activationResponse.getMessages());
                }
            }

            @Override
            public void onFailure(String msg) {
                toast(msg);
            }
        });
    }
}
