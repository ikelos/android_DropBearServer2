package me.shkschneider.dropbearserver2.util;

import java.io.File;

import android.content.Context;

import com.stericson.RootTools.RootTools;

public abstract class RootUtils {

	public static Boolean hasRootAccess = false;
	public static Boolean hasBusybox = false;
	public static Boolean hasDropbear = false;

	public static final Boolean checkRootAccess() {
		hasRootAccess = false;
		if (RootTools.isRootAvailable()) {
			if (RootTools.isAccessGiven()) {
				hasRootAccess = true;
			}
			else {
				L.w("access rejected");
			}
		}
		else {
			L.w("su not found");
		}
		return hasRootAccess;
	}

	public static final Boolean checkBusybox() {
		hasBusybox = false;
		if (RootTools.checkUtil("busybox")) {
			hasBusybox = true;
		}
		else {
			L.w("busybox not found");
		}
		return hasBusybox;
	}

	public static final Boolean checkDropbear(Context context) {
		hasDropbear = false;
		File file = null;

		file = new File(ServerUtils.getLocalDir(context) + "/dropbear");
		if (file.exists() == false || file.isFile() == false) {
			L.w("dropbear");
			return false;
		}
		file = new File(ServerUtils.getLocalDir(context) + "/dropbearkey");
		if (file.exists() == false || file.isFile() == false) {
			L.w("dropbearkey");
			return false;
		}
		file = new File(ServerUtils.getLocalDir(context) + "/host_rsa");
		if (file.exists() == false || file.isFile() == false) {
			L.w("host_rsa");
			return false;
		}
		file = new File(ServerUtils.getLocalDir(context) + "/host_dss");
		if (file.exists() == false || file.isFile() == false) {
			L.w("host_dss");
			return false;
		}
		file = new File(ServerUtils.getLocalDir(context) + "/authorized_keys");
		if (file.exists() == false || file.isFile() == false) {
			L.w("authorized_keys");
			return false;
		}
		file = new File(ServerUtils.getLocalDir(context) + "/banner");
		if (file.exists() == false || file.isFile() == false) {
			L.w("banner");
			return false;
		}
		file = new File(ServerUtils.getLocalDir(context) + "/lock");
		if (file.exists() == false || file.isFile() == false) {
			L.w("lock");
			return false;
		}

		hasDropbear = true;

		return hasDropbear;
	}
}
