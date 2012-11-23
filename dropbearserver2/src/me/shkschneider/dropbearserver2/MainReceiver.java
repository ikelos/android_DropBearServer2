package me.shkschneider.dropbearserver2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import me.shkschneider.dropbearserver2.task.Callback;
import me.shkschneider.dropbearserver2.task.Starter;
import me.shkschneider.dropbearserver2.util.L;

public class MainReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(final Context context, Intent intent) {
		L.d(intent.getAction());

		L.d("Starter");
		new Starter(context, new Callback<Boolean>() {

			@Override
			public void onTaskComplete(int id, Boolean result) {
				L.d("Result: " + result);
			}
		}, true).execute();
	}
}
