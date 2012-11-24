package me.shkschneider.dropbearserver2;

import android.content.Context;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;

import com.actionbarsherlock.app.SherlockPreferenceActivity;
import com.actionbarsherlock.view.MenuItem;

public class SettingsActivity extends SherlockPreferenceActivity {

	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.settings);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		final Context context = getApplicationContext();

		findPreference("allow_password").setDefaultValue(LocalPreferences.getBoolean(context,
				LocalPreferences.PREF_ALLOW_PASSWORD,
				LocalPreferences.PREF_ALLOW_PASSWORD_DEFAULT));
		findPreference("allow_password").setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				LocalPreferences.putBoolean(context,
						LocalPreferences.PREF_ALLOW_PASSWORD,
						(Boolean) newValue);
				return true;
			}
		});

		findPreference("start_boot").setDefaultValue(LocalPreferences.getBoolean(context,
				LocalPreferences.PREF_START_BOOT,
				LocalPreferences.PREF_START_BOOT_DEFAULT));
		findPreference("start_boot").setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				LocalPreferences.putBoolean(context,
						LocalPreferences.PREF_START_BOOT,
						(Boolean) newValue);
				return true;
			}
		});
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
}
