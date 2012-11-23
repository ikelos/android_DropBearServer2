package me.shkschneider.dropbearserver2;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Window;
import com.stericson.RootTools.RootTools;

import me.shkschneider.dropbearserver2.MyDialog.MyDialogCallback;

public class MainActivity extends SherlockActivity implements OnClickListener {

	private Button mStart = null;
	private Button mStop = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setProgressBarIndeterminate(true);
		setProgressBarIndeterminateVisibility(false);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setContentView(R.layout.main);

		mStart = (Button) findViewById(R.id.start);
		mStart.setOnClickListener(this);

		mStop = (Button) findViewById(R.id.stop);
		mStop.setOnClickListener(this);

		((TextView) findViewById(R.id.status)).setText("OK");
	}

	@Override
	protected void onResume() {
		setProgressBarIndeterminateVisibility(true);

		if (RootTools.isRootAvailable() == false) {
			new MyDialog(this, MyDialog.CHOICE_NEUTRAL).setTitle("Alert").setMessage("Root is not available").setCallback(new MyDialogCallback() {

				@Override
				public void onMyDialogCallback(int choice) {
					finish();
				}
			}).show();
		}
		if (RootTools.isAccessGiven() == false) {
			new MyDialog(this, MyDialog.CHOICE_NEUTRAL).setTitle("Alert").setMessage("Root access denied").setCallback(new MyDialogCallback() {

				@Override
				public void onMyDialogCallback(int choice) {
					finish();
				}
			}).show();
		}
		if (RootTools.isBusyboxAvailable() == false) {
			new MyDialog(this, MyDialog.CHOICE_NEUTRAL).setTitle("Alert").setMessage("Busybox is not available").setCallback(new MyDialogCallback() {

				@Override
				public void onMyDialogCallback(int choice) {
					finish();
				}
			}).show();
		}
		setProgressBarIndeterminateVisibility(false);

		super.onResume();
	}

	@Override
	public void onClick(View view) {
		if (view == mStart) {
			// ...
		}
		else if (view == mStop) {
			// ...
		}
	}
}
