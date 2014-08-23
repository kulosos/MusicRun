package de.thm.fmi.musicrun.application;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.preference.PreferenceManager;

public class PreferencesManager implements OnSharedPreferenceChangeListener {

	// Preferences
	private SharedPreferences prefs;
	private String musicFilepath = "";
	
	// ------------------------------------------------------------------------
	
	public PreferencesManager(Context context){
		
		// PREFERENCES
	    this.prefs = PreferenceManager.getDefaultSharedPreferences(context);
        // register preference change listener
        prefs.registerOnSharedPreferenceChangeListener(this);
        // and set remembered preferences
        this.musicFilepath = (prefs.getString("pref_key_musicfilepath", "/storage/extSdCard/"));
	}

	// ------------------------------------------------------------------------

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

		if (key.equals("pref_key_musicfilepath")) {
			this.musicFilepath = (prefs.getString("pref_key_musicfilepath", "/storage/extSd/"));
		}

	}

	// ------------------------------------------------------------------------
	
	public SharedPreferences getPreferences() {
		return prefs;
	}

	public void setPreferences(SharedPreferences prefs) {
		this.prefs = prefs;
	}

	public String getMusicFilepath() {
		return musicFilepath;
	}

	public void setMusicFilepath(String musicFilepath) {
		this.musicFilepath = musicFilepath;
	}


	
	
	
	
}
