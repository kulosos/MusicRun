package de.thm.fmi.musicrun.application;

import de.thm.fmi.musicrun.R;
import de.thm.fmi.musicrun.application.MainActivity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceFragment;

public class SettingsFragment extends PreferenceFragment implements OnSharedPreferenceChangeListener {

	// DEBUG
	private static final String TAG = MainActivity.class.getName();
	private static final boolean D = false;

	// -----------------------------------------------------------------------
	
	 @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);

	        // Load the preferences from an XML resource
	        addPreferencesFromResource(R.xml.preferences);
	        

	    }

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		// TODO Auto-generated method stub
		
	}
	
	// -----------------------------------------------------------------------
	
//	@Override
//	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//		
//		if(D) Log.i(TAG, "SettingsFragment onCreate()");
//		/**
//		 * Inflate the layout for this fragment
//		 */
//		return inflater.inflate(R.layout.fragment_settings, container, false);
//	}

	// ------------------------------------------------------------------------
	
//	@Override
//	public void onResume(){
//		super.onResume();
//	}
//	
//	// ------------------------------------------------------------------------
//
//	@Override
//	public void onPause(){
//		super.onPause();
//	}
}
