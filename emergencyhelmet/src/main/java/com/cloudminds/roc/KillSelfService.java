package com.cloudminds.roc;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;


public class KillSelfService extends Service {

    private static long stopDelayed = 2000;
    private PowerManager.WakeLock wakeLock = null;
    private Handler handler;
    private String PackageName;
    private String TAG = "META/KillSelfService";
    private boolean startCall;

    public KillSelfService() {
        Log.e(TAG, "KillSelfService: ");
        handler = new Handler();
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        Log.e(TAG, "onStartCommand: ");
        PowerManager powerManager = (PowerManager) this.getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK, "Restart Lock");
        Log.e(TAG, "onStartCommand: wakeLock.acquire");
        if (!wakeLock.isHeld())
            wakeLock.acquire();
        try {
            stopDelayed = intent.getLongExtra("Delayed", 2000);
            PackageName = intent.getStringExtra("PackageName");
        } catch (Exception e) {
            e.printStackTrace();
            if (wakeLock.isHeld())
                wakeLock.release();
            wakeLock = null;
            KillSelfService.this.stopSelf();
        }
        startMeta();
        return super.onStartCommand(intent, flags, startId);
    }

    private void startMeta() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.e(TAG, "run: startActivity");
                Intent LaunchIntent = getPackageManager().getLaunchIntentForPackage(PackageName);
                startActivity(LaunchIntent);
            }
        }, stopDelayed);
        close();
    }

    private void close() {
        Log.e(TAG, "run: check  true wakeLock.release");
        if (wakeLock.isHeld())
            wakeLock.release();
        wakeLock = null;
        KillSelfService.this.stopSelf();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
