package com.cloudminds.rocapi.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * 轻量级存储工具
 */
public class SharePreferenceUtils {

	private static Context gContext;
	private static String TAG = "SharePreferenceUtils";

	public static void setContext(Context context){
		gContext = context;
	}

	public static String getPrefString(String key, final String defaultValue) {
//		Log.e(TAG, "getPrefString: "+key+"  "+defaultValue );
		final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(gContext);
		return settings.getString(key, defaultValue);
	}

	public static void setPrefString(final String key, final String value) {
//		Log.e(TAG, "setPrefString: "+key+"  "+value );
		final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(gContext);
		settings.edit().putString(key, value).commit();
	}

	public static boolean getPrefBoolean(final String key, final boolean defaultValue) {
//		Log.e(TAG, "getPrefBoolean: "+key+"  "+defaultValue );
		final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(gContext);
		return settings.getBoolean(key, defaultValue);
	}

	public static boolean hasKey(final String key) {
		return PreferenceManager.getDefaultSharedPreferences(gContext).contains(key);
	}

	public static void setPrefBoolean(final String key, final boolean value) {
//		Log.e(TAG, "setPrefBoolean: "+key+"  "+value );
		final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(gContext);
		settings.edit().putBoolean(key, value).commit();
	}

	public static void setPrefInt(final String key, final int value) {
//		Log.e(TAG, "setPrefInt: "+key+"  "+value );
		final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(gContext);
		settings.edit().putInt(key, value).commit();
	}

	public static int getPrefInt(final String key, final int defaultValue) {
//		Log.e(TAG, "getPrefInt: "+key+"  "+defaultValue );
		final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(gContext);
		return settings.getInt(key, defaultValue);
	}

	public static void setPrefFloat(final String key, final float value) {
//		Log.e(TAG, "setPrefFloat: "+key+"  "+value );
		final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(gContext);
		settings.edit().putFloat(key, value).commit();
	}

	public static float getPrefFloat(Context context, final String key, final float defaultValue) {
//		Log.e(TAG, "getPrefFloat: "+key+"  "+defaultValue );
		final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
		return settings.getFloat(key, defaultValue);
	}

	public static void setSettingLong(final String key, final long value) {
//		Log.e(TAG, "setSettingLong: "+key+"  "+value );
		final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(gContext);
		settings.edit().putLong(key, value).commit();
	}

	public static long getPrefLong(final String key, final long defaultValue) {
//		Log.e(TAG, "getPrefLong: "+key+"  "+defaultValue );
		final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(gContext);
		return settings.getLong(key, defaultValue);
	}

	public static void clearPreference(final SharedPreferences p) {
		Log.e(TAG, "clearPreference: " );
		final Editor editor = p.edit();
		editor.clear();
		editor.commit();
	}
}
