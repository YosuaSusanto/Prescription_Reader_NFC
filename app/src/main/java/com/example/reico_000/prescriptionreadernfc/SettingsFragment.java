package com.example.reico_000.prescriptionreadernfc;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.view.Menu;
import android.view.MenuItem;

/**
 * Created by Yosua Susanto on 16/2/2016.
 */
public class SettingsFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.fragment_preferences);
        setHasOptionsMenu(true);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.action_legends).setVisible(false);
        menu.findItem(R.id.action_report).setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_NEVER);
        super.onPrepareOptionsMenu(menu);
    }

}