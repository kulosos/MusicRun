package de.thm.fmi.musicrun.player;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import wseemann.media.FFmpegMediaMetadataRetriever;
import de.thm.fmi.musicrun.R;
import de.thm.fmi.musicrun.application.MainActivity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.graphics.Bitmap;
import android.app.Fragment;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

public class PlayerFragment extends Fragment implements OnSharedPreferenceChangeListener {
	
	// DEBUG
	private static final String TAG = MainActivity.class.getName();
	private static final boolean D = true;

	// Buttons
	private ImageView btnPlay, btnStop, btnPause, btnNext, btnLast, btnList;
	
	// MediaPlayer
	private MediaPlayer mediaPlayer;
	
	// Database
	DatabaseManager db;
	
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
		this.btnPlay = (ImageView) view.findViewById(R.id.btn_play);
		this.btnPlay.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(D) Log.i(TAG, "BUTTON START CLICKED");
				playMusic();
			}
		}); 
		
		// stop button on fragment and add listener
		this.btnStop = (ImageView) view.findViewById(R.id.btn_stop);
		this.btnStop.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(D) Log.i(TAG, "BUTTON STOP CLICKED");
				stopMusic();
//				getPlayList();
			}
		}); 
		
		// pause button on fragment and add listener
		this.btnPause = (ImageView) view.findViewById(R.id.btn_pause);
		this.btnPause.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(D) Log.i(TAG, "BUTTON STOP CLICKED");
				pauseMusic();
			}
		}); 
		
		
		// DATABASE
		this.db = new DatabaseManager(getActivity());

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
		
		if(!this.mediaPlayer.isPlaying()){
			// check for external storage isReadable
			if(this.isExternalStorageReadable()){

				String fileName = "TryHarder.mp3";

				String filePath = this.musicFilepath + fileName; 
				//			if(D) Log.d(TAG, "MUSIC FILE PATH: " +  filePath);


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
				Log.e(TAG, "EXTERNAL STORAGE IS NOT READABLE");
			}
		}
	}
	
	// ------------------------------------------------------------------------
	
	private void pauseMusic() {
		
		if(this.mediaPlayer.isPlaying()){
			this.mediaPlayer.pause();
		}
		else{
			this.mediaPlayer.release();
		}
	}
	
	// ------------------------------------------------------------------------
	
	private void stopMusic() {
		
		if(this.mediaPlayer.isPlaying()){
			this.mediaPlayer.stop();
		}
	}
	
	// ------------------------------------------------------------------------
	
	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		
		if (key.equals("pref_key_musicfilepath")) {
            this.musicFilepath = (prefs.getString("pref_key_musicfilepath", "/storage/extSd/"));
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

	// ------------------------------------------------------------------------
	
	/* Checks if external storage is available to at least read */
	public boolean isExternalStorageReadable() {
	    String state = Environment.getExternalStorageState();
	    if (Environment.MEDIA_MOUNTED.equals(state) ||
	        Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
	        return true;
	    }
	    return false;
	}
	
	// ------------------------------------------------------------------------
	
	private List<String> getPlayList(){

		List<String> playlist = new ArrayList<String>();

		File file = new File(this.musicFilepath) ;       
		File list[] = file.listFiles();

		for(int i=0; i< list.length; i++){
			playlist.add(list[i].getName());
//			if(D) Log.d(TAG, "LIST" + i + " :" + list[i].getName());
			
			FFmpegMediaMetadataRetriever mmr = new FFmpegMediaMetadataRetriever();
			
			mmr.setDataSource(this.musicFilepath + list[i].getName());

			String title = mmr.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_TITLE);
			String artist = mmr.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_ARTIST);
			String album = mmr.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_ALBUM);
			String year = mmr.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_DATE);
			int bpm = 120;
			String category = "category";
			String mimetype = mmr.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_ENCODER);

			Bitmap b = mmr.getFrameAtTime(2000000, FFmpegMediaMetadataRetriever.OPTION_CLOSEST); // frame at 2 seconds
			byte [] artwork = mmr.getEmbeddedPicture();

			mmr.release();
			
			
//			MediaMetadataRetriever mmr = new MediaMetadataRetriever();
//			mmr.setDataSource(this.musicFilepath + list[i].getName());
//
//			String title = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
//			String artist = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
//			String album = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
//			String year = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_YEAR);
//			int bpm = 120;
//			String category = "category";
//			String mimetype = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_MIMETYPE);
			
			db.addTrack(new Track(i, title, artist, album, year, bpm, category, mimetype));
		}
		
		
		
//		if(D) Log.d(TAG, "ID3-TAG: Artist: " + artistName + ", Title: " + titleName + ", Album: " + albumName + ", Mimetype: " + bpm);

		
//		db.getTrack(23);
//		if(D) Log.i(TAG, "########################################################");
		
//		db.getAllTracks();
		
		
		return playlist;
	}
}
