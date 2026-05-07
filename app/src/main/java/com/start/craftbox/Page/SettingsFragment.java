package com.start.craftbox.Page;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

import com.start.craftbox.R;

public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);
    }
}