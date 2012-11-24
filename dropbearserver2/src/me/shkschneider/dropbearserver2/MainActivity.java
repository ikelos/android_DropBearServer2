package me.shkschneider.dropbearserver2;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;

import me.shkschneider.dropbearserver2.task.Callback;
import me.shkschneider.dropbearserver2.task.Checker;
import me.shkschneider.dropbearserver2.task.Installer;
import me.shkschneider.dropbearserver2.task.Remover;
import me.shkschneider.dropbearserver2.task.Starter;
import me.shkschneider.dropbearserver2.task.Stopper;
import me.shkschneider.dropbearserver2.util.L;
import me.shkschneider.dropbearserver2.util.RootUtils;
import me.shkschneider.dropbearserver2.util.ServerUtils;

public class MainActivity extends SherlockActivity implements View.OnClickListener, Callback<Boolean> {

	private static final int STATUS_UNKNOWN = 0;
	private static final int STATUS_NOT_READY = 1;
	private static final int STATUS_NOT_INSTALLED = 2;
	private static final int STATUS_STARTED = 3;
	private static final int STATUS_STOPPED = 4;
	private int mStatus = STATUS_UNKNOWN;

	private Button mInstall = null;
	private Button mStart = null;
	private Button mStop = null;
	private Button mRemove = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setProgressBarIndeterminate(true);
		setProgressBarIndeterminateVisibility(false);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setContentView(R.layout.main);

		((TextView) findViewById(R.id.name)).setText(MainApplication.APP_NAME);
		((TextView) findViewById(R.id.version)).setText("v" + MainApplication.APP_VERSION + "/" + ServerUtils.getDropbearVersion(this));

		mInstall = (Button) findViewById(R.id.install);
		mInstall.setOnClickListener(this);

		mStart = (Button) findViewById(R.id.start);
		mStart.setOnClickListener(this);

		mStop = (Button) findViewById(R.id.stop);
		mStop.setOnClickListener(this);

		mRemove = (Button) findViewById(R.id.remove);
		mRemove.setOnClickListener(this);

		stdout("Application started");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true ;
		case R.id.about:
			about();
			return true;
		}
		return super.onMenuItemSelected(featureId, item);
	}

	@Override
	protected void onResume() {
		stdout("Application resumed");

		check();

		super.onResume();
	}

	@Override
	protected void onStart() {
		((ScrollView) findViewById(R.id.scrollView)).post(new Runnable() {

			@Override
			public void run() {
				((ScrollView) findViewById(R.id.scrollView)).fullScroll(ScrollView.FOCUS_DOWN);
			}
		});

		super.onStart();
	}

	@Override
	public void onClick(View view) {
		setProgressBarIndeterminateVisibility(true);

		if (view == mInstall) {
			stdout("Installer started");
			new Installer(this, this).execute();
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
		}
		else {
			stdout("Operation failed");
		}
	}

	private void check() {
		setProgressBarIndeterminateVisibility(true);
		new Checker(this, new Callback<Boolean>() {

			@Override
			public void onTaskComplete(int id, Boolean result) {
				status();
				setProgressBarIndeterminateVisibility(false);
			}
		}).execute();
	}

	private void status() {
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
			for (String ipAddress : ServerUtils.getIpAddresses(this)) {
				stdout("IP: " + ipAddress);
			}
		}
		else {
			mStatus = STATUS_STOPPED;
			stdout("Server stopped");
		}

		mInstall.setEnabled(mStatus == STATUS_NOT_INSTALLED);
		mStart.setEnabled(mStatus == STATUS_STOPPED);
		mStop.setEnabled(mStatus == STATUS_STARTED);
		mRemove.setEnabled(mStatus == STATUS_STOPPED);
	}

	private void stdout(String string) {
		TextView textView = new TextView(this);
		textView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
		textView.setText("[" + new SimpleDateFormat("hh:mm:ss", Locale.getDefault()).format(new Date()) + "]\t" + string);
		((LinearLayout) findViewById(R.id.linearLayout)).addView(textView);

		final ScrollView scrollView = (ScrollView) findViewById(R.id.scrollView);
		scrollView.post(new Runnable() {

			@Override
			public void run() {
				scrollView.fullScroll(View.FOCUS_DOWN);
			}
		});
	}

	private void about() {
		WebView webView = new WebView(this);
		webView.getSettings().setDefaultTextEncodingName("utf-8");
		webView.loadUrl("file:///android_asset/about.html");
		webView.setWebViewClient(new WebViewClient() {

			@Override
			public void onReceivedError(WebView webView, int errorCode, String description, String failingUrl) {
				L.w(description + ": " + failingUrl);
				super.onReceivedError(webView, errorCode, description, failingUrl);
			}
		});

		AlertDialog alertDialog = new AlertDialog.Builder(this).create();
		alertDialog.setCancelable(false);
		alertDialog.setCanceledOnTouchOutside(false);
		alertDialog.setIcon(android.R.drawable.ic_dialog_info);
		alertDialog.setTitle("About");
		alertDialog.setMessage(null);
		alertDialog.setView(webView);
		alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		});
		alertDialog.show();
	}
}
