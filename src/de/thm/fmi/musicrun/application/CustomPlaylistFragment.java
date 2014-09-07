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

public class CustomPlaylistFragment extends Fragment {

	// Playlist
	PlaylistController plc;
	ListView playlistView, list;
	
	String[] titles, artists;
	
	// DEBUG
	private static final String TAG = MainActivity.class.getName();
	private static final boolean D = true;
	
	// ------------------------------------------------------------------------

	public CustomPlaylistFragment(){

	}

	// -----------------------------------------------------------------------
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.fragment_playlist, container, false);
		
		if(D) Log.i(TAG, "CustomPlaylistFragment onCreate()");
		
		// instantiate PlaylistController
		this.plc = new PlaylistController(this, getActivity());
		
		
		
		//get titles and artists lists
		this.titles = this.plc.getTitles();
		this.artists = this.plc.getArtists();
		
		// set PlayListAdapter
		PlaylistAdapter adapter = new PlaylistAdapter(getActivity(), this.titles, this.artists);
		
		list = (ListView)view.findViewById(R.id.playlistView);
		list.setAdapter(adapter);
		
		// set list item listener
		list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
				Toast.makeText(getActivity(), "You Clicked at " +titles[+ position] +" - "+ artists[+ position], Toast.LENGTH_SHORT).show();
			}
		});

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
}
