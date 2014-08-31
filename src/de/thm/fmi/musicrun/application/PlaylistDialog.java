package de.thm.fmi.musicrun.application;

import de.thm.fmi.musicrun.R;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class PlaylistDialog extends DialogFragment {

	// ------------------------------------------------------------------------

	public PlaylistDialog() {
		// Empty constructor required for DialogFragment
	}	

	// ------------------------------------------------------------------------

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.playlist_dialogfragment, container);

		return view;
	}
}