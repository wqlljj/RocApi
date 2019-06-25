package com.cloudminds.roc;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

public class StartSelfReceiver extends BroadcastReceiver {

    private String TAG = "StartSelfReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e(TAG, "onReceive: "+intent.getAction() );
        switch (intent.getAction()){
            case "android.intent.action.ACTION_SHUTDOWN":
                context.stopService(new Intent(context,RocService.class));
                break;
            case "android.intent.action.BOOT_COMPLETED":
//               Intent startService=new Intent(context,RocService.class);
//
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                    context.startForegroundService(startService);
//                }else{
//                    context.startService(startService);
//                }

                Intent start = new Intent();
                ComponentName cmp = new ComponentName(context.getPackageName(), "com.cloudminds.roc.MainActivity");
                start.setAction(Intent.ACTION_MAIN);
                start.addCategory(Intent.CATEGORY_LAUNCHER);
                start.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                start.putExtra("moveToBack", true);
                start.setComponent(cmp);
                context.startActivity(start);

                break;
        }
    }
}
