package de.thm.fmi.musicrun.application;

import de.thm.fmi.musicrun.R;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class SettingsFragment extends Fragment {

	private TextView tvCount;

	// ------------------------------------------------------------------------

	public SettingsFragment(){

//		tvCount = (TextView) getView().findViewById(R.id.textView1);

	}

	// -----------------------------------------------------------------------
	
	@Override
	public View onCreateView(LayoutInflater inflater,
			ViewGroup container, Bundle savedInstanceState) {
		/**
		 * Inflate the layout for this fragment
		 */
		return inflater.inflate(
				R.layout.fragment_settings, container, false);
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
