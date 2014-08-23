package de.thm.fmi.musicrun.application;

import de.thm.fmi.musicrun.R;
import de.thm.fmi.musicrun.application.MainActivity;
import android.app.DialogFragment;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
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
	        
//	        this.setPreferencesListener();

	    }

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		// TODO Auto-generated method stub
		
	}
	
	// -----------------------------------------------------------------------
	
//	private void setPreferencesListener(){
//
//		Preference dialogPreference = (Preference) getPreferenceScreen().findPreference("dialog_preference");
//		dialogPreference.setOnPreferenceClickListener(new OnPreferenceClickListener() {
//		        public boolean onPreferenceClick(Preference preference) {
//		           
//
//				    DialogFragment newFragment = new StepLengthDialogFragment();
//				    newFragment.show(getActivity().getFragmentManager(), "missiles");
//		          
//		        	
//		            return true;
//		        }
//		    });
//	}
	

}
