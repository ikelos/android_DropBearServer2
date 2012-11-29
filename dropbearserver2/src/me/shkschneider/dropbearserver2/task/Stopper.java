package me.shkschneider.dropbearserver2.task;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;

import me.shkschneider.dropbearserver2.util.L;
import me.shkschneider.dropbearserver2.util.ServerUtils;
import me.shkschneider.dropbearserver2.util.ShellUtils;

public class Stopper extends AsyncTask<Void, String, Boolean> {

	private Context mContext = null;
	private ProgressDialog mProgressDialog = null;
	private boolean mStartInBackground = false;

	private Callback<Boolean> mCallback = null;

	public Stopper(Context context, Callback<Boolean> callback) {
		this(context, callback, false);
	}

	public Stopper(Context context, Callback<Boolean> callback, boolean startInBackground) {
		mContext = context;
		mCallback = callback;
		mStartInBackground = startInBackground;

		if (mContext != null && !mStartInBackground) {
			mProgressDialog = new ProgressDialog(mContext);
			mProgressDialog.setTitle("Stopper");
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
		if (mProgressDialog != null && !mStartInBackground) {
			mProgressDialog.show();
		}
	}

	private Boolean falseWithError(String error) {
		L.d(error);
		return false;
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

	@Override
	protected void onPostExecute(Boolean result) {
		dismiss();

		if (mCallback != null) {
			mCallback.onTaskComplete(Callback.TASK_STOP, result);
		}
	}

	@Override
	protected void onCancelled() {
		dismiss();

		super.onCancelled();
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	protected void onCancelled(Boolean result) {
		dismiss();

		super.onCancelled(result);
	}

	private void dismiss() {
		try {
			if (mProgressDialog != null) {
				mProgressDialog.dismiss();
				mProgressDialog = null;
			}
		}
		catch (IllegalArgumentException e) {
			L.w("IllegalArgumentException: " + e.getMessage());
		}
	}
}