package com.cloudminds.roc;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;
import android.util.Log;

public class BatteryChangedReceiver extends BroadcastReceiver {
    public static MetricsDataBean dataBean=new MetricsDataBean();
    private String TAG = "BatteryChangedReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        Log.e(TAG, "onReceive: " +action);
        if (action.equalsIgnoreCase(Intent.ACTION_BATTERY_CHANGED)) {
            // 当前电池的电压
            int voltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1);
            // 电池的健康状态
            int health = intent.getIntExtra(BatteryManager.EXTRA_HEALTH, -1);
            switch (health) {
                case BatteryManager.BATTERY_HEALTH_GOOD:
                    dataBean.setHealthState("BATTERY_HEALTH_GOOD");
                case BatteryManager.BATTERY_HEALTH_COLD:
                    dataBean.setHealthState("BATTERY_HEALTH_COLD");
                    break;
                case BatteryManager.BATTERY_HEALTH_DEAD:
                    dataBean.setHealthState("BATTERY_HEALTH_DEAD");
                    break;
                case BatteryManager.BATTERY_HEALTH_OVERHEAT:
                    dataBean.setHealthState("BATTERY_HEALTH_OVERHEAT");
                    break;
                case BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE:
                    dataBean.setHealthState("BATTERY_HEALTH_OVER_VOLTAGE");
                    break;
                case BatteryManager.BATTERY_HEALTH_UNKNOWN:
                    dataBean.setHealthState("BATTERY_HEALTH_UNKNOWN");
                    break;
                case BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE:
                    dataBean.setHealthState("BATTERY_HEALTH_UNSPECIFIED_FAILURE");
                    break;
                default:
                    break;
            }
            // 电池当前的电量, 它介于0和 EXTRA_SCALE之间
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            dataBean.setPercent(level+"%");
            // 电池电量的最大值
            int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            dataBean.setResidueCapacity(scale);
            dataBean.setResidueCapacity(scale*level/100);
            // 当前手机使用的是哪里的电源
            int pluged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
            switch (pluged) {
                case BatteryManager.BATTERY_PLUGGED_AC:
                    // 电源是AC charger.[应该是指充电器]
                    dataBean.setPluged("电源是AC charger.");
                    break;
                case BatteryManager.BATTERY_PLUGGED_USB:
                    // 电源是USB port
                    dataBean.setPluged("电源是USB port");
                    break;
                default:
                    break;
            }
            int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
            switch (status) {
                case BatteryManager.BATTERY_STATUS_CHARGING:
                    // 正在充电
                    dataBean.setBatteryState("正在充电");
                    break;
                case BatteryManager.BATTERY_STATUS_DISCHARGING:
                    dataBean.setBatteryState("BATTERY_STATUS_DISCHARGING");
                    break;
                case BatteryManager.BATTERY_STATUS_FULL:
                    // 充满
                    dataBean.setBatteryState("充满");
                    break;
                case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
                    // 没有充电
                    dataBean.setBatteryState("没有充电");
                    break;
                case BatteryManager.BATTERY_STATUS_UNKNOWN:
                    // 未知状态
                    dataBean.setBatteryState("未知状态");
                    break;
                default:
                    break;
            }
            // 电池使用的技术。比如，对于锂电池是Li-ion
            String technology = intent.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY);
            // 当前电池的温度
            int temperature = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1);
            String str = "voltage = " + voltage + " technology = " + technology + " temperature = " + temperature;
            dataBean.setOther(str);
        } else if (action.equalsIgnoreCase(Intent.ACTION_BATTERY_LOW)) {
            // 表示当前电池电量低
        } else if (action.equalsIgnoreCase(Intent.ACTION_BATTERY_OKAY)) {
            // 表示当前电池已经从电量低恢复为正常
            System.out.println("BatteryChangedReceiver ACTION_BATTERY_OKAY---");
        }
        Log.e(TAG, "onReceive: "+dataBean );
    }
}
