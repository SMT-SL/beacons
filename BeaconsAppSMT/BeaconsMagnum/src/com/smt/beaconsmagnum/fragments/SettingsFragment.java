package com.smt.beaconsmagnum.fragments;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.smt.beaconsmagnum.R;

public class SettingsFragment extends PreferenceFragment {

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
    }

}
