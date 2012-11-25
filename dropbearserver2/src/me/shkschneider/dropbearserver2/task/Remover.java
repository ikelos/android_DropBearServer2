package me.shkschneider.dropbearserver2.task;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import me.shkschneider.dropbearserver2.util.ServerUtils;
import me.shkschneider.dropbearserver2.util.ShellUtils;

public class Remover extends AsyncTask<Void, String, Boolean> {

	private Context mContext = null;
	private ProgressDialog mProgressDialog = null;

	private Callback<Boolean> mCallback;

	public Remover(Context context, Callback<Boolean> callback) {
		mContext = context;
		mCallback = callback;

		if (mContext != null) {
			mProgressDialog = new ProgressDialog(mContext);
			mProgressDialog.setTitle("Remover");
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

	@Override
	protected void onPostExecute(Boolean result) {
		if (mProgressDialog != null) {
			mProgressDialog.dismiss();
		}

		if (mCallback != null) {
			mCallback.onTaskComplete(Callback.TASK_REMOVE, result);
		}
	}
}