package me.shkschneider.dropbearserver2.task;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import me.shkschneider.dropbearserver2.util.L;
import me.shkschneider.dropbearserver2.util.ServerUtils;
import me.shkschneider.dropbearserver2.util.ShellUtils;

public class Stopper extends AsyncTask<Void, String, Boolean> {

	private Context mContext = null;
	private ProgressDialog mProgressDialog = null;
	private boolean mStartInBackground = false;

	private Callback<Boolean> mCallback;

	public Stopper(Context context, Callback<Boolean> callback) {
		this(context, callback, false);
	}

	public Stopper(Context context, Callback<Boolean> callback, boolean startInBackground) {
		mContext = context;
		mCallback = callback;
		mStartInBackground = startInBackground;

		if (mContext != null && !mStartInBackground) {
			mProgressDialog = new ProgressDialog(mContext);
			mProgressDialog.setTitle("Stopping server");
			mProgressDialog.setMessage("Please wait...");
			mProgressDialog.setCancelable(false);
			mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			mProgressDialog.setMax(100);
			mProgressDialog.setIcon(0);
		}
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		if (mProgressDialog != null && !mStartInBackground) {
			mProgressDialog.show();
		}
	}

	private Boolean falseWithError(String error) {
		L.d(error);
		//Toast.makeText(mContext, "Error: " + error, Toast.LENGTH_LONG).show();
		return false;
	}

	@Override
	protected Boolean doInBackground(Void... params) {
		String pidFile = ServerUtils.getLocalDir(mContext) + "/pid";
		ShellUtils.rm(pidFile);

		String lockFile = ServerUtils.getLocalDir(mContext) + "/lock";
		if (ShellUtils.echoToFile("0", lockFile) == false) {
			return falseWithError("echoToFile(0, " + lockFile + ")");
		}

		L.i("Killing processes");
		if (ShellUtils.killall("dropbear") == false) {
			return falseWithError("killall(dropbear)");
		}

		return true;
	}

	@Override
	protected void onPostExecute(Boolean result) {
		if (mProgressDialog != null && !mStartInBackground) {
			mProgressDialog.dismiss();
		}

		if (mCallback != null) {
			mCallback.onTaskComplete(Callback.TASK_STOP, result);
		}
	}
}