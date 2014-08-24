package de.thm.fmi.musicrun.application;

import de.thm.fmi.musicrun.R;
import de.thm.fmi.musicrun.application.MainActivity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;
import android.util.Log;

public class SettingsFragment extends PreferenceFragment implements OnSharedPreferenceChangeListener {

	// Player
	PlayerController pc;
//	ProgressDialog progress;
	
	// DEBUG
	private static final String TAG = MainActivity.class.getName();
	private static final boolean D = false;
	
	// ------------------------------------------------------------------------
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Load the preferences from an XML resource
		addPreferencesFromResource(R.xml.preferences);

		// init dialog for music folder scan
		this.musicFolderScanDialog();
		
		this.pc = new PlayerController(this.getActivity());

	}
	
	// ------------------------------------------------------------------------

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		// TODO Auto-generated method stub

	}

	// -----------------------------------------------------------------------
	
	private void musicFolderScanDialog(){

		Preference dialogPreference = (Preference) getPreferenceScreen().findPreference("pref_key_libraryscan");
		
		dialogPreference.setOnPreferenceClickListener(new OnPreferenceClickListener() {
		        public boolean onPreferenceClick(Preference preference) {
		           

		        	AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

		        	builder.setMessage(R.string.settings_label_musicplayer_libraryscan_desc)
		        	       .setTitle(R.string.settings_label_musicplayer_libraryscan);

		        	
		        	builder.setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
		                public void onClick(DialogInterface dialog, int id) {
		                	
		                	pc.scanMusicFolder();
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
	}
	
	// ------------------------------------------------------------------------
	
	private void musicFolderScanProgressBarDialog(){
		

		int filesInFolder = this.pc.getFileList().length;
		
//		this.pc.scanMusicFolder();
		
//
//		this.progress = new ProgressDialog(getActivity());
//
//		progress.setMessage("Scanning music folder");
//		progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
//		progress.setIndeterminate(true);
//		progress.show();
//
//		final int totalProgressTime = filesInFolder;
//
//		final Thread t = new Thread(){
//
//			@Override
//			public void run(){
//
//				int jumpTime = 0;
//				while(jumpTime < totalProgressTime){
//					try {
////						sleep(200);
//						jumpTime += 5;
//						progress.setProgress(jumpTime);
//					} catch (InterruptedException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//
//				}
//
//			}
//		};
//		t.start();
	}

}
