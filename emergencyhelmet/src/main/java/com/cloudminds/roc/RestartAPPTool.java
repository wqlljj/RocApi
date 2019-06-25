package com.cloudminds.roc;

import android.content.Context;
import android.content.Intent;

/**
 * Created by SX on 2018/1/29.
 */

public class RestartAPPTool {
    /**
     * @param context
     */
    public static void restartAPP(Context context,long Delayed){

        Intent intent1=new Intent(context,KillSelfService.class);
        intent1.putExtra("PackageName",context.getPackageName());
        intent1.putExtra("Delayed",Delayed);
        context.startService(intent1);

        android.os.Process.killProcess(android.os.Process.myPid());
    }
    public static void restartAPP(Context context){
        restartAPP(context,0);
    }
}
