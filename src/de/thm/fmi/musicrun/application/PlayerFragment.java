package de.thm.fmi.musicrun.application;

import de.thm.fmi.musicrun.R;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class PlayerFragment extends Fragment  {

	// Buttons
	private ImageView btnPlay, btnStop, btnPause, btnNext, btnLast, btnList, btnTrackImage;
	
	// Preferences
	PreferencesManager prefsManager;
	
	// DEBUG
	private static final String TAG = MainActivity.class.getName();
	private static final boolean D = true;
	
	// ------------------------------------------------------------------------

	public PlayerFragment(){
	}

	// -----------------------------------------------------------------------
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		if(D) Log.i(TAG, "PlayerFragment onCreate()");	
		
		// get the fragment view
		View view = inflater.inflate(R.layout.fragment_player, container, false);

		// instantiate singleton PlayerController
		PlayerController.initInstance(getActivity(), this);
		
		// get buttons from fragment and set listeners
		view = this.initButtons(view);
		
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
	
	private View initButtons(View view){

		// play / pause button on fragment and add listener
		this.btnPlay = (ImageView) view.findViewById(R.id.btn_play);
		this.btnPlay.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(D) Log.i(TAG, "BUTTON PLAY CLICKED");
				PlayerController.getInstance().playMusic();
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

	// -------------------------- SETTERS / GETTERS ---------------------------
	
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
}
