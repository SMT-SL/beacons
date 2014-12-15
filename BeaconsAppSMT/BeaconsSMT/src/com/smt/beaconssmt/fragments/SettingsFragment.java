package com.smt.beaconssmt.fragments;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.smt.beaconssmt.R;

public class SettingsFragment extends PreferenceFragment {

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
    }

}
