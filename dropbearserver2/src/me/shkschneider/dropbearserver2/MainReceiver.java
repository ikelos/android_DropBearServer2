package me.shkschneider.dropbearserver2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import me.shkschneider.dropbearserver2.task.Callback;
import me.shkschneider.dropbearserver2.task.Starter;
import me.shkschneider.dropbearserver2.util.L;
import me.shkschneider.dropbearserver2.util.RootUtils;
import me.shkschneider.dropbearserver2.util.ServerUtils;
import me.shkschneider.dropbearserver2.util.ShellUtils;

public class MainReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(final Context context, Intent intent) {
		L.d(intent.getAction());

		ShellUtils.echoToFile("0", ServerUtils.getLocalDir(context) + "/lock");

		L.d("Starting server");
		Toast.makeText(context, "DropBearServer: Starting server", Toast.LENGTH_LONG).show();

		if (RootUtils.checkRootAccess() == false) {
			L.e("checkRootAccess() = false");
		}
		else if (RootUtils.checkBusybox() == false) {
			L.e("checkBusybox() = false");
		}
		else if (RootUtils.checkDropbear(context) == false) {
			L.e("checkDropbear() = false");
		}
		else {
			new Starter(context, new Callback<Boolean>() {

				@Override
				public void onTaskComplete(int id, Boolean result) {
					L.d("Result: " + false);
					if (result == true) {
						Toast.makeText(context, "DropBearServer: Server started", Toast.LENGTH_LONG).show();
					}
					else {
						Toast.makeText(context, "DropBearServer: Server failure", Toast.LENGTH_LONG).show();
					}
				}
			}, true).execute();
		}
	}
}
