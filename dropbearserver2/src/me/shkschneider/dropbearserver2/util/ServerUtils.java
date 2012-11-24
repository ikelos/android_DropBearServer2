/*
 * Sherif elKhatib <http://stackoverflow.com/questions/6896618/read-command-output-inside-su-process>
 * Martin <http://www.droidnova.com/get-the-ip-address-of-your-device,304.html>
 * javadb <http://www.javadb.com/remove-a-line-from-a-text-file>
 * external-ip <http://code.google.com/p/external-ip/>
 */
package me.shkschneider.dropbearserver2.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.apache.http.conn.util.InetAddressUtils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public abstract class ServerUtils {

	public static String localDir = null;
	public static List<String> ipAddresses = null;

	public static final String getLocalDir(Context context) {
		if (localDir == null) {
			localDir = context.getDir("data", Context.MODE_PRIVATE).toString();
		}
		return localDir;
	}

	// WARNING: this is not threaded
	public static Boolean isOnline(Context context) {
		ConnectivityManager connectManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

		NetworkInfo mobile = connectManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		NetworkInfo wifi   = connectManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

		return ((mobile != null && mobile.getState() == NetworkInfo.State.CONNECTED) ||
				(wifi != null && wifi.getState() == NetworkInfo.State.CONNECTED));
	}

	// WARNING: this is not threaded
	public static final List<String> getIpAddresses(Context context) {
		if (ipAddresses == null) {
			ipAddresses = new ArrayList<String>();
			try {
				// android's interfaces
				for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
					NetworkInterface intf = en.nextElement();
					for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
						InetAddress inetAddress = enumIpAddr.nextElement();
						String ipAddress = inetAddress.getHostAddress().toString();
						if (InetAddressUtils.isIPv4Address(ipAddress) && !ipAddresses.contains(ipAddress)) {
							ipAddresses.add(ipAddress);
						}
					}
				}

				// external ip address
				if (ServerUtils.isOnline(context) == true) {
					Process suProcess = Runtime.getRuntime().exec("su");
					DataOutputStream stdin = new DataOutputStream(suProcess.getOutputStream());
					L.d("# busybox wget -qO - http://ifconfig.me/ip");
					stdin.writeBytes("busybox wget -qO - http://ifconfig.me/ip\n");
					stdin.flush();
					stdin.writeBytes("exit\n");
					stdin.flush();
					BufferedReader reader = new BufferedReader(new InputStreamReader(suProcess.getInputStream()));
					String line = reader.readLine();
					if (line != null) {
						ipAddresses.add(line);
					}
				}
			}
			catch (SocketException e) {
				L.e("SocketException: " + e.getMessage());
			}
			catch (IOException e) {
				L.e("IOException: " + e.getMessage());
			}
			return ipAddresses;
		}
		return ipAddresses;
	}

	// WARNING: this is not threaded
	public static final Boolean isDropbearRunning() {
		try {
			Process suProcess = Runtime.getRuntime().exec("su");

			// stdin
			DataOutputStream stdin = new DataOutputStream(suProcess.getOutputStream());
			L.d("# ps dropbear");
			stdin.writeBytes("ps dropbear\n");
			stdin.flush();
			stdin.writeBytes("exit\n");
			stdin.flush();

			// stdout
			BufferedReader reader = new BufferedReader(new InputStreamReader(suProcess.getInputStream()));
			ArrayList<String> output = new ArrayList<String>();
			String line;
			while ((line = reader.readLine()) != null) {
				if (line.endsWith("dropbear") == true) {
					output.add(line);
				}
			}

			// parsing
			return (output.size() >= 1);
		}
		catch (IOException e) {
			L.e("IOException: " + e.getMessage());
		}
		return false;
	}

	// WARNING: this is not threaded
	public static final Boolean generateRsaPrivateKey(String path) {
		return ShellUtils.execute(ServerUtils.getLocalDir(null) + "/dropbearkey -t rsa -f " + path);
	}

	// WARNING: this is not threaded
	public static final Boolean generateDssPrivateKey(String path) {
		return ShellUtils.execute(ServerUtils.getLocalDir(null) + "/dropbearkey -t dss -f " + path);
	}

	// WARNING: this is not threaded
	public static List<String> getPublicKeys(String path) {
		List<String> publicKeysList = new ArrayList<String>();
		File f = new File(path);
		if (f.exists() == true && f.isFile() == true) {
			try {
				FileInputStream fis = new FileInputStream(path);
				DataInputStream dis = new DataInputStream(fis);
				BufferedReader br = new BufferedReader(new InputStreamReader(dis));
				String line = null;
				while ((line = br.readLine()) != null) {
					publicKeysList.add(line);
				}
				dis.close();
			}
			catch (FileNotFoundException e) {
				L.e("FileNotFoundException: " + e.getMessage());
			}
			catch (IOException e) {
				L.e("IOException: " + e.getMessage());
			}
		}
		else {
			L.w("File could not be found: " + path);
		}
		return publicKeysList;
	}

	// WARNING: this is not threaded
	public static final Boolean addPublicKey(String publicKey, String path) {
		File f = new File(path);
		if (f.exists() == true && f.isFile() == true) {
			try {
				BufferedWriter out = new BufferedWriter(new FileWriter(path, true));
				out.write(publicKey + "\n");
				out.close();
				return true;
			}
			catch (IOException e) {
				L.e("IOException: " + e.getMessage());
			}
		}
		return false;
	}

	// WARNING: this is not threaded
	public static final Boolean removePublicKey(String publicKey, String path) {
		File f = new File(path);
		if (f.exists() == true && f.isFile() == true) {
			try {
				File inFile = new File(path);
				File tempFile = new File(inFile.getAbsolutePath() + ".tmp");

				BufferedReader br = new BufferedReader(new FileReader(path));
				PrintWriter pw = new PrintWriter(new FileWriter(tempFile));

				String line = null;

				while ((line = br.readLine()) != null) {
					if (!line.trim().equals(publicKey)) {
						pw.println(line);
						pw.flush();
					}
				}
				pw.close();
				br.close();

				if (!inFile.delete()) {
					L.d("delete() failed");
					return false;
				}
				if (!tempFile.renameTo(inFile)) {
					L.d("renameTo() failed");
					return false;
				}
				return true;
			}
			catch (FileNotFoundException e) {
				L.d("FileNotFoundException: " + e.getMessage());
			}
			catch (IOException e) {
				L.d("IOException: " + e.getMessage());
			}
		}
		return false;
	}

	public static final Boolean createIfNeeded(String path) {
		File file = new File(path);
		if (file.exists() == false) {
			try {
				file.createNewFile();
				return true;
			}
			catch (IOException e) {
				L.e("IOException: " + e.getMessage());
			}
		}
		return false;
	}

	// WARNING: this is not threaded
	public static final String getDropbearVersion(Context context) {
		String version = null;
		try {
			Process suProcess = Runtime.getRuntime().exec("su");

			// stdin
			DataOutputStream stdin = new DataOutputStream(suProcess.getOutputStream());
			L.d("# dropbear -h");
			stdin.writeBytes(ServerUtils.getLocalDir(context) + "/dropbear -h 2>&1 | busybox head -1\n");
			stdin.flush();
			stdin.writeBytes("exit\n");
			stdin.flush();

			// stdout
			BufferedReader reader = new BufferedReader(new InputStreamReader(suProcess.getInputStream()));
			String line = reader.readLine();

			// parsing
			if (line != null && line.matches("^Dropbear sshd v[0-9\\.]+$")) {
				version = line.replaceFirst("^Dropbear sshd v", "");
			}
		}
		catch (IOException e) {
			L.e("IOException: " + e.getMessage());
		}
		return version;
	}
}
