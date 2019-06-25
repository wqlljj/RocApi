package com.cloudminds.rocapi.util;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;

import java.lang.reflect.Method;

/**
 * Created by cloud on 2018/7/6.
 */

public class SystemUtil {
    private static String TAG = "SystemUtil";
    private static Context context;

    /**
     * 获取程序的权限
     */
    public static void init(Context context){
        SystemUtil.context = context;
    }
    public static void clear(){
        SystemUtil.context=null;
    }
    public static String[] AppPremission() {
        try {
            PackageInfo packinfo = context.getPackageManager().getPackageInfo(context.getPackageName(),
                    PackageManager.GET_PERMISSIONS);
            // 获取到所有的权限
            return packinfo.requestedPermissions;

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();

        }
        return null;
    }

    public boolean checkPermission(String... permissions){
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    /**
     * 获取程序的签名
     */
    public static String AppSignature() {
        try {
            PackageInfo packinfo = context.getPackageManager().getPackageInfo(context.getPackageName(),
                    PackageManager.GET_SIGNATURES);
            // 获取到所有的权限
            return packinfo.signatures[0].toCharsString();

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();

        }
        return "No Search";
    }

    /**
     * 获得程序图标
     */
    public static Drawable AppIcon() {
        try {
            ApplicationInfo info = context.getPackageManager().getApplicationInfo(
                    context.getPackageName(), 0);
            return info.loadIcon(context.getPackageManager());
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();

        }
        return null;
    }

    /**
     * 获得程序名称
     */
    public static String AppName() {
        try {
            ApplicationInfo info = context.getPackageManager().getApplicationInfo(context.getPackageName(), 0);
            return info.loadLabel(context.getPackageManager()).toString();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "No Search";
    }

    /**
     * 获得软件版本号
     */
    public static int VersionCode() {
        int versioncode = 0;
        try {
            versioncode = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return versioncode;
    }

    /**
     * 获得软件版本名
     */
    public static String VersionName() {
        String versionname = "unknow";
        try {
            versionname =context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return versionname;
    }

    /**
     * 得到软件包名
     */
    public static String PackgeName() {
        String packgename = "unknow";
        try {
            packgename = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0).packageName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return packgename;
    }

    /**
     * 获得imei号
     */
    @SuppressLint("MissingPermission")
    public static String IMEI() {
        String imei = "NO Search";
        TelephonyManager telephonyManager = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        imei = telephonyManager.getDeviceId();
        return imei;
    }

    /**
     * 获得imsi号
     */
    @SuppressLint("MissingPermission")
    public static String IMSI() {
        String imsi = "NO Search";
        TelephonyManager telephonyManager = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        imsi = telephonyManager.getSubscriberId();
        return imsi;
    }

    /**
     * 得到手机产品序列号
     */
    public static String SN() {
        String sn = "NO Search";
        String serial = android.os.Build.SERIAL;// 第二种得到序列号的方法
        try {
            Class<?> c = Class.forName("android.os.SystemProperties");
            Method get = c.getMethod("get", String.class);
            sn = (String) get.invoke(c, "ro.serialno");
        } catch (Exception e) {

            e.printStackTrace();
        }
        return sn;
    }

    /**
     * 获得手机sim号
     */
    @SuppressLint("MissingPermission")
    public static String SIM() {
        String sim = "NO Search";
        TelephonyManager telephonyManager = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        sim = telephonyManager.getSimSerialNumber();

        return sim;
    }

    /**
     * 返回安卓设备ID
     */
    public static String ID() {
        String id = "NO Search";
        id = android.provider.Settings.Secure.getString(
                context.getContentResolver(),
                android.provider.Settings.Secure.ANDROID_ID);

        return id;
    }

    /**
     * 得到设备mac地址
     */
    public static String MAC() {
        String mac = "NO Search";
        WifiManager manager = (WifiManager) context
                .getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = manager.getConnectionInfo();
        mac = info.getMacAddress();
        return mac;
    }

    /**
     * 得到当前系统国家和地区
     */
    public static String Country() {
        String country = "NO Search";
        country = context.getResources().getConfiguration().locale.getCountry();
        return country;
    }

    /**
     * 得到当前系统语言
     */
    public static String Language() {
        String language = "NO Search";
        String country = context.getResources().getConfiguration().locale
                .getCountry();
        language = context.getResources().getConfiguration().locale
                .getLanguage();
        // 区分简体和繁体中文
        if (language.equals("zh")) {
            if (country.equals("CN")) {
                language = "Simplified Chinese";
            } else {
                language = "Traditional Chinese";
            }
        }
        return language;
    }

    /**
     * 返回系统屏幕的高度（像素单位）
     */
    public static int Height() {
        int height = 0;
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        height = dm.heightPixels;
        return height;
    }

    /**
     * 返回系统屏幕的宽度（像素单位）
     */
    public static int Width() {
        int width = 0;
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        width = dm.widthPixels;
        return width;
    }
    public static float density() {
        return context.getResources().getDisplayMetrics().density;
    }
    public static float scaledDensity() {
        return context.getResources().getDisplayMetrics().scaledDensity;
    }

    public static int densityDpi(Activity activity) {
        return context.getResources().getDisplayMetrics().densityDpi;  // 屏幕密度DPI（120 / 160 / 240）
    }
}
