package de.thm.fmi.musicrun.application;

import de.thm.fmi.musicrun.R;
import de.thm.fmi.musicrun.application.MainActivity;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
	
	// ------------------------------------------------------------------------
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Load the preferences from an XML resource
		addPreferencesFromResource(R.xml.preferences);

		// init events for preferences view
		this.settingsEvents();

	}
	
	// ------------------------------------------------------------------------

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		// TODO Auto-generated method stub

	}

	// -----------------------------------------------------------------------
	
	private void settingsEvents(){

		Preference musicFolderScanDialog = (Preference) getPreferenceScreen().findPreference("pref_key_libraryscan");

		musicFolderScanDialog.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			public boolean onPreferenceClick(Preference preference) {


				AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

				builder.setMessage(R.string.settings_label_musicplayer_libraryscan_desc)
				.setTitle(R.string.settings_label_musicplayer_libraryscan)
				.setIcon(R.drawable.ic_folderscan_blue_50);


				builder.setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						PlayerController.getInstance().scanMusicFolder();
					}
				});

				builder.setNegativeButton(R.string.btn_cancel, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {

					}
				});

				AlertDialog dialog = builder.create();
				dialog.show();

				return true;
			}
		});

		Preference dialogPreference = (Preference) getPreferenceScreen().findPreference("pref_key_libraryscan_test");

		dialogPreference.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			public boolean onPreferenceClick(Preference preference) {

				PlayerController.getInstance().getAllTracks();
				
				return true;
			}
		});
	}

}
