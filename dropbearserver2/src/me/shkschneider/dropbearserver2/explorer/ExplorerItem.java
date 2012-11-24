/*
 * Michael Almyros <http://www.dreamincode.net/forums/topic/190013-creating-simple-file-chooser/>
 */
package me.shkschneider.dropbearserver2.explorer;

import java.util.Locale;

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
