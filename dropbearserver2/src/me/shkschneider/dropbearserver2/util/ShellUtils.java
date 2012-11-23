/*
 * Muzikant <http://muzikant-android.blogspot.fr/2011/02/how-to-get-root-access-and-execute.html>
 */
package me.shkschneider.dropbearserver2.util;

import java.io.File;

import com.stericson.RootTools.CommandCapture;
import com.stericson.RootTools.RootTools;


public abstract class ShellUtils {

	public static final Boolean execute(String command) {
		CommandCapture commands = new CommandCapture(0, command);
		try {
			RootTools.getShell(true).add(commands).waitForFinish();
		}
		catch (Exception e) {
			L.e(e.getMessage());
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public static final Boolean mkdir(String path) {
		return execute("mkdir " + path);
	}

	public static final Boolean mkdirRecursive(String path) {
		return execute("mkdir -p " + path);
	}

	public static final Boolean chown(String path, String owner) {
		return execute("chown " + owner + " " + path);
	}

	public static final Boolean chownRecursive(String path, String owner) {
		return execute("chown -R " + owner + " " + path);
	}

	public static final Boolean chmod(String path, String chmod) {
		return execute("chmod " + chmod + " " + path);
	}

	public static final Boolean chmodRecursive(String path, String chmod) {
		return execute("chmod -R " + chmod + " " + path);
	}

	public static final Boolean touch(String path) {
		return execute("echo -n '' > " + path);
	}

	public static final Boolean rm(String path) {
		return execute("rm -f " + path);
	}

	public static final Boolean rmRecursive(String path) {
		return execute("rm -rf " + path);
	}

	public static final Boolean mv(String srcPath, String destPath) {
		return execute("mv " + srcPath + " " + destPath);
	}

	public static final Boolean cp(String srcPath, String destPath) {
		return execute("cp " + srcPath + " " + destPath);
	}

	public static final Boolean cpRecursive(String srcPath, String destPath) {
		return execute("cp -r " + srcPath + " " + destPath);
	}

	public static final Boolean echoToFile(String text, String path) {
		return execute("echo '" + text + "' > " + path);
	}

	public static final Boolean echoAppendToFile(String text, String path) {
		return execute("echo '" + text + "' >> " + path);
	}

	public static final Boolean lnSymbolic(String srcPath, String destPath) {
		return execute("ln -s " + srcPath + " " + destPath);
	}

	public static final Boolean killall(String processName) {
		return execute("killall " + processName);
	}

	public static final Boolean remountReadWrite(String path) {
		return RootTools.remount(path, "RW");
	}

	public static final Boolean remountReadOnly(String path) {
		return RootTools.remount(path, "RO");
	}

	/*
	public static final Boolean wget(String urlStr, String localName) {
        BufferedInputStream bis = null;

        try {
            URL url = new URL(urlStr);

            URLConnection urlCon = url.openConnection();
            bis = new BufferedInputStream(urlCon.getInputStream());

            ByteArrayBuffer baf = new ByteArrayBuffer(50);
            int current = 0;
            while (((current = bis.read()) != -1)) {
                baf.append((byte) current);
                if (isCancelled()) {
                    return false;
                }
            }
            bis.close();

            if (isCancelled()) {
                return false;
            } else {
                FileOutputStream outFileStream = getActivity().openFileOutput(localName, 0);
                outFileStream.write(baf.toByteArray());
                outFileStream.close();
                if (localName.equals("busybox")) {
                    mBusyboxPath = getActivity().getFilesDir().getAbsolutePath().concat("/busybox");
                }
            }

        } catch (MalformedURLException e) {
            Log.e(TAG, "Bad URL: " + urlStr, e);
            return false;
        } catch (IOException e) {
            Log.e(TAG, "Problem downloading file: " + localName, e);
            return false;
        }
        return true;
    }

	public static final String md5sum(String s) {
	    try {
	        MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
	        digest.update(s.getBytes());
	        byte messageDigest[] = digest.digest();

	        StringBuffer hexString = new StringBuffer();
	        for (int i = 0; i < messageDigest.length; i++) {
	            String h = Integer.toHexString(0xFF & messageDigest[i]);
	            while (h.length() < 2)
	                h = "0" + h;
	            hexString.append(h);
	        }
	        return hexString.toString();

	    }
	    catch (Exception e) {
	        Log.e(TAG, e.getMessage());
	    }
	    return "";
	}

	public static Boolean md5chk(String path, String md5sum) {
        if (mBusyboxPath == null) {
            Log.e(TAG, "Busybox not present");
            return false;
        }

        Process process = null;
        try {
            process = Runtime.getRuntime().exec(new String[] { mBusyboxPath, "md5sum", path});
            BufferedReader is = new BufferedReader(new InputStreamReader(new DataInputStream(process.getInputStream())), 64);
            BufferedReader es = new BufferedReader(new InputStreamReader(new DataInputStream(process.getErrorStream())), 64);
            for (int i = 0; i < 200; i++) {
                if (is.ready())
                	break;
                try {
                    Thread.sleep(5);
                }
                catch (InterruptedException e) {
                    Log.e(TAG, "md5chk(): sleep timer got interrupted");
                }
            }
            String inLine = null;
            if (es.ready()) {
                inLine = es.readLine();
                Log.i(TAG, inLine);
            }
            if (is.ready()) {
                inLine = is.readLine();
            }
            else {
                Log.e(TAG, "md5chk(): could not check md5sum");
                return false;
            }
            process.destroy();
            if (!inLine.split(" ")[0].equals(md5sum)) {
                Log.e(TAG, "md5chk(): checksum mismatch");
                return false;
            }
        } catch (IOException e) {
            Log.e(TAG, "md5chk(): checking of md5sum failed", e);
            return false;
        }
        return true;
    }
	 */

	public static String which(String binaryName) {
		String path = System.getenv("PATH");
		for (String s : path.split(File.pathSeparator)) {
			File file = new File(s, binaryName);
			if (file.exists()) {
				return file.getAbsolutePath();
			}
		}
		return null;
	}

}
