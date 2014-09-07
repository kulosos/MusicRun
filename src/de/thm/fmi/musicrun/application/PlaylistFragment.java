package de.thm.fmi.musicrun.application;

import de.thm.fmi.musicrun.R;
import de.thm.fmi.musicrun.application.MainActivity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

public class PlaylistFragment extends Fragment {

	// Playlist
	private ListView playlistView;
	
	// DEBUG
	private static final String TAG = MainActivity.class.getName();
	private static final boolean D = true;
	
	// ------------------------------------------------------------------------

	public PlaylistFragment(){
	}
	
	// -----------------------------------------------------------------------
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.fragment_playlist, container, false);
		
		if(D) Log.i(TAG, "CustomPlaylistFragment onCreate()");
		
		playlistView = (ListView)view.findViewById(R.id.playlistView);
		
		// instantiate PlaylistController Singleton
		PlaylistController.initInstance(this, getActivity());
		
		// get list from fragment
		playlistView = (ListView)view.findViewById(R.id.playlistView);

		PlaylistController.getInstance().initPlaylist();
		
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
	
	// ------------------------- SETTERS / GETTERS ----------------------------
	
	public ListView getPlaylistView() {
		return playlistView;
	}

	public void setPlaylistView(ListView list) {
		this.playlistView = list;
	}
	
}
