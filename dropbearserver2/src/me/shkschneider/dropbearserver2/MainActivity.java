package me.shkschneider.dropbearserver2;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Window;

import me.shkschneider.dropbearserver2.task.Callback;
import me.shkschneider.dropbearserver2.task.Checker;
import me.shkschneider.dropbearserver2.task.Installer;
import me.shkschneider.dropbearserver2.task.Remover;
import me.shkschneider.dropbearserver2.task.Starter;
import me.shkschneider.dropbearserver2.task.Stopper;
import me.shkschneider.dropbearserver2.util.RootUtils;
import me.shkschneider.dropbearserver2.util.ServerUtils;

public class MainActivity extends SherlockActivity implements OnClickListener, Callback<Boolean> {

	private static final int STATUS_UNKNOWN = 0;
	private static final int STATUS_NOT_INSTALLED = 1;
	private static final int STATUS_NOT_READY = 2;
	private static final int STATUS_STARTED = 3;
	private static final int STATUS_STOPPED = 4;
	private int mStatus = STATUS_UNKNOWN;

	private TextView mLabel = null;
	private Button mInstall = null;
	private Button mCheck = null;
	private Button mStart = null;
	private Button mStop = null;
	private Button mRemove = null;
	private TextView mTextView = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setProgressBarIndeterminate(true);
		setProgressBarIndeterminateVisibility(false);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setContentView(R.layout.main);

		mLabel = (TextView) findViewById(R.id.label);
		mLabel.setText(MainApplication.APP_NAME + "\n" + "v" + MainApplication.APP_VERSION);

		mInstall = (Button) findViewById(R.id.install);
		mInstall.setOnClickListener(this);

		mCheck = (Button) findViewById(R.id.check);
		mCheck.setOnClickListener(this);

		mStart = (Button) findViewById(R.id.start);
		mStart.setOnClickListener(this);

		mStop = (Button) findViewById(R.id.stop);
		mStop.setOnClickListener(this);

		mRemove = (Button) findViewById(R.id.remove);
		mRemove.setOnClickListener(this);

		mTextView = (TextView) findViewById(R.id.textView);
		mTextView.setMovementMethod(new ScrollingMovementMethod());
		mTextView.post(new Runnable() {

			@Override
			public void run() {
				if (mTextView.getLayout() != null) {
					final int scrollAmount = mTextView.getLayout().getLineTop(mTextView.getLineCount()) - mTextView.getHeight();
					if (scrollAmount > 0) {
						mTextView.scrollTo(0, scrollAmount);
					}
					else {
						mTextView.scrollTo(0, 0);
					}
				}
			}
		});

		stdout("Application started");

		check();
	}

	@Override
	protected void onResume() {
		stdout("Application resumed");

		status();

		super.onResume();
	}

	@Override
	public void onClick(View view) {
		setProgressBarIndeterminateVisibility(true);

		if (view == mInstall) {
			stdout("Installer started");
			new Installer(this, this).execute();
		}
		else if (view == mCheck) {
			stdout("Checker started");
			new Checker(this, this).execute();
		}
		else if (view == mStart) {
			stdout("Starter started");
			new Starter(this, this).execute();
		}
		else if (view == mStop) {
			stdout("Stopper started");
			new Stopper(this, this).execute();
		}
		else if (view == mRemove) {
			stdout("Remover started");
			new Remover(this, this).execute();
		}
	}

	@Override
	public void onTaskComplete(int id, Boolean result) {
		setProgressBarIndeterminateVisibility(false);
		if (result == true) {
			stdout("Operation succeeded");
			check();
			status();
		}
		else {
			stdout("Operation failed");
		}
	}

	private void check() {
		setProgressBarIndeterminateVisibility(true);
		stdout("Checking root access");
		if (RootUtils.checkRootAccess() == true) {
			stdout("Checking busybox");
			if (RootUtils.checkBusybox() == true) {
				stdout("Checking dropbear");
				RootUtils.checkDropbear(this);
			}
		}
		setProgressBarIndeterminateVisibility(false);
	}

	private void status() {
		setProgressBarIndeterminateVisibility(true);

		if (RootUtils.hasDropbear == false) {
			mStatus = STATUS_NOT_INSTALLED;
			stdout("Server not installed");
		}
		else if (RootUtils.hasRootAccess == false) {
			mStatus = STATUS_NOT_READY;
			stdout("Server not ready (root access denied)");
		}
		else if (RootUtils.hasBusybox == false) {
			mStatus = STATUS_NOT_READY;
			stdout("Server not ready (busybox missing)");
		}
		else if (ServerUtils.isDropbearRunning() == true) {
			mStatus = STATUS_STARTED;
			stdout("Server started");
			for (String ipAddress : ServerUtils.getIpAddresses()) {
				stdout("IP: " + ipAddress);
			}
		}
		else {
			mStatus = STATUS_STOPPED;
			stdout("Server stopped");
		}

		mInstall.setEnabled(mStatus == STATUS_NOT_INSTALLED);
		mCheck.setEnabled(mStatus == STATUS_NOT_READY);
		mStart.setEnabled(mStatus == STATUS_STOPPED);
		mStop.setEnabled(mStatus == STATUS_STARTED);
		mRemove.setEnabled(mStatus == STATUS_STOPPED);

		setProgressBarIndeterminateVisibility(false);
	}

	private void stdout(String string) {
		Date date = new Date();
		mTextView.append("[" + new SimpleDateFormat("hh:mm:ss").format(date) + "]\t" + string + "\n");
	}
}
