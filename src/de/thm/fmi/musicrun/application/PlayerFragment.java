package de.thm.fmi.musicrun.application;

import de.thm.fmi.musicrun.R;
//import de.thm.fmi.musicrun.maps.MapsFragment;
//import de.thm.fmi.musicrun.pedometer.PedometerFragment;
//import android.app.Activity;
//import android.app.DialogFragment;
//import android.app.FragmentManager;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
//import android.support.v4.app.FragmentActivity;
//import android.support.v4.app.FragmentManager;
//import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
//import android.widget.Toast;

public class PlayerFragment extends Fragment  {
	
	// DEBUG
	private static final String TAG = MainActivity.class.getName();
	private static final boolean D = true;

	// Buttons
	private ImageView btnPlay, btnStop, btnPause, btnNext, btnLast, btnList, btnTrackImage;
	
	// MediaPlayer
	private MediaPlayer mediaPlayer;
	private PlayerController pc;
	
	// Database
//	DatabaseManager db;
	
	// Preferences
	PreferencesManager prefsManager;
	
	// ------------------------------------------------------------------------

	public PlayerFragment(){

	}

	// -----------------------------------------------------------------------
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		if(D) Log.i(TAG, "PlayerFragment onCreate()");	
		
		// get the fragment view
		View view = inflater.inflate(R.layout.fragment_player, container, false);

		// get buttons from fragment and set listeners
		view = this.initButtons(view);
		
		// MusicPlayer
		this.mediaPlayer = new MediaPlayer();
		this.pc = new PlayerController(this.getActivity(), this);
		
		// DATABASE
//		this.db = new DatabaseManager(getActivity());

		// PREFERENCES
		this.prefsManager = new PreferencesManager(this.getActivity());

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
				
	            // change Play Button to PauseIcon
	            this.btnPlay.setImageDrawable(getResources().getDrawable(R.drawable.btn_pause_white));

				String fileName = "TryHarder.mp3";

				String filePath = this.prefsManager.getMusicFilepath() + fileName; 
				//			if(D) Log.d(TAG, "MUSIC FILE PATH: " +  filePath);


				//			File f = new File(filePath);
				//			if(f.exists()) {
				//				if(D) Log.i(TAG, "FILE EXISTS");
				//			}else{
				//				if(D) Log.w(TAG, "FILE DOESN'T EXISTS");
				//			}

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
		else{
			this.pauseMusic();
		}
	}
	
	// ------------------------------------------------------------------------
	
	private void pauseMusic() {
		
		if(this.mediaPlayer.isPlaying()){
			
			// change PauseButton to PlayIcon
            this.btnPlay.setImageDrawable(getResources().getDrawable(R.drawable.btn_play_white));
            
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
	
	private View initButtons(View view){

		// play / pause button on fragment and add listener
		this.btnPlay = (ImageView) view.findViewById(R.id.btn_play);
		this.btnPlay.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(D) Log.i(TAG, "BUTTON PLAY CLICKED");
				playMusic();
			}
		}); 
		
		// last track button on fragment and add listener
		this.btnLast = (ImageView) view.findViewById(R.id.btn_last);
		this.btnLast.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(D) Log.i(TAG, "BUTTON LAST CLICKED");
			}
		}); 
		
		// next track button on fragment and add listener
		this.btnNext = (ImageView) view.findViewById(R.id.btn_next);
		this.btnNext.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(D) Log.i(TAG, "BUTTON NEXT CLICKED");
			}
		}); 
		
		// playlist button on fragment and add listener
		this.btnList = (ImageView) view.findViewById(R.id.btn_list);
		this.btnList.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(D) Log.i(TAG, "BUTTON LIST CLICKED");

				showPlaylistFragment();	
			}
		}); 
		
		// track image button on fragment and add listener
		this.btnTrackImage = (ImageView) view.findViewById(R.id.trackImage);
		this.btnTrackImage.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {	
				if(D) Log.i(TAG, "TRACK IMAGE CLICKED");
				
			}
		}); 
		
		return view;
	}
	
	// ------------------------------------------------------------------------
	
	public void showPlaylistFragment(){
		
		// Create new fragment and transaction
		PlaylistFragment newFragment = new PlaylistFragment();
		android.app.FragmentTransaction transaction = getFragmentManager().beginTransaction();
		
		// Replace whatever is in the fragment_container view with this fragment,
		// and add the transaction to the back stack
		transaction.replace(R.id.player_frame, newFragment);

		transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
		
		transaction.addToBackStack(null);
		
		// Commit the transaction
		transaction.commit();
		
		//TODO
		// this is pretty dirty coded;
		// because the replacing of the playerFragment replaces only the parent node in xml
		// not the children. so here every single child is deactivating here 
		this.btnLast.setVisibility(ImageView.INVISIBLE);
		this.btnNext.setVisibility(ImageView.INVISIBLE);
		this.btnPlay.setVisibility(ImageView.INVISIBLE);
		this.btnList.setVisibility(ImageView.INVISIBLE);
		this.btnTrackImage.setVisibility(ImageView.INVISIBLE);

	}

	// ------------------------- SETTERS / GETTERS ----------------------------
	
	public ImageView getBtnPlay() {
		return btnPlay;
	}

	public void setBtnPlay(ImageView btnPlay) {
		this.btnPlay = btnPlay;
	}

	public ImageView getBtnStop() {
		return btnStop;
	}

	public void setBtnStop(ImageView btnStop) {
		this.btnStop = btnStop;
	}

	public ImageView getBtnPause() {
		return btnPause;
	}

	public void setBtnPause(ImageView btnPause) {
		this.btnPause = btnPause;
	}

	public ImageView getBtnNext() {
		return btnNext;
	}

	public void setBtnNext(ImageView btnNext) {
		this.btnNext = btnNext;
	}

	public ImageView getBtnLast() {
		return btnLast;
	}

	public void setBtnLast(ImageView btnLast) {
		this.btnLast = btnLast;
	}

	public ImageView getBtnList() {
		return btnList;
	}

	public void setBtnList(ImageView btnList) {
		this.btnList = btnList;
	}

	public ImageView getBtnTrackImage() {
		return btnTrackImage;
	}

	public void setBtnTrackImage(ImageView btnTrackImage) {
		this.btnTrackImage = btnTrackImage;
	}

	public MediaPlayer getMediaPlayer() {
		return mediaPlayer;
	}

	public void setMediaPlayer(MediaPlayer mediaPlayer) {
		this.mediaPlayer = mediaPlayer;
	}
	
	// ------------------------------------------------------------------------
	
}
