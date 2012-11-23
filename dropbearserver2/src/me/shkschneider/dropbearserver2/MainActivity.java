package me.shkschneider.dropbearserver2;

import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View;

import com.actionbarsherlock.app.SherlockActivity;

public class MainActivity extends SherlockActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		getMenuInflater().inflate(R.menu.main, menu);
		super.onCreateContextMenu(menu, v, menuInfo);
	}
}
