package me.shkschneider.dropbearserver2;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Build;
import android.view.WindowManager;

public class MyDialog implements OnClickListener {

	public static final int CHOICE_NEGATIVE = 2;
	public static final int CHOICE_POSITIVE = 4;
	public static final int CHOICE_NEUTRAL = 8;
	public static final int CHOICE_BOTH = (CHOICE_NEGATIVE + CHOICE_POSITIVE);
	public static final int CHOICE_ALL = (CHOICE_NEGATIVE + CHOICE_NEUTRAL + CHOICE_POSITIVE);

	private static AlertDialog mDialog = null;

	private MyDialogCallback mCallback = null;
	private Integer mScenario = 0;

	public MyDialog(Context context, int scenario) {
		if (mDialog != null && mDialog.isShowing() == true) {
			mDialog.dismiss();
		}

		mDialog = new AlertDialog.Builder(context).create();

		mDialog.setIcon(null);
		mDialog.setTitle(null);
		mDialog.setMessage(null);
		mDialog.setCancelable(false);
		mDialog.setCanceledOnTouchOutside(false);

		setScenario(context, mScenario);
	}

	// Fixes

	public MyDialog setIcon(int iconId) {
		mDialog.setIcon(iconId);
		return this;
	}

	public MyDialog setTitle(CharSequence title) {
		mDialog.setTitle(title);
		return this;
	}

	public MyDialog setMessage(CharSequence message) {
		mDialog.setMessage(message);
		return this;
	}

	private void setScenario(Context context, int scenario) {
		if (mScenario == CHOICE_NEGATIVE || mScenario == CHOICE_BOTH || mScenario == CHOICE_ALL) {
			mDialog.setButton(Dialog.BUTTON_NEGATIVE, "No", new OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					if (mCallback != null) {
						mCallback.onMyDialogCallback(CHOICE_NEGATIVE);
					}
				}
			});
		}
		if (mScenario != CHOICE_BOTH) {
			mDialog.setButton(Dialog.BUTTON_NEUTRAL, ((mScenario == CHOICE_ALL) ? "Cancel" : "OK"), new OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					if (mCallback != null) {
						mCallback.onMyDialogCallback(CHOICE_NEUTRAL);
					}
				}
			});
		}
		if (mScenario == CHOICE_POSITIVE || mScenario == CHOICE_BOTH || mScenario == CHOICE_ALL) {
			mDialog.setButton(Dialog.BUTTON_POSITIVE, "Yes", new OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					if (mCallback != null) {
						mCallback.onMyDialogCallback(CHOICE_POSITIVE);
					}
				}
			});
		}
	}

	public MyDialog setCallback(MyDialogCallback callback) {
		mCallback = callback;
		return this;
	}

	public MyDialog show() {
		if (MainApplication.ANDROID_API < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			old_blur();
		}
		mDialog.show();
		return null;
	}

	@SuppressWarnings("deprecation")
	private void old_blur() {
		mDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		mCallback.onMyDialogCallback(which);
	}

	public interface MyDialogCallback {
		public void onMyDialogCallback(int choice);
	}
}
