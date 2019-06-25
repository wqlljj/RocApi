package com.cloudminds.rocapi.util;

import android.content.Context;
import android.view.WindowManager;

public class DeviceUtil {

	// 新设备宽1280
	public static boolean isNewDeviceOf1280Width(Context context) {
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		int width = wm.getDefaultDisplay().getWidth();
		return width  == 1280;
	}
}
