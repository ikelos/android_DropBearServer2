package me.shkschneider.dropbearserver2;

import android.os.AsyncTask;

import com.stericson.RootTools.RootTools;

public class Starter extends AsyncTask<String, Void, Boolean>{

	private StarterCallback mCallback = null;

	public Starter(StarterCallback callback) {
		mCallback = callback;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}

	@Override
	protected Boolean doInBackground(String... params) {
		RootTools.remount("/system", "RW");
		RootTools.remount("/system", "RO");
		return true;
	}

	@Override
	protected void onPostExecute(Boolean result) {
		super.onPostExecute(result);

		if (mCallback != null) {
			mCallback.onStartedComplete(result);
		}
	}

	public interface StarterCallback {

		public void onStartedComplete(Boolean result);
	}
}
