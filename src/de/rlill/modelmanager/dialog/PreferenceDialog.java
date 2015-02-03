package de.rlill.modelmanager.dialog;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import de.rlill.modelmanager.R;
import de.rlill.modelmanager.Util;
import de.rlill.modelmanager.service.PropertiesService;

public class PreferenceDialog extends PreferenceFragment implements
		OnSharedPreferenceChangeListener {

	private static final String LOG_TAG = "MM*" + PreferenceDialog.class.getSimpleName();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);

		// show the current value in the settings screen
		for (int i = 0; i < getPreferenceScreen().getPreferenceCount(); i++) {
			initSummary(getPreferenceScreen().getPreference(i));
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		getPreferenceScreen().getSharedPreferences()
				.registerOnSharedPreferenceChangeListener(this);
	}

	@Override
	public void onPause() {
		super.onPause();
		getPreferenceScreen().getSharedPreferences()
				.unregisterOnSharedPreferenceChangeListener(this);
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		String overwriteValue = null;
		if (key.equals(getResources().getString(R.string.worthincrease))) {
			double wi = Util.atof(sharedPreferences.getString(key, "1"));
			// format with 2 fraction digits
			overwriteValue = String.format("%.2f", wi);
			if (!PropertiesService.setWorthincrease(wi)) {
				// setting new value failed, revert to previous
				wi = PropertiesService.getWorthincrease();
				overwriteValue = String.format("%.2f", wi);
				SharedPreferences.Editor editor = sharedPreferences.edit();
				editor.putString(key, overwriteValue);
				editor.commit();
			}
		}
		updatePreferences(findPreference(key), overwriteValue);
	}

	private void initSummary(Preference p) {
		if (p instanceof PreferenceCategory) {
			PreferenceCategory cat = (PreferenceCategory) p;
			for (int i = 0; i < cat.getPreferenceCount(); i++) {
				initSummary(cat.getPreference(i));
			}
		} else {
			updatePreferences(p, null);
		}
	}

	private void updatePreferences(Preference p, String overwriteValue) {
		if (p instanceof EditTextPreference) {
			EditTextPreference editTextPref = (EditTextPreference) p;
			p.setSummary((overwriteValue != null) ? overwriteValue : editTextPref.getText());
		}
	}
}
