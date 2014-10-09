package de.thm.fmi.musicrun.application;

import de.thm.fmi.musicrun.R;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class PlayerFragment extends Fragment {

	// GUI Elements
	private ImageView btnPlay, btnStop, btnPause, btnNext, btnLast, btnTrackImage, btnPitchPlus, btnPitchMinus;
	private TextView labelArtist, labelTitle, labelDuration, labelBPM, labelPitch;
	private SeekBar songProgressSeekBar;
	
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
		view = this.initGUIElements(view);

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
	
	private View initGUIElements(View view){

		// play / pause button on fragment and add listener
		this.btnPlay = (ImageView) view.findViewById(R.id.btn_play);
		this.btnPlay.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(D) Log.i(TAG, "BUTTON PLAY CLICKED");
				PlayerController.getInstance().pauseMusic();
			}
		}); 
		
		// last track button on fragment and add listener
		this.btnLast = (ImageView) view.findViewById(R.id.btn_last);
		this.btnLast.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(D) Log.i(TAG, "BUTTON LAST CLICKED");
				PlayerController.getInstance().playLastTrack();
			}
		}); 
		
		// next track button on fragment and add listener
		this.btnNext = (ImageView) view.findViewById(R.id.btn_next);
		this.btnNext.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(D) Log.i(TAG, "BUTTON NEXT CLICKED");
				PlayerController.getInstance().playNextTrack();
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
		
		// Pitch Button Plus
		this.btnPitchPlus = (ImageView) view.findViewById(R.id.btn_pitch_plus);
		this.btnPitchPlus.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {	
				if(D) Log.i(TAG, "BUTTON PITCH PLUS CLICKED");
				PlayerController.getInstance().pitchBPM(PreferencesManager.getInstance().getPitchValue());
			}
		}); 
		
		// Pitch Button Minus
		this.btnPitchMinus = (ImageView) view.findViewById(R.id.btn_ptich_minus);
		this.btnPitchMinus.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {	
				if(D) Log.i(TAG, "BUTTON PITCH MINUS CLICKED");
				PlayerController.getInstance().pitchBPM(PreferencesManager.getInstance().getPitchValue()*(-1));
			}
		}); 
		
		// get seekbar for song progress and setListener
		this.songProgressSeekBar = (SeekBar) view.findViewById(R.id.songProgressSeekbar);
		this.songProgressSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			int progressChanged = 0;
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser){
				if(fromUser){
					progressChanged = progress;	
				}
			}
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// seek to position
				PlayerController.getInstance().setCurrentSongPlaybackPosition(progressChanged);
				PlayerController.getInstance().updateSeekbarPosition();	
			}
		});
		
		
		// Getting TextViews
		this.labelTitle = (TextView) view.findViewById(R.id.label_currentTitle);
		this.labelArtist = (TextView) view.findViewById(R.id.label_currentArtist);
		this.labelDuration = (TextView) view.findViewById(R.id.label_current_duration);
		this.labelBPM = (TextView) view.findViewById(R.id.label_current_bpm);
		this.labelPitch = (TextView) view.findViewById(R.id.label_pitchfactor);
		
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

	public ImageView getBtnTrackImage() {
		return btnTrackImage;
	}

	public void setBtnTrackImage(ImageView btnTrackImage) {
		this.btnTrackImage = btnTrackImage;
	}

	public TextView getLabelArtist() {
		return labelArtist;
	}

	public void setLabelArtist(TextView labelArtist) {
		this.labelArtist = labelArtist;
	}

	public TextView getLabelTitle() {
		return labelTitle;
	}

	public void setLabelTitle(TextView labelTitle) {
		this.labelTitle = labelTitle;
	}

	public SeekBar getSongProgressSeekBar() {
		return songProgressSeekBar;
	}

	public void setSongProgressSeekBar(SeekBar songProgressSeekBar) {
		this.songProgressSeekBar = songProgressSeekBar;
	}

	public ImageView getBtnPitchPlus() {
		return btnPitchPlus;
	}

	public void setBtnPitchPlus(ImageView btnPitchPlus) {
		this.btnPitchPlus = btnPitchPlus;
	}

	public ImageView getBtnPitchMinus() {
		return btnPitchMinus;
	}

	public void setBtnPitchMinus(ImageView btnPitchMinus) {
		this.btnPitchMinus = btnPitchMinus;
	}

	public TextView getLabelDuration() {
		return labelDuration;
	}

	public void setLabelDuration(TextView labelDuration) {
		this.labelDuration = labelDuration;
	}

	public TextView getLabelBPM() {
		return labelBPM;
	}

	public void setLabelBPM(TextView labelBPM) {
		this.labelBPM = labelBPM;
	}

	public TextView getLabelPitch() {
		return labelPitch;
	}

	public void setLabelPitch(TextView labelPitch) {
		this.labelPitch = labelPitch;
	}
	
	
	
}
