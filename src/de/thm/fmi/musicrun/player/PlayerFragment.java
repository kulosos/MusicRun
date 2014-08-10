package de.thm.fmi.musicrun.player;

import java.io.File;

import de.thm.fmi.musicrun.R;
import de.thm.fmi.musicrun.application.MainActivity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.app.Fragment;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class PlayerFragment extends Fragment implements OnSharedPreferenceChangeListener {
	
	// DEBUG
	private static final String TAG = MainActivity.class.getName();
	private static final boolean D = true;

	// Buttons
	private Button btnPlay, btnStop;
	
	// MediaPlayer
	private MediaPlayer mediaPlayer;
	private boolean isPlaying;
	
	// Preferences
	private SharedPreferences prefs;
	private String musicFilepath = "";
	
	// ------------------------------------------------------------------------

	public PlayerFragment(){

	}

	// -----------------------------------------------------------------------
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		if(D) Log.i(TAG, "PlayerFragment onCreate()");	
		
		// get the fragment view
		View view = inflater.inflate(R.layout.fragment_player, container, false);


		// play button on fragment and add listener
		this.btnPlay = (Button) view.findViewById(R.id.btn_play);
		this.btnPlay.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(D) Log.i(TAG, "BUTTON START CLICKED");
				playMusic();
			}
		}); 
		
		// play button on fragment and add listener
		this.btnStop = (Button) view.findViewById(R.id.btn_stop);
		this.btnStop.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(D) Log.i(TAG, "BUTTON STOP CLICKED");
				stopMusic();
			}
		}); 


		// PREFERENCES
	    this.prefs = PreferenceManager.getDefaultSharedPreferences(this.getActivity());
        // register preference change listener
        prefs.registerOnSharedPreferenceChangeListener(this);
        // and set remembered preferences
        this.musicFilepath = (prefs.getString("pref_key_musicfilepath", "/storage/extSdCard/"));
		
		return view;
	}

	// ------------------------------------------------------------------------
	
	@Override
	public void onResume(){
		super.onResume();
	}
	
	// ------------------------------------------------------------------------

	@Override
	public void onPause(){
		super.onPause();
	}
	
	// ------------------------------------------------------------------------

	private void playMusic(){

		this.isPlaying = true;
		
		// check for external storage isReadable
		if(this.isExternalStorageReadable()){
			
			if(D) Log.i(TAG, "EXTERNAL STORAGE IS READABLE");


			String fileName = "Boss.mp3";

//			String filePath = Environment.getExternalStorageDirectory().getAbsolutePath().toString() + File.separator + "SuperMarioSounds" + File.separator + fileName;
			String filePath = this.musicFilepath + fileName; 
			if(D) Log.d(TAG, "MUSIC FILE PATH: " +  filePath);
			

//			File f = new File(filePath);
//			if(f.exists()) {
//				if(D) Log.i(TAG, "FILE EXISTS");
//			}else{
//				if(D) Log.w(TAG, "FILE DOESN'T EXISTS");
//			}

			this.mediaPlayer = new  MediaPlayer();
			
			try {
				this.mediaPlayer.setDataSource(filePath);
			} catch (Exception e) {
				e.printStackTrace();
				if(D) Log.e(TAG, e.toString());
			}

			try {
				this.mediaPlayer.prepare();
			} catch (Exception e) {
				e.printStackTrace();
				if(D) Log.e(TAG, e.toString());
			} 

			this.mediaPlayer.start();

		}
		else{
			if(D) Log.i(TAG, "EXTERNAL STORAGE IS NOT READABLE");
		}
	}
	
	// ------------------------------------------------------------------------
	
	private void stopMusic() {
		
		if(this.isPlaying){
			
			this.mediaPlayer.stop();
			
			this.isPlaying = false;
		}
	}
	
	
	// ------------------------------------------------------------------------
	
	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		
		if (key.equals("pref_key_musicfilepath")) {
            this.musicFilepath = (prefs.getString("pref_key_musicfilepath", "/mnt/external_sd/"));
        }
		
	}
	
	// ------------------------------------------------------------------------
	
	/* Checks if external storage is available for read and write */
	public boolean isExternalStorageWritable() {
	    String state = Environment.getExternalStorageState();
	    if (Environment.MEDIA_MOUNTED.equals(state)) {
	        return true;
	    }
	    return false;
	}

	/* Checks if external storage is available to at least read */
	public boolean isExternalStorageReadable() {
	    String state = Environment.getExternalStorageState();
	    if (Environment.MEDIA_MOUNTED.equals(state) ||
	        Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
	        return true;
	    }
	    return false;
	}
	
	
}
