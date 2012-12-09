package me.shkschneider.dropbearserver2.task;

import android.content.Context;

import me.shkschneider.dropbearserver2.util.L;
import me.shkschneider.dropbearserver2.util.ServerUtils;
import me.shkschneider.dropbearserver2.util.ShellUtils;

public class Stopper extends Task {

	public Stopper(Context context, Callback<Boolean> callback, boolean startInBackground) {
		super(Callback.TASK_STOP, context, callback, startInBackground);

		if (mProgressDialog != null) {
			mProgressDialog.setTitle("Stopper");
		}
	}

	@Override
	protected Boolean doInBackground(Void... params) {
		String pidFile = ServerUtils.getLocalDir(mContext) + "/pid";
		ShellUtils.rm(pidFile);

		L.i("Killing processes");
		if (ShellUtils.killall("dropbear") == false) {
			return falseWithError("killall(dropbear)");
		}

		return true;
	}
}