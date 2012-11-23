package me.shkschneider.dropbearserver2;

import me.shkschneider.dropbearserver2.task.Starter;
import me.shkschneider.dropbearserver2.task.Stopper;
import me.shkschneider.dropbearserver2.util.L;
import me.shkschneider.dropbearserver2.util.ServerUtils;

import android.content.Context;
import android.content.Intent;

public class MainService extends MainIntentService {

	public static final String ACTION_START_SERVER = "me.shkschneider.dropbearserver2.START_SERVER";
	public static final String ACTION_SERVER_STARTED = "me.shkschneider.dropbearserver2.SERVER_STARTED";
	public static final String ACTION_STOP_SERVER = "me.shkschneider.dropbearserver2.STOP_SERVER";
	public static final String ACTION_SERVER_STOPPED = "me.shkschneider.dropbearserver2.SERVER_STOPPED";
	public static final String ACTION_UPDATE_UI = "me.shkschneider.dropbearserver2.UPDATE_UI";

	public static final String EXTRA_IS_SUCCESS = "is_success";

	public MainService() {
		super(MainService.class.getSimpleName());
	}

	@Override
	protected void doWakefulWork(final Intent intent, final Thread callback) {
		try {
			Context context = getApplicationContext();
			String action = intent.getAction();

			if (ACTION_START_SERVER.equals(action)) {
				startServerInBackground(context);
			}
			else if (ACTION_SERVER_STARTED.equals(action)) {
				handleServerStarted(context, intent);
			}
			if (ACTION_STOP_SERVER.equals(action)) {
				stopServerInBackground(context);
			}
			else if (ACTION_SERVER_STOPPED.equals(action)) {
				handleServerStopped(context, intent);
			}
		}
		catch (Exception e) {
			L.e("Error handling Server Action");
		}
	}

	private void startServerInBackground(Context context) {
		L.d("Processing");
		Starter serverStarter = new Starter(context, null, true);
		serverStarter.execute();
	}

	private void handleServerStarted(Context context, Intent intent) {
		L.d("Processing Service Started broadcast");
		boolean success = intent.getBooleanExtra(EXTRA_IS_SUCCESS, false);
		if (success) {
			String infos = "";
			for (String externalIpAddress : ServerUtils.getIpAddresses()) {
				if (externalIpAddress != null) {
					infos = infos.concat("ssh ");
					infos = infos.concat("root@");
					infos = infos.concat(externalIpAddress);
					infos = infos.concat("\n");
				}
			}
		}

		context.sendBroadcast(new Intent(MainService.ACTION_UPDATE_UI));
	}

	private void stopServerInBackground(Context context) {
		L.d("Processing");
		Stopper serverStopper = new Stopper(context, null, true);
		serverStopper.execute();
	}

	private void handleServerStopped(Context context, Intent intent) {
		L.d("Processing");
		context.sendBroadcast(new Intent(MainService.ACTION_UPDATE_UI));
	}

}
