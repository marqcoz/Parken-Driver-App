package com.parken.parkenv03;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.preference.RingtonePreference;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class SettingsActivity extends AppCompatPreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setupActionBar();

    }
    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * This fragment shows notification preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class ParkenPreferenceFragment extends PreferenceFragment {

        ShPref session;
        int apps;
        CharSequence[] charApplicaciones = {""};

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.main_settings);
            setHasOptionsMenu(true);

            session = new ShPref(getActivity());

            PreferenceScreen screen = getPreferenceScreen();
            final PreferenceCategory category = new PreferenceCategory(getActivity());

            category.setTitle("Navegación GPS");
            screen.addPreference(category);
            List<String> appicaciones = new ArrayList<String>();

            try {
                ArrayList<ResolveInfo> listaApps = new ArrayList<ResolveInfo>();
                Uri location = Uri.parse("geo:0,0?q=1600+Amphitheatre+Parkway,+Mountain+View,+California");
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, location);
                PackageManager packageManager = getActivity().getPackageManager();
                List<ResolveInfo> activities = packageManager.queryIntentActivities(mapIntent, PackageManager.GET_META_DATA);
                apps = 0;

                for(ResolveInfo info: activities){
                    final String nameApp = info.loadLabel(packageManager).toString();
                    if(!nameApp.equals("Uber")){
                        final CheckBoxPreference checkBoxPref = new CheckBoxPreference(getActivity());
                        appicaciones.add(nameApp);
                        checkBoxPref.setTitle(nameApp);
                        checkBoxPref.setIcon(info.loadIcon(packageManager));
                        checkBoxPref.setKey(nameApp);

                        if(session.getGPS().equals(nameApp)){
                            checkBoxPref.setChecked(true);
                        }else {
                            checkBoxPref.setChecked(false);
                        }

                        category.addPreference(checkBoxPref);
                        checkBoxPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                            @Override
                            public boolean onPreferenceClick(Preference preference) {
                                //Log.d("Preference", nameApp);
                                session.setGPS(nameApp);
                                clearCheckBoxPref();
                                checkBoxPref.setChecked(true);
                                return true;
                            }
                        });
                        apps++;
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }

            setPreferenceScreen(screen);
            charApplicaciones = appicaciones.toArray(new CharSequence[appicaciones.size()]);
            checkApps();


            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.

            bindPreferenceSummaryToValue(findPreference("notify_time"));
            bindPreferenceSummaryToValue(findPreference("distance_zonaParken"));

        }

        public void clearCheckBoxPref(){
            for(int g = 0; g < charApplicaciones.length; g++){
                CheckBoxPreference c = (CheckBoxPreference) findPreference(charApplicaciones[g]);
                c.setChecked(false);
            }
        }

        public void checkApps(){
            boolean appInstalled = false;
            for(int g = 0; g < charApplicaciones.length; g++){
                if(charApplicaciones[g].equals(session.getGPS())){
                    appInstalled = true;
                }
            }
            if(!appInstalled){
                CheckBoxPreference d = (CheckBoxPreference) findPreference(charApplicaciones[0]);
                session.setGPS(charApplicaciones[0].toString());
                d.setChecked(true);
                }

        }


    }

    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();

            if (preference instanceof ListPreference) {
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list.
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);

                // Set the summary to reflect the new value.
                preference.setSummary(
                        index >= 0
                                ? listPreference.getEntries()[index]
                                : null);

            } else if (preference instanceof RingtonePreference) {
                // For ringtone preferences, look up the correct display value
                // using RingtoneManager.
                if (TextUtils.isEmpty(stringValue)) {
                    // Empty values correspond to 'silent' (no ringtone).
                    preference.setSummary(R.string.pref_ringtone_silent);

                } else {
                    Ringtone ringtone = RingtoneManager.getRingtone(
                            preference.getContext(), Uri.parse(stringValue));

                    if (ringtone == null) {
                        // Clear the summary if there was a lookup error.
                        preference.setSummary(null);
                    } else {
                        // Set the summary to reflect the new ringtone display
                        // name.
                        String name = ringtone.getTitle(preference.getContext());
                        preference.setSummary(name);
                    }
                }

            } else {
                // For all other preferences, set the summary to the value's
                // simple string representation.
                preference.setSummary(stringValue);
            }
            return true;
        }
    };

    private static void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        // Trigger the listener immediately with the preference's
        // current value.
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }


    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == android.R.id.home) {

            onBackPressed();
            //startActivity(new Intent(getActivity(), ParkenActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
