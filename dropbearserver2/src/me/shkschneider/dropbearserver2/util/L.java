package me.shkschneider.dropbearserver2.util;

import android.util.Log;

public class L {

	private static final int VERBOSE = 10;
	private static final int DEBUG = 20;
	private static final int INFO = 30;
	private static final int WARN = 40;
	private static final int ERROR = 50;

	private static final void log(int state, final String msg) {
		String tag = "DropBearServer2";

		// Uses StackTrace to build the log tag
		StackTraceElement[] elements = new Throwable().getStackTrace();
		String callerClassName = "?";
		String callerMethodName = "?";

		if (elements.length >= 3) {
			callerClassName = elements[2].getClassName();
			callerClassName = callerClassName.substring(callerClassName.lastIndexOf('.') + 1);
			if (callerClassName.indexOf("$") > 0) {
				callerClassName = callerClassName.substring(0, callerClassName.indexOf("$"));
			}

			callerMethodName = elements[2].getMethodName();
			callerMethodName = callerMethodName.substring(callerMethodName.lastIndexOf('_') + 1);
		}
		String stack = callerClassName + " " + callerMethodName + "()";

		switch (state) {
		case VERBOSE:
			Log.v(tag, "[" + stack + "] " + msg);
			break ;

		case DEBUG:
			Log.d(tag, "[" + stack + "] " + msg);
			break ;

		case INFO:
			Log.i(tag, "[" + stack + "] " + msg);
			break ;

		case WARN:
			Log.w(tag, "[" + stack + "] " + msg);
			break ;

		case ERROR:
			Log.e(tag, "[" + stack + "] " + msg);
			break ;
		}
	}

	public static final void v(final String msg) {
		log(VERBOSE, msg);
	}

	public static final void d(final String msg) {
		log(DEBUG, msg);
	}

	public static final void i(final String msg) {
		log(INFO, msg);
	}

	public static final void w(final String msg) {
		log(WARN, msg);
	}

	public static final void e(final String msg) {
		log(ERROR, msg);
	}
}
