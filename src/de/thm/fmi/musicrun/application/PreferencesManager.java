package de.thm.fmi.musicrun.application;

import de.thm.fmi.musicrun.pedometer.PedometerController;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.preference.PreferenceManager;

public class PreferencesManager implements OnSharedPreferenceChangeListener {

	private static PreferencesManager instance;
	
	// Preferences
	private SharedPreferences prefs;
	private String musicFilepath = "";
	private float stepLength = 120.0f;
	private boolean autostartPedometer = true;
	private int minimumPlaybackTime = 30;
	private boolean isCrossfadingActive = true;
	private int crossfadingDuration = 5;
	private int pitchValue = 5;

	// DEBUG
	private static final String TAG = MainActivity.class.getName();
	private static final boolean D = true;
	
	// ------------------------------------------------------------------------
	
	private PreferencesManager(Context context){
		
	    this.prefs = PreferenceManager.getDefaultSharedPreferences(context);
        // register preference change listener
        prefs.registerOnSharedPreferenceChangeListener(this);
        
        // and set remembered preferences
        // PLAYER
        this.musicFilepath = (prefs.getString("pref_key_musicfilepath", "/storage/extSdCard/"));
        this.isCrossfadingActive = (prefs.getBoolean("pref_key_crossfading_toggle", true));
        this.crossfadingDuration = Integer.parseInt((prefs.getString("pref_key_crossfading_duration", "5")));
        // PEDOMETER
        this.stepLength = Float.parseFloat((prefs.getString("pref_key_steplength", "120.0")));
        this.autostartPedometer = (prefs.getBoolean("pref_key_autostart_pedometer", true));
        this.minimumPlaybackTime = Integer.parseInt(prefs.getString("pref_key_minimum_playbacktime", "30"));
        this.pitchValue = Integer.parseInt(prefs.getString("pref_key_pitch_value", "5"));
	}
	
	// ------------------- SINGLETON METHODS ----------------------------------

	public static void initInstance(Context context){
		if(instance == null){
			instance = new PreferencesManager(context);
		}
	}

	public static PreferencesManager getInstance(){
		return instance;
	}

	// ------------------------------------------------------------------------

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

		// PLAYER
		if (key.equals("pref_key_musicfilepath")) {
			this.musicFilepath = (prefs.getString("pref_key_musicfilepath", "/storage/extSd/"));
		}
		if (key.equals("pref_key_crossfading_toggle")) {
			this.isCrossfadingActive = (prefs.getBoolean("pref_key_crossfading_toggle", true));
		}
		if (key.equals("pref_key_crossfading_duration")) {
			this.crossfadingDuration = Integer.parseInt((prefs.getString("pref_key_crossfading_duration", "5")));
		}
		if (key.equals("pref_key_pitch_value")) {
			this.pitchValue = Integer.parseInt(prefs.getString("pref_key_pitch_value", "5"));
		}

		// PEDOMETER 
		if (key.equals("pref_key_steplength")) {
			this.stepLength = Float.parseFloat((prefs.getString("pref_key_steplength", "120.0")));
		}
		if(key.equals("pref_key_autostart_pedometer")){
			this.autostartPedometer = (prefs.getBoolean("pref_key_autostart_pedometer", true));
		}
		if (key.equals("pref_key_minimum_playbacktime")) {
			this.minimumPlaybackTime = Integer.parseInt((prefs.getString("pref_key_minimum_playbacktime", "30")));
		}
	}

	// ----------------------- SETTERS / GETTERS ------------------------------
	
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

	public boolean isAutostartPedometer() {
		return autostartPedometer;
	}

	public void setAutostartPedometer(boolean autostartPedometer) {
		this.autostartPedometer = autostartPedometer;
	}

	public int getMinimumPlaybackTime() {
		return minimumPlaybackTime;
	}

	public void setMinimumPlaybackTime(int minimumPlaybackTime) {
		this.minimumPlaybackTime = minimumPlaybackTime;
	}

	public float getStepLength() {
		return stepLength;
	}

	public void setStepLength(float stepLength) {
		this.stepLength = stepLength;
	}

	public boolean isCrossfadingActive() {
		return isCrossfadingActive;
	}

	public void setCrossfadingActive(boolean isCrossfadingActive) {
		this.isCrossfadingActive = isCrossfadingActive;
	}

	public int getCrossfadingDuration() {
		return crossfadingDuration;
	}

	public void setCrossfadingDuration(int crossfadingDuration) {
		this.crossfadingDuration = crossfadingDuration;
	}

	public int getPitchValue() {
		return pitchValue;
	}

	public void setPitchValue(int pitchValue) {
		this.pitchValue = pitchValue;
	}

	
}
