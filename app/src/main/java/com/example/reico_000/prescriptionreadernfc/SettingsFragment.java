package com.example.reico_000.prescriptionreadernfc;

import android.os.Bundle;
import android.preference.PreferenceFragment;

/**
 * Created by Yosua Susanto on 16/2/2016.
 */
public class SettingsFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.fragment_preferences);
    }
}