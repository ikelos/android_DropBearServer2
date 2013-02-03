package me.shkschneider.dropbearserver2.task;

import java.io.File;

import android.content.Context;

import me.shkschneider.dropbearserver2.R;
import me.shkschneider.dropbearserver2.util.ServerUtils;
import me.shkschneider.dropbearserver2.util.ShellUtils;
import me.shkschneider.dropbearserver2.util.Utils;

public class Installer extends Task {

	public Installer(Context context, Callback<Boolean> callback) {
		super(Callback.TASK_INSTALL, context, callback, false);

		if (mProgressDialog != null) {
			mProgressDialog.setTitle("Installer");
		}
	}

	private Boolean copyToAppData(int resId, String path) {
		if (new File(path).exists() == true && ShellUtils.rm(path) == false) {
			return falseWithError(path);
		}
		if (Utils.copyRawFile(mContext, resId, path) == false) {
			return falseWithError(path);
		}
		if (ShellUtils.chmod(path, "755") == false) {
			return falseWithError(path);
		}
		return true;
	}

	private Boolean copyToSystemXbin(int resId, String tmp, String path) {
		if (Utils.copyRawFile(mContext, resId, tmp) == false) {
			return falseWithError(tmp);
		}
		if (ShellUtils.rm(path) == false) {
			// Ignore
		}
		if (ShellUtils.cp(tmp, path) == false) {
			return falseWithError(path);
		}
		if (ShellUtils.rm(tmp) == false) {
			// Ignore
		}
		if (ShellUtils.chmod(path, "755") == false) {
			return falseWithError(path);
		}
		return true;
	}

	@Override
	protected Boolean doInBackground(Void... params) {
		String tmp = ServerUtils.getLocalDir(mContext) + "/tmp";

		publishProgress("Dropbear binary");
		copyToAppData(R.raw.dropbear, ServerUtils.getLocalDir(mContext) + "/dropbear");

		publishProgress("Dropbearkey binary");
		copyToAppData(R.raw.dropbearkey, ServerUtils.getLocalDir(mContext) + "/dropbearkey");

		publishProgress("Remount Read-Write");
		if (Utils.remountReadWrite("/system") == false) {
			return falseWithError("/system RW");
		}
		if (Utils.remountReadWrite("/") == false) {
			return falseWithError("/ RW");
		}

		publishProgress("SSH binary");
		copyToSystemXbin(R.raw.ssh, tmp, "/system/xbin/ssh");

		publishProgress("SCP binary");
		copyToSystemXbin(R.raw.scp, tmp, "/system/xbin/scp");

		publishProgress("DBClient binary");
		copyToSystemXbin(R.raw.dbclient, tmp, "/system/xbin/dbclient");

		publishProgress("SFTP binary");
		ShellUtils.mkdir("/usr");
		ShellUtils.mkdir("/usr/libexec");
		copyToSystemXbin(R.raw.sftp_server, tmp, "/usr/libexec/sftp-server");

		publishProgress("Remount Read-Only");
		if (Utils.remountReadOnly("/system") == false) {
			return falseWithError("/system RO");
		}
		if (Utils.remountReadOnly("/") == false) {
			return falseWithError("/ RO");
		}

		publishProgress("Banner");
		copyToAppData(R.raw.banner, ServerUtils.getLocalDir(mContext) + "/banner");

		publishProgress("Authorized keys");
		String authorized_keys = ServerUtils.getLocalDir(mContext) + "/authorized_keys";
		if (new File(authorized_keys).exists() == true && ShellUtils.rm(authorized_keys) == false) {
			return falseWithError(authorized_keys);
		}
		if (ServerUtils.createIfNeeded(authorized_keys) == false) {
			return falseWithError(authorized_keys);
		}

		publishProgress("Host RSA key");
		String host_rsa = ServerUtils.getLocalDir(mContext) + "/host_rsa";
		if (new File(host_rsa).exists() == true && ShellUtils.rm(host_rsa) == false) {
			return falseWithError(host_rsa);
		}
		if (ServerUtils.generateRsaPrivateKey(host_rsa) == false) {
			return falseWithError(host_rsa);
		}

		publishProgress("Host DSS key");
		String host_dss = ServerUtils.getLocalDir(mContext) + "/host_dss";
		if (new File(host_dss).exists() == true && ShellUtils.rm(host_dss) == false) {
			return falseWithError(host_dss);
		}
		if (ServerUtils.generateDssPrivateKey(host_dss) == false) {
			return falseWithError(host_dss);
		}

		publishProgress("Permissions");
		if (ShellUtils.chmod("/data/local", "755") == false) {
			return falseWithError("/data/local");
		}
		if (ShellUtils.chmod(authorized_keys, "644") == false) {
			return falseWithError(authorized_keys);
		}
		if (ShellUtils.chown(host_rsa, "0:0") == false) {
			return falseWithError(host_rsa);
		}
		if (ShellUtils.chown(host_dss, "0:0") == false) {
			return falseWithError(host_dss);
		}

		return true;
	}
}