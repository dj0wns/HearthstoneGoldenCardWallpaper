package com.dj0wns.goldencards.hearthstonegoldencardwallpaper;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Created by dj0wns on 4/7/16.
 */
public class SettingsActivity extends PreferenceActivity {

    SharedPreferences prefs;
    @Override
    protected void onCreate(final Bundle savedInstanceState)
    {
        prefs =PreferenceManager.getDefaultSharedPreferences
            (getApplicationContext());
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new MyPreferenceFragment()).commit();
        prefs.registerOnSharedPreferenceChangeListener(spChanged);
    }

    public static class MyPreferenceFragment extends PreferenceFragment
    {
        @Override
        public void onCreate(final Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings);
        }
    }
    SharedPreferences.OnSharedPreferenceChangeListener spChanged = new
            SharedPreferences.OnSharedPreferenceChangeListener() {
                @Override
                public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
                                                      String key) {
                    if(key.equals("update_frequency")){
                        GIFWallpaperService.delay = Long.decode(sharedPreferences
                                .getString("update_frequency",
                                "120")) * 1000 * 60;
                        Log.d("key caught", key);
                    } else if(key.equals("framerate")){
                        GIFWallpaperService.sleep = 1000/Integer.decode
                                (sharedPreferences
                                .getString("framerate",
                                        "20"));
                        Log.d("key caught", key);
                    } else {
                        Log.d("uncaught change", key);
                    }
                }
            };

}