/*
 * Michael Almyros <http://www.dreamincode.net/forums/topic/190013-creating-simple-file-chooser/>
 * Max Aller <http://blog.maxaller.name/2010/05/attaching-a-sticky-headerfooter-to-an-android-listview/>
 */
package me.shkschneider.dropbearserver2.explorer;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockListActivity;

import me.shkschneider.dropbearserver2.R;
import me.shkschneider.dropbearserver2.util.L;
import me.shkschneider.dropbearserver2.util.ServerUtils;

public class ExplorerActivity extends SherlockListActivity implements DialogInterface.OnClickListener {

	private TextView mCurrentPath;
	private String mPublicKey;

	private File mDir;
	private File mSdcard;
	private ExplorerAdapter mExplorerAdapter;
	Stack<File> mDirStack = new Stack<File>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.explorer);

		mCurrentPath = (TextView) findViewById(R.id.current_path);
		mCurrentPath.setText("SDCard: /");

		// Explorer
		mSdcard = Environment.getExternalStorageDirectory();
		mDir = new File(mSdcard.toString());
		fill(mDir);
	}

	private void fill(File path)
	{
		File[] content = path.listFiles();
		List<ExplorerItem> dirs = new ArrayList<ExplorerItem>();
		List<ExplorerItem> files = new ArrayList<ExplorerItem>();

		mCurrentPath.setText("SDCard: " + path.toString().replaceFirst("^" + mSdcard.toString() + "/?", "/"));

		try {
			for (File file : content) {
				if (file.getName().startsWith(".") == false) {
					if (file.isDirectory() == true) {
						dirs.add(new ExplorerItem(file.getName(), file.getAbsolutePath(), true));
					} else {
						files.add(new ExplorerItem(file.getName(), file.getAbsolutePath(), false));
					}
				}
			}
		}
		catch (Exception e) {
			L.e(e.getMessage());
		}

		// After this, files should be after directories
		Collections.sort(dirs);
		Collections.sort(files);
		dirs.addAll(files);

		if (mCurrentPath.getText().equals("SDCard: /") == false) {
			dirs.add(0, new ExplorerItem("..", path.getParent(), true));
		}

		mExplorerAdapter = new ExplorerAdapter(ExplorerActivity.this, R.layout.explorer_item, dirs);
		setListAdapter(mExplorerAdapter);
	}

	@Override
	protected void onListItemClick(ListView listView, View view, int position, long id) {
		super.onListItemClick(listView, view, position, id);

		ExplorerItem item = mExplorerAdapter.getItem(position);
		File f = new File(item.getPath());
		if (f.isDirectory() == true) {
			mDirStack.push(mDir);
			mDir = f;
			fill(mDir);
		}
		else if (item.getName().equals("..")) {
			mDir = mDirStack.pop();
			fill(mDir);
		}
		else {
			onFileClick(item);
		}
	}

	private void onFileClick(ExplorerItem item) {
		mPublicKey = null;
		try {
			FileInputStream fis = new FileInputStream(item.getPath());
			DataInputStream dis = new DataInputStream(fis);
			BufferedReader br = new BufferedReader(new InputStreamReader(dis));
			String line = br.readLine();
			dis.close();
			// validates the PublicKey
			if (line != null && line.startsWith("ssh-") == true) {
				mPublicKey = line;
			}
		}
		catch (Exception e) {
			L.e(e.getMessage());
		}
		if (mPublicKey == null) {
			Toast.makeText(this, "Error: Invalid public key", Toast.LENGTH_SHORT).show();
		}
		else {
			new AlertDialog.Builder(this)
			.setTitle("Confirm")
			.setMessage(mPublicKey)
			.setCancelable(true)
			.setPositiveButton("Okay", this)
			.setNegativeButton("Cancel", this)
			.show();
		}
	}

	@Override
	public void onBackPressed() {
		if (mDirStack.size() > 0) {
			mDir = mDirStack.pop();
			fill(mDir);
		}
		else {
			finish();
		}
	}

	@Override
	public void onClick(DialogInterface dialog, int button) {
		List<String> publicKeysList = ServerUtils.getPublicKeys(ServerUtils.getLocalDir(this) + "/authorized_keys");
		if (button == DialogInterface.BUTTON_POSITIVE) {
			if (publicKeysList.contains(mPublicKey) == false) {
				ServerUtils.addPublicKey(mPublicKey, ServerUtils.getLocalDir(this) + "/authorized_keys");
				Toast.makeText(this, "Public key successfully added", Toast.LENGTH_SHORT).show();
				finish();
			}
			else {
				Toast.makeText(this, "Public key already registered", Toast.LENGTH_SHORT).show();
			}
		}
	}

}

