package jp.ddo.masm11.lounlocker;

import android.preference.PreferenceFragment;
import android.preference.EditTextPreference;
import android.preference.SwitchPreference;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.preference.PreferenceCategory;
import android.preference.Preference.OnPreferenceChangeListener;
import android.os.Bundle;
import android.os.Environment;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.Context;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Calendar;
import java.text.DateFormat;

public class LoUnlockerPreferenceFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
	Log.d("");
	super.onCreate(savedInstanceState);
	addPreferencesFromResource(R.xml.preference_fragment);
	
	PreferenceManager manager = getPreferenceManager();
	PreferenceCategory cat = (PreferenceCategory) manager.findPreference("dirs");
	
	for (String key: new String[] { "hostname", "username" }) {
	    EditTextPreference etp;
	    etp = (EditTextPreference) findPreference(key);
	    etp.setSummary(etp.getText());
	    etp.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
		@Override
		public boolean onPreferenceChange(Preference pref, Object val) {
		    pref.setSummary(val.toString());
		    return true;
		}
	    });
	}
	
	for (String key: new String[] { "sudo", "decr" }) {
	    EditTextPreference etp;
	    etp = (EditTextPreference) findPreference(key);
	    etp.setSummary(replaceWithAsterisk(etp.getText()));
	    etp.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
		@Override
		public boolean onPreferenceChange(Preference pref, Object val) {
		    pref.setSummary(replaceWithAsterisk(val.toString()));
		    return true;
		}
	    });
	}
    }
    
    private static String replaceWithAsterisk(String orig) {
	if (orig == null)
	    return null;
	return orig.replaceAll(".", "*");
    }
}
