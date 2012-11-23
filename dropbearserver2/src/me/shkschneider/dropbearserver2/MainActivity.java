package me.shkschneider.dropbearserver2;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Window;

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
