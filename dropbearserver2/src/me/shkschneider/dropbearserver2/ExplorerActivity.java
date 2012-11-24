/*
 * Michael Almyros <http://www.dreamincode.net/forums/topic/190013-creating-simple-file-chooser/>
 * Max Aller <http://blog.maxaller.name/2010/05/attaching-a-sticky-headerfooter-to-an-android-listview/>
 */
package me.shkschneider.dropbearserver2;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Stack;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockListActivity;
import com.actionbarsherlock.view.MenuItem;

import me.shkschneider.dropbearserver2.util.L;
import me.shkschneider.dropbearserver2.util.ServerUtils;

public class ExplorerActivity extends SherlockListActivity implements DialogInterface.OnClickListener {

	private TextView mCurrentPath = null;
	private String mPublicKey = null;

	private File mDir = null;
	private File mSdcard = null;
	private ExplorerAdapter mExplorerAdapter = null;
	Stack<File> mDirStack = new Stack<File>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.explorer);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		mCurrentPath = (TextView) findViewById(R.id.current_path);
		mCurrentPath.setText("SDCard: /");

		mSdcard = Environment.getExternalStorageDirectory();
		mDir = new File(mSdcard.toString());
		fill(mDir);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true ;
		}
		return super.onOptionsItemSelected(item);
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
			.setPositiveButton("OK", this)
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
		List<String> publicKeys = ServerUtils.getPublicKeys(ServerUtils.getLocalDir(this) + "/authorized_keys");
		if (button == DialogInterface.BUTTON_POSITIVE) {
			if (publicKeys.contains(mPublicKey) == false) {
				ServerUtils.addPublicKey(mPublicKey, ServerUtils.getLocalDir(this) + "/authorized_keys");
				Toast.makeText(this, "Public key successfully added", Toast.LENGTH_SHORT).show();
				finish();
			}
			else {
				Toast.makeText(this, "Public key already registered", Toast.LENGTH_SHORT).show();
			}
		}
	}

	// Adapter

	public class ExplorerAdapter extends ArrayAdapter<ExplorerItem> {

		private Context mContext;
		private Integer mId;
		private List<ExplorerItem> mItems;

		public ExplorerAdapter(Context context, Integer id, List<ExplorerItem> items) {
			super(context, id, items);
			mContext = context;
			mId = id;
			mItems = items;
		}

		public ExplorerItem getItem(Integer i) {
			return mItems.get(i);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = convertView;
			if (view == null) {
				LayoutInflater vi = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				view = vi.inflate(mId, null);
			}
			final ExplorerItem item = mItems.get(position);
			if (item != null) {
				File file = new File(item.getPath());
				TextView title = (TextView) view.findViewById(R.id.filename);
				if (file.isDirectory()) {
					title.setText(item.getName() + "/");
					title.setTypeface(null, Typeface.NORMAL);
				}
				else {
					title.setText(item.getName());
					title.setTypeface(null, Typeface.ITALIC);
				}
			}
			return view;
		}
	}

	// Item

	public class ExplorerItem implements Comparable<ExplorerItem> {

		private String mName;
		private String mPath;
		private Boolean mIsDirectory;

		public ExplorerItem(String name, String path, Boolean isDirectory) {
			mName = name;
			mPath = path;
			mIsDirectory = isDirectory;
		}

		public String getName() {
			return mName;
		}

		public String getPath() {
			return mPath;
		}

		public Boolean isDirectory() {
			return mIsDirectory;
		}

		@Override
		public int compareTo(ExplorerItem item) {
			if (mName != null) {
				return mName.toLowerCase(Locale.getDefault()).compareTo(item.getName().toLowerCase(Locale.getDefault()));
			} else {
				throw new IllegalArgumentException();
			}
		}
	}
}

