/*
 * elsdoerfer <http://blog.elsdoerfer.name/2010/04/15/android-check-if-sd-card-storage-is-available/>
 */
package me.shkschneider.dropbearserver2.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.os.Environment;

import com.stericson.RootTools.RootTools;

public abstract class Utils {

	public static final Boolean copyRawFile(Context context, int rawId, String path) {
		try {
			InputStream in = context.getResources().openRawResource(rawId);
			OutputStream out = new FileOutputStream(new File(path));
			byte[] buffer = new byte[1024];
			int read;
			while ((read = in.read(buffer)) != -1) {
				out.write(buffer, 0, read);
			}
			in.close();
			out.flush();
			out.close();
			return true;
		}
		catch(FileNotFoundException e) {
			L.e("FileNotFoundException: " + e.getMessage());
		}
		catch(IOException e) {
			L.e("IOException: " + e.getMessage());
		}
		return false;
	}

	public static final Boolean remountReadWrite(String path) {
		return RootTools.remount(path, "RW");
	}

	public static final Boolean remountReadOnly(String path) {
		return RootTools.remount(path, "RO");
	}

	public static Boolean hasStorage(Boolean requireWriteAccess) {
		String state = Environment.getExternalStorageState();

		if (Environment.MEDIA_MOUNTED.equals(state)) {
			return true;
		}
		else if (!requireWriteAccess && Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
			return true;
		}

		return false;
	}
}
