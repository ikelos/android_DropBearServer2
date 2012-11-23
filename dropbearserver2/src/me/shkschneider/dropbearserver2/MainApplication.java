package me.shkschneider.dropbearserver2;

import android.app.Application;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;

public class MainApplication extends Application {

	public static Boolean DEBUG = false;
	public static String ANDROID_VERSION = null;
	public static Integer ANDROID_API = 0;
	public static String APP_PACKAGE = null;
	public static String APP_NAME = null;
	public static String APP_VERSION = null;

	@Override
	public void onCreate() {
		super.onCreate();

		ANDROID_VERSION = Build.VERSION.RELEASE;
		ANDROID_API = Build.VERSION.SDK_INT;

		APP_PACKAGE = getPackageName();
		APP_NAME = getResources().getString(R.string.app_name);
		APP_VERSION = "0";
		try {
			PackageInfo packageInfo = getPackageManager().getPackageInfo(APP_PACKAGE, PackageManager.GET_META_DATA);
			APP_VERSION = packageInfo.versionName.toString();
		}
		catch (NameNotFoundException e) {
			L.e("NameNotFoundException: " + e.getMessage());
		}

		L.i(toString());
	}

	@Override
	public void onLowMemory() {
		Runtime runtime = Runtime.getRuntime();
		L.w("HeapSize: " + runtime.freeMemory() + "B free out of " + runtime.maxMemory() + "B");

		super.onLowMemory();
	}

	// Info

	@Override
	public String toString() {
		return APP_NAME + " v" + APP_VERSION + " (" + APP_PACKAGE + ")" + " Android " + ANDROID_VERSION + " (API-" + ANDROID_API + ")";
	}
}
