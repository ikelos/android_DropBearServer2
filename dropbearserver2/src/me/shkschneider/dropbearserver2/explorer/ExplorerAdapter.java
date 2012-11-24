/*
 * Michael Almyros <http://www.dreamincode.net/forums/topic/190013-creating-simple-file-chooser/>
 */
package me.shkschneider.dropbearserver2.explorer;

import java.io.File;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import me.shkschneider.dropbearserver2.R;

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
				title.setTextColor(mContext.getResources().getColor(R.color.green_dark));
			}
			else {
				title.setText(item.getName());
				title.setTextColor(mContext.getResources().getColor(android.R.color.white));
			}
		}
		return view;
	}

}


