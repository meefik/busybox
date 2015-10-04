package ru.meefik.busybox;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceGroup;
import android.preference.PreferenceScreen;

/**
 * Created by anton on 19.09.15.
 */
public class SettingsActivity extends AppCompatPreferenceActivity implements
        SharedPreferences.OnSharedPreferenceChangeListener, Preference.OnPreferenceClickListener {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PrefStore.setLocale(this);
        getSupportActionBar().setTitle(getString(R.string.title_activity_settings));

        // init variables
        PrefStore.getLogFile(this);

        getPreferenceManager().setSharedPreferencesName(PrefStore.APP_PREF_NAME);
        addPreferencesFromResource(R.xml.settings);
        initSummaries(getPreferenceScreen());
    }

    @Override
    public void setTheme(int resid) {
        super.setTheme(PrefStore.getTheme(this));
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        return false;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Preference pref = findPreference(key);
        setSummary(pref, true);
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }

    /**
     * Recursive set summaries
     *
     * @param pg group
     */
    private void initSummaries(PreferenceGroup pg) {
        for (int i = 0; i < pg.getPreferenceCount(); ++i) {
            Preference p = pg.getPreference(i);
            if (p instanceof PreferenceGroup)
                initSummaries((PreferenceGroup) p);
            else
                setSummary(p, false);
            if (p instanceof PreferenceScreen)
                p.setOnPreferenceClickListener(this);
        }
    }

    /**
     * Set summary for preference
     *
     * @param pref preference
     * @param init true if no recursive
     */
    private void setSummary(Preference pref, boolean init) {
        if (pref instanceof EditTextPreference) {
            EditTextPreference editPref = (EditTextPreference) pref;
            pref.setSummary(editPref.getText());
        }

        if (pref instanceof ListPreference) {
            ListPreference listPref = (ListPreference) pref;
            pref.setSummary(listPref.getEntry());
        }
    }
}