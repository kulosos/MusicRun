package de.thm.fmi.musicrun.player;

import de.thm.fmi.musicrun.R;
import de.thm.fmi.musicrun.application.MainActivity;
import de.thm.fmi.musicrun.pedometer.IStepDetectionObserver;
import de.thm.fmi.musicrun.pedometer.StepDetector;
import android.annotation.SuppressLint;
import android.content.Context;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


@SuppressLint("ValidFragment")
public class PlayerFragment extends Fragment {

	// DEBUG
		private static final String TAG = MainActivity.class.getName();
		private static final boolean D = false;
	
	// ------------------------------------------------------------------------

	public PlayerFragment(){

	}

	// -----------------------------------------------------------------------
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		if(D) Log.i(TAG, "PlayerFragment onCreate()");	
		
		/**
		 * Inflate the layout for this fragment
		 */
		return inflater.inflate(
				R.layout.fragment_main, container, false);
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
	
	
}
