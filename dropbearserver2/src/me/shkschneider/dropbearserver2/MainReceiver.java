package me.shkschneider.dropbearserver2;

import me.shkschneider.dropbearserver2.util.L;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MainReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		L.d("Received Server Action: " + intent.getAction());
		intent.setClass(context, MainService.class);
		MainIntentService.sendWakefulWork(context, intent);
	}
}
