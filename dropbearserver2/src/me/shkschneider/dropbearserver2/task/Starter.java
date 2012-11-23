/*
 * Pawel Nadolski <http://stackoverflow.com/questions/10319471/android-is-the-groupid-of-sdcard-rw-always-1015/>
 */
package me.shkschneider.dropbearserver2.task;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import me.shkschneider.dropbearserver2.MainService;
import me.shkschneider.dropbearserver2.util.L;
import me.shkschneider.dropbearserver2.util.ServerUtils;
import me.shkschneider.dropbearserver2.util.ShellUtils;

public class Starter extends AsyncTask<Void, String, Boolean> {

	private static final int ID_ROOT = 0;
	private static final int UID_SHELL = 2000;
	private static final int GID_SDCARD_RW = 1015;

	private Context mContext = null;
	private ProgressDialog mProgressDialog = null;
	private boolean mStartInBackground = false;

	private Callback<Boolean> mCallback;

	public Starter(Context context, Callback<Boolean> callback) {
		this(context, callback, false);
	}

	public Starter(Context context, Callback<Boolean> callback, boolean startInBackground) {
		mContext = context;
		mCallback = callback;
		mStartInBackground = startInBackground;
		if (mContext != null && !mStartInBackground) {
			mProgressDialog = new ProgressDialog(mContext);
			mProgressDialog.setTitle("Starting server");
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
		if (ServerUtils.isDropbearRunning() == true) {
			ShellUtils.killall("dropbear");
		}

		String login = "root";
		String passwd = "42";
		String banner = ServerUtils.getLocalDir(mContext) + "/banner";
		String hostRsa = ServerUtils.getLocalDir(mContext) + "/host_rsa";
		String hostDss = ServerUtils.getLocalDir(mContext) + "/host_dss";
		String authorizedKeys = ServerUtils.getLocalDir(mContext) + "/authorized_keys";
		Integer listeningPort = 22;
		String pidFile = ServerUtils.getLocalDir(mContext) + "/pid";

		String command = ServerUtils.getLocalDir(mContext) + "/dropbear";
		command = command.concat(" -A -N " + login);
		command = command.concat(" -C " + passwd);
		command = command.concat(" -r " + hostRsa + " -d " + hostDss);
		command = command.concat(" -R " + authorizedKeys);
		if (login.equals("root")) {
			command = command.concat(" -U " + ID_ROOT + " -G " + ID_ROOT);
		}
		else {
			// Problems writing to SDCard? See <http://www.chainfire.eu/articles/113/Is_Google_blocking_apps_writing_to_SD_cards_/>
			command = command.concat(" -U " + UID_SHELL + " -G " + GID_SDCARD_RW);
		}
		command = command.concat(" -p " + listeningPort);
		command = command.concat(" -P " + pidFile);
		command = command.concat(" -b " + banner);

		if (ShellUtils.execute(command) == false) {
			return falseWithError("execute(" + command + ")");
		}

		return true;
	}

	@Override
	protected void onPostExecute(Boolean result) {
		if (mProgressDialog != null && !mStartInBackground) {
			mProgressDialog.dismiss();
		}
		if (result == true) {
			ShellUtils.echoToFile("0" + android.os.Process.myPid(), ServerUtils.getLocalDir(mContext) + "/lock");
		}
		if (mCallback != null) {
			mCallback.onTaskComplete(result);
		}
		else {
			Intent intent = new Intent(MainService.ACTION_SERVER_STARTED);
			intent.putExtra(MainService.EXTRA_IS_SUCCESS, result);
			mContext.sendBroadcast(intent);
		}
	}
}