package me.shkschneider.dropbearserver2.task;

import java.io.File;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import me.shkschneider.dropbearserver2.R;
import me.shkschneider.dropbearserver2.util.L;
import me.shkschneider.dropbearserver2.util.ServerUtils;
import me.shkschneider.dropbearserver2.util.ShellUtils;
import me.shkschneider.dropbearserver2.util.Utils;

public class Installer extends AsyncTask<Void, String, Boolean> {

	private Context mContext = null;
	private ProgressDialog mProgressDialog = null;

	private Callback<Boolean> mCallback;

	public Installer(Context context, Callback<Boolean> callback) {
		mContext = context;
		mCallback = callback;

		if (mContext != null) {
			mProgressDialog = new ProgressDialog(mContext);
			mProgressDialog.setTitle("Installer");
			mProgressDialog.setMessage("Please wait...");
			mProgressDialog.setCancelable(false);
			mProgressDialog.setCanceledOnTouchOutside(false);
			mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			mProgressDialog.setMax(100);
			mProgressDialog.setIcon(0);
		}
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		if (mProgressDialog != null) {
			mProgressDialog.show();
		}
	}

	@Override
	protected void onProgressUpdate(String... progress) {
		super.onProgressUpdate(progress);
		if (mProgressDialog != null) {
			Float f = (Float.parseFloat(progress[0] + ".0") / Float.parseFloat(progress[1] + ".0") * 100);
			mProgressDialog.setProgress(Math.round(f));
			mProgressDialog.setMessage(progress[2]);
		}
	}

	private Boolean falseWithError(String error) {
		L.d(error);
		//Toast.makeText(mContext, "Error: " + error, Toast.LENGTH_LONG).show();
		return false;
	}

	@Override
	protected Boolean doInBackground(Void... params) {
		int step = 0;
		int steps = 38;

		String dropbear = ServerUtils.getLocalDir(mContext) + "/dropbear";
		String dropbearkey = ServerUtils.getLocalDir(mContext) + "/dropbearkey";
		String tmp = ServerUtils.getLocalDir(mContext) + "/tmp";
		String ssh = "/system/xbin/ssh";
		String scp = "/system/xbin/scp";
		String dbclient = "/system/xbin/dbclient";
		String banner = ServerUtils.getLocalDir(mContext) + "/banner";
		String host_rsa = ServerUtils.getLocalDir(mContext) + "/host_rsa";
		String host_dss = ServerUtils.getLocalDir(mContext) + "/host_dss";
		String authorized_keys = ServerUtils.getLocalDir(mContext) + "/authorized_keys";
		String lock = ServerUtils.getLocalDir(mContext) + "/lock";

		// dropbear
		publishProgress("" + step++, "" + steps, "Dropbear binary");
		if (new File(dropbear).exists() == true && ShellUtils.rm(dropbear) == false) {
			return falseWithError(dropbear);
		}
		publishProgress("" + step++, "" + steps, "Dropbear binary");
		if (Utils.copyRawFile(mContext, R.raw.dropbear, dropbear) == false) {
			return falseWithError(dropbear);
		}
		publishProgress("" + step++, "" + steps, "Dropbear binary");
		if (ShellUtils.chmod(dropbear, "755") == false) {
			return falseWithError(dropbear);
		}

		// dropbearkey
		publishProgress("" + step++, "" + steps, "Dropbearkey binary");
		if (new File(dropbearkey).exists() == true && ShellUtils.rm(dropbearkey) == false) {
			return falseWithError(dropbearkey);
		}
		publishProgress("" + step++, "" + steps, "Dropbearkey binary");
		if (Utils.copyRawFile(mContext, R.raw.dropbearkey, dropbearkey) == false) {
			return falseWithError(dropbearkey);
		}
		publishProgress("" + step++, "" + steps, "Dropbearkey binary");
		if (ShellUtils.chmod(dropbearkey, "755") == false) {
			return falseWithError(dropbearkey);
		}

		// Read-Write
		publishProgress("" + step++, "" + steps, "Remount Read-Write");
		if (Utils.remountReadWrite("/system") == false) {
			return falseWithError("/system RW");
		}

		// ssh
		publishProgress("" + step++, "" + steps, "SSH binary");
		if (Utils.copyRawFile(mContext, R.raw.ssh, tmp) == false) {
			return falseWithError(tmp);
		}
		publishProgress("" + step++, "" + steps, "SSH binary");
		if (ShellUtils.rm(ssh) == false) {
			// Ignore
		}
		publishProgress("" + step++, "" + steps, "SSH binary");
		if (ShellUtils.cp(tmp, ssh) == false) {
			return falseWithError(ssh);
		}
		publishProgress("" + step++, "" + steps, "SSH binary");
		if (ShellUtils.rm(tmp) == false) {
			return falseWithError(tmp);
		}
		publishProgress("" + step++, "" + steps, "SSH binary");
		if (ShellUtils.chmod(ssh, "755") == false) {
			return falseWithError(ssh);
		}

		// scp
		publishProgress("" + step++, "" + steps, "SCP binary");
		if (Utils.copyRawFile(mContext, R.raw.scp, tmp) == false) {
			return falseWithError(tmp);
		}
		publishProgress("" + step++, "" + steps, "SCP binary");
		if (ShellUtils.rm(scp) == false) {
			// Ignore
		}
		publishProgress("" + step++, "" + steps, "SCP binary");
		if (ShellUtils.cp(tmp, scp) == false) {
			return falseWithError(scp);
		}
		publishProgress("" + step++, "" + steps, "SSH binary");
		if (ShellUtils.rm(tmp) == false) {
			return falseWithError(tmp);
		}
		publishProgress("" + step++, "" + steps, "SCP binary");
		if (ShellUtils.chmod(scp, "755") == false) {
			return falseWithError(scp);
		}

		// dbclient
		publishProgress("" + step++, "" + steps, "DBClient binary");
		if (Utils.copyRawFile(mContext, R.raw.dbclient, tmp) == false) {
			return falseWithError(tmp);
		}
		publishProgress("" + step++, "" + steps, "DBClient binary");
		if (ShellUtils.rm(dbclient) == false) {
			// Ignore
		}
		publishProgress("" + step++, "" + steps, "DBClient binary");
		if (ShellUtils.cp(tmp, dbclient) == false) {
			return falseWithError(dbclient);
		}
		publishProgress("" + step++, "" + steps, "DBClient binary");
		if (ShellUtils.rm(tmp) == false) {
			return falseWithError(tmp);
		}
		publishProgress("" + step++, "" + steps, "DBClient binary");
		if (ShellUtils.chmod(dbclient, "755") == false) {
			return falseWithError(dbclient);
		}

		// Read-Only
		publishProgress("" + step++, "" + steps, "Remount Read-Only");
		if (Utils.remountReadOnly("/system") == false) {
			return falseWithError("/system RO");
		}

		// banner
		publishProgress("" + step++, "" + steps, "Banner");
		if (new File(banner).exists() == true && ShellUtils.rm(banner) == false) {
			return falseWithError(banner);
		}
		publishProgress("" + step++, "" + steps, "Banner");
		if (Utils.copyRawFile(mContext, R.raw.banner, banner) == false) {
			return falseWithError(banner);
		}
		publishProgress("" + step++, "" + steps, "Banner");
		if (ShellUtils.chmod(banner, "644") == false) {
			return falseWithError(banner);
		}

		// authorized_keys
		publishProgress("" + step++, "" + steps, "Authorized keys");
		if (new File(authorized_keys).exists() == true && ShellUtils.rm(authorized_keys) == false) {
			return falseWithError(authorized_keys);
		}
		publishProgress("" + step++, "" + steps, "Authorized keys");
		if (ServerUtils.createIfNeeded(authorized_keys) == false) {
			return falseWithError(authorized_keys);
		}
		publishProgress("" + step++, "" + steps, "Authorized keys");
		if (ShellUtils.chmod(authorized_keys, "644") == false) {
			return falseWithError(authorized_keys);
		}

		// host_rsa
		publishProgress("" + step++, "" + steps, "Host RSA key");
		if (new File(host_rsa).exists() == true && ShellUtils.rm(host_rsa) == false) {
			return falseWithError(host_rsa);
		}
		publishProgress("" + step++, "" + steps, "Host RSA key");
		if (ServerUtils.generateRsaPrivateKey(host_rsa) == false) {
			return falseWithError(host_rsa);
		}
		publishProgress("" + step++, "" + steps, "Host RSA key");
		if (ShellUtils.chown(host_rsa, "0:0") == false) {
			return falseWithError(host_rsa);
		}

		// host_dss
		publishProgress("" + step++, "" + steps, "Host DSS key");
		if (new File(host_dss).exists() == true && ShellUtils.rm(host_dss) == false) {
			return falseWithError(host_dss);
		}
		publishProgress("" + step++, "" + steps, "Host DSS key");
		if (ServerUtils.generateDssPrivateKey(host_dss) == false) {
			return falseWithError(host_dss);
		}
		publishProgress("" + step++, "" + steps, "Host DSS key");
		if (ShellUtils.chown(host_dss, "0:0") == false) {
			return falseWithError(host_dss);
		}

		// lock
		publishProgress("" + step++, "" + steps, "Lock file");
		if (new File(lock).exists() == true && ShellUtils.rm(lock) == false) {
			return falseWithError(lock);
		}
		publishProgress("" + step++, "" + steps, "Lock file");
		if (ShellUtils.echoToFile("0", lock) == false) {
			return falseWithError(lock);
		}
		publishProgress("" + step++, "" + steps, "Lock file");
		if (ShellUtils.chmod(lock, "644") == false) {
			return falseWithError(lock);
		}

		// /data/local
		publishProgress("" + step++, "" + steps, "Permissions");
		if (ShellUtils.chmod("/data/local", "755") == false) {
			return falseWithError("/data/local");
		}

		return true;
	}

	@Override
	protected void onPostExecute(Boolean result) {
		if (mProgressDialog != null) {
			mProgressDialog.dismiss();
		}

		if (mCallback != null) {
			mCallback.onTaskComplete(Callback.TASK_INSTALL, result);
		}
	}
}