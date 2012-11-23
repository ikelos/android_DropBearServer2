package me.shkschneider.dropbearserver2;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Window;

import me.shkschneider.dropbearserver2.task.Callback;
import me.shkschneider.dropbearserver2.task.Checker;
import me.shkschneider.dropbearserver2.task.Installer;
import me.shkschneider.dropbearserver2.task.Remover;
import me.shkschneider.dropbearserver2.task.Starter;
import me.shkschneider.dropbearserver2.task.Stopper;

public class MainActivity extends SherlockActivity implements OnClickListener, Callback<Boolean> {

	private Button mInstall = null;
	private Button mCheck = null;
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
	}

	@Override
	public void onClick(View view) {
		setProgressBarIndeterminateVisibility(true);

		if (view == mInstall) {
			new Installer(this, this).execute();
		}
		else if (view == mCheck) {
			new Checker(this, this).execute();
		}
		else if (view == mStart) {
			new Starter(this, this).execute();
		}
		else if (view == mStop) {
			new Stopper(this, this).execute();
		}
		else if (view == mRemove) {
			new Remover(this, this).execute();
		}
	}

	@Override
	public void onTaskComplete(Boolean result) {
		setProgressBarIndeterminateVisibility(false);

		Toast.makeText(this, Boolean.toString(result), Toast.LENGTH_SHORT).show();
	}
}
