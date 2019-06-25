package com.cloudminds.roc;
import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.cloudminds.rocapi.ROCApi;
import com.cloudminds.rocapi.util.SystemUtil;
import java.util.List;
public class MainActivity extends AppCompatActivity implements View.OnClickListener, PermissionFragment.CallBack {

    private String TAG ="MainActivity";
    private TextView url;
    private TextView imei;
    private ServiceConnection conn;
    private RocService.RocBinder service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG, "onCreate: " );
        setContentView(R.layout.activity_main);
        findViewById(R.id.activation).setOnClickListener(this);
        findViewById(R.id.connect).setOnClickListener(this);
        findViewById(R.id.disconnect).setOnClickListener(this);
        findViewById(R.id.reportAttribute).setOnClickListener(this);
        findViewById(R.id.reportError).setOnClickListener(this);
        findViewById(R.id.reportInstall).setOnClickListener(this);
        findViewById(R.id.heartbeat).setOnClickListener(this);
        findViewById(R.id.reportAttribute).setOnClickListener(this);
        findViewById(R.id.reportConfig).setOnClickListener(this);
        findViewById(R.id.reportMetrics).setOnClickListener(this);
        url = ((TextView) findViewById(R.id.url));
        imei = ((TextView) findViewById(R.id.imei));
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
    boolean isInit=false;
    @Override
    protected void onResume() {
        super.onResume();
        if(!isInit) {
            isInit=true;
            if (!hasPermissions(this, Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_PHONE_STATE, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                requestPermission();
            } else {
                initData();
            }
        }
        Log.e(TAG, "onResume: " );
    }
    private void requestPermission(){
        findViewById(R.id.requestPer).setVisibility(View.VISIBLE);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.requestPer,PermissionFragment.newInstance("","")).commitNowAllowingStateLoss();
    }
    private void initData(){
        startRocService();
    }

    private void startRocService() {
        Intent intent=new Intent(this,RocService.class);
        startService(intent);
        conn = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                MainActivity.this.service = ((RocService.RocBinder) service);
                url.setText(ROCApi.getInstance().getRocHost());
                imei.setText(SystemUtil.IMEI());
                Log.e(TAG, "onServiceConnected: " );
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                Log.e(TAG, "onServiceDisconnected: " );
                service=null;
            }
        };
        bindService(intent, conn,BIND_AUTO_CREATE);
        if(getIntent().getBooleanExtra("moveToBack",false)) {
            Log.e(TAG, "startRocService: moveToBack" );
            this.moveTaskToBack(true);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.activation:
                service.setRocIp(url.getText().toString());
                service.activateRoc();
                break;
            case R.id.connect:
                service.connectPushService();
                    break;
            case R.id.disconnect:
                service.disconnectPushService();
                break;
            case R.id.reportAttribute:
                service.reportAttribute();
                break;
            case R.id.reportError:
                service.reportError();
                break;
            case R.id.reportInstall:
                service.reportAppInstall();
                break;
            case R.id.heartbeat:
                if(!ROCApi.ISHEARTBEAT) {
                    service.startHeartBeat();
                }else{
                    service.stopHeartBeat();
                }
                break;
            case R.id.reportConfig:
                service.reportConfig();
                break;
            case R.id.reportMetrics:
                service.reportMetrics();
                break;
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(conn!=null) {
            unbindService(conn);
        }
        Log.e(TAG, "onDestroy: " );
    }
    @Override
    public void onSuccess() {
        List<Fragment> fragmentList = getSupportFragmentManager().getFragments();
        for (Fragment fragment : fragmentList) {
            Log.e(TAG, "onSuccess: "+fragment );
            this.getSupportFragmentManager().beginTransaction().remove(fragment).commitNow();
        }
        findViewById(R.id.requestPer).setVisibility(View.GONE);
        initData();

    }

    @Override
    public void onFailure() {
        Toast.makeText(this,"权限申请失败",Toast.LENGTH_LONG).show();
    }
}
