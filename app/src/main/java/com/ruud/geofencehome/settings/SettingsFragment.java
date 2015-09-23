package com.ruud.geofencehome.settings;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.ruud.geofencehome.R;

/**
 * The setting fragment that shows the preferences for the application.
 */
public class SettingsFragment extends PreferenceFragment {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Load the preferences as configured in the /res/xml/preferences.xml file
		// and displays them.
		// The preferences will be automatically saved.
		addPreferencesFromResource(R.xml.preferences);
	}


}

