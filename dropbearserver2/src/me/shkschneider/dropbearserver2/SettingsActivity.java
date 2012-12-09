package me.shkschneider.dropbearserver2;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.text.InputType;
import android.view.WindowManager.LayoutParams;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.actionbarsherlock.app.SherlockPreferenceActivity;
import com.actionbarsherlock.view.MenuItem;

public class SettingsActivity extends SherlockPreferenceActivity implements OnPreferenceClickListener {

	private static final int PORT_MIN = 1;
	private static final int PORT_MAX = 65535;

	private CheckBoxPreference mAllowPassword = null;
	private CheckBoxPreference mStartBoot = null;
	private Preference mPassword = null;
	private Preference mPort = null;

	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.settings);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		final Context context = getApplicationContext();

		mAllowPassword = (CheckBoxPreference) findPreference("allow_password");
		mAllowPassword.setDefaultValue(LocalPreferences.getBoolean(context,
				LocalPreferences.PREF_ALLOW_PASSWORD,
				LocalPreferences.PREF_ALLOW_PASSWORD_DEFAULT));
		mAllowPassword.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				LocalPreferences.putBoolean(context,
						LocalPreferences.PREF_ALLOW_PASSWORD,
						(Boolean) newValue);
				return true;
			}
		});

		mStartBoot = (CheckBoxPreference) findPreference("start_boot");
		mStartBoot.setDefaultValue(LocalPreferences.getBoolean(context,
				LocalPreferences.PREF_START_BOOT,
				LocalPreferences.PREF_START_BOOT_DEFAULT));
		mStartBoot.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				LocalPreferences.putBoolean(context,
						LocalPreferences.PREF_START_BOOT,
						(Boolean) newValue);
				return true;
			}
		});

		mPassword = findPreference("password");
		mPassword.setSummary(LocalPreferences.getString(context, LocalPreferences.PREF_PASSWORD, LocalPreferences.PREF_PASSWORD_DEFAULT));
		mPassword.setOnPreferenceClickListener(this);

		mPort = findPreference("port");
		mPort.setSummary(LocalPreferences.getString(context, LocalPreferences.PREF_PORT, LocalPreferences.PREF_PORT_DEFAULT));
		mPort.setOnPreferenceClickListener(this);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onPreferenceClick(final Preference preference) {
		final Context context = getApplicationContext();

		if (preference == mPassword) {
			AlertDialog alertDialog = new AlertDialog.Builder(this).create();
			alertDialog.setCancelable(false);
			alertDialog.setCanceledOnTouchOutside(false);
			alertDialog.setIcon(android.R.drawable.ic_dialog_info);
			alertDialog.setTitle("Password");
			alertDialog.setMessage(null);

			final EditText editText = new EditText(this);
			editText.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
			editText.setHint(LocalPreferences.PREF_PASSWORD_DEFAULT);
			editText.setText(LocalPreferences.getString(context, LocalPreferences.PREF_PASSWORD, LocalPreferences.PREF_PASSWORD_DEFAULT));
			editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
			editText.requestFocus();

			alertDialog.getWindow().setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_VISIBLE);
			alertDialog.setView(editText);
			alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
				}
			});
			alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					String password = editText.getText().toString();
					if (password.length() == 0) {
						password = LocalPreferences.PREF_PASSWORD_DEFAULT;
					}
					LocalPreferences.putString(context, LocalPreferences.PREF_PASSWORD, password);
					preference.setSummary(password);
				}
			});
			alertDialog.show();

			return true;
		}
		else if (preference == mPort) {
			AlertDialog alertDialog = new AlertDialog.Builder(this).create();
			alertDialog.setCancelable(false);
			alertDialog.setCanceledOnTouchOutside(false);
			alertDialog.setIcon(android.R.drawable.ic_dialog_info);
			alertDialog.setTitle("Port");
			alertDialog.setMessage(null);

			final EditText editText = new EditText(this);
			editText.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
			editText.setHint(LocalPreferences.PREF_PORT_DEFAULT);
			editText.setText(LocalPreferences.getString(context, LocalPreferences.PREF_PORT, LocalPreferences.PREF_PORT_DEFAULT));
			editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
			editText.requestFocus();

			alertDialog.getWindow().setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_VISIBLE);
			alertDialog.setView(editText);
			alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
				}
			});
			alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					String port = editText.getText().toString();
					if (port.length() == 0 || port.matches("^[0-9]+$") == false) {
						port = LocalPreferences.PREF_PORT_DEFAULT;
					}
					Integer value = Integer.valueOf(port);
					if (value < PORT_MIN || value > PORT_MAX) {
						port = LocalPreferences.PREF_PORT_DEFAULT;
					}
					LocalPreferences.putString(context, LocalPreferences.PREF_PORT, port);
					preference.setSummary(port);
				}
			});
			alertDialog.show();
		}
		return false;
	}
}
