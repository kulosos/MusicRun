package de.thm.fmi.musicrun.maps;

import de.thm.fmi.musicrun.R;
import de.thm.fmi.musicrun.application.MainActivity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MapsFragment extends Fragment {

	// DEBUG
	private static final String TAG = MainActivity.class.getName();
	private static final boolean D = false;

	// ------------------------------------------------------------------------

	public MapsFragment(){

	}

	// -----------------------------------------------------------------------
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		if(D) Log.i(TAG, "MapsFragment onCreate()");
		/**
		 * Inflate the layout for this fragment
		 */
		return inflater.inflate(R.layout.fragment_maps, container, false);
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
