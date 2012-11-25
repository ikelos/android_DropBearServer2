package me.shkschneider.dropbearserver2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;

import me.shkschneider.dropbearserver2.task.Callback;
import me.shkschneider.dropbearserver2.task.Starter;
import me.shkschneider.dropbearserver2.util.L;

public class MainReceiver extends BroadcastReceiver {

	private static final int PAUSE = 10;

	@Override
	public void onReceive(final Context context, Intent intent) {
		L.d(intent.getAction());

		if (LocalPreferences.getBoolean(context, LocalPreferences.PREF_START_BOOT, LocalPreferences.PREF_START_BOOT_DEFAULT) == true) {
			L.d("Handler: +" + PAUSE + "s");
			Handler handler = new Handler();
			handler.postDelayed(new Runnable() {

				@Override
				public void run() {
					L.d("Starter");
					new Starter(context, new Callback<Boolean>() {

						@Override
						public void onTaskComplete(int id, Boolean result) {
							L.d("Result: " + result);
						}
					}, true).execute();
				}
			}, PAUSE * 1000);

		}
	}
}
