package me.shkschneider.dropbearserver2.task;

import android.content.Context;

import me.shkschneider.dropbearserver2.util.ServerUtils;
import me.shkschneider.dropbearserver2.util.ShellUtils;

public class Remover extends Task {

	public Remover(Context context, Callback<Boolean> callback) {
		super(context, callback, false);

		if (mProgressDialog != null) {
			mProgressDialog.setTitle("Remover");
		}
	}

	@Override
	protected Boolean doInBackground(Void... params) {
		int step = 0;
		int steps = 9;

		String dropbear = ServerUtils.getLocalDir(mContext) + "/dropbear";
		String dropbearkey = ServerUtils.getLocalDir(mContext) + "/dropbearkey";
		String ssh = "/system/xbin/ssh";
		String scp = "/system/xbin/scp";
		String dbclient = "/system/xbin/dbclient";
		String banner = ServerUtils.getLocalDir(mContext) + "/banner";
		String host_rsa = ServerUtils.getLocalDir(mContext) + "/host_rsa";
		String host_dss = ServerUtils.getLocalDir(mContext) + "/host_dss";
		String authorized_keys = ServerUtils.getLocalDir(mContext) + "/authorized_keys";

		// dropbear
		publishProgress("" + step++, "" + steps, "Dropbear binary");
		ShellUtils.rm(dropbear);

		// dropbearkey
		publishProgress("" + step++, "" + steps, "Dropbearkey binary");
		ShellUtils.rm(dropbearkey);

		// ssh
		publishProgress("" + step++, "" + steps, "SSH binary");
		ShellUtils.rm(ssh);

		// scp
		publishProgress("" + step++, "" + steps, "SCP binary");
		ShellUtils.rm(scp);

		// dbclient
		publishProgress("" + step++, "" + steps, "DBClient binary");
		ShellUtils.rm(dbclient);

		// banner
		publishProgress("" + step++, "" + steps, "Banner");
		ShellUtils.rm(banner);

		// authorized_keys
		publishProgress("" + step++, "" + steps, "Authorized keys");
		ShellUtils.rm(authorized_keys);

		// host_rsa
		publishProgress("" + step++, "" + steps, "Host RSA key");
		ShellUtils.rm(host_rsa);

		// host_dss
		publishProgress("" + step++, "" + steps, "Host DSS key");
		ShellUtils.rm(host_dss);

		return true;
	}
}