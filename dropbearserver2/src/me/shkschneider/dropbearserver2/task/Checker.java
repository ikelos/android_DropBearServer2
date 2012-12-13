package me.shkschneider.dropbearserver2.task;

import android.content.Context;

import me.shkschneider.dropbearserver2.util.RootUtils;
import me.shkschneider.dropbearserver2.util.ServerUtils;

public class Checker extends Task {

	public Checker(Context context, Callback<Boolean> callback) {
		super(Callback.TASK_CHECK, context, callback, true);
	}

	@Override
	protected Boolean doInBackground(Void... params) {
		RootUtils.checkRootAccess();
		RootUtils.checkBusybox();
		RootUtils.checkDropbear(mContext);

		ServerUtils.isDropbearRunning();
		if (RootUtils.hasDropbear == true) {
			ServerUtils.getDropbearVersion(mContext);
		}
		if (ServerUtils.dropbearRunning == true) {
			ServerUtils.getIpAddresses(mContext);
		}

		return (RootUtils.hasRootAccess && RootUtils.hasBusybox && RootUtils.hasDropbear && ServerUtils.dropbearRunning);
	}
}