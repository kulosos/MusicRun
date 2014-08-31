package de.thm.fmi.musicrun.application;

import java.util.List;

import de.thm.fmi.musicrun.R;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.app.ListFragment;
import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.util.Log;

public  class PlaylistFragment extends ListFragment  {  

	// DATABASE
	private DatabaseManager db;
	
	private List<Track> tracks;

	String[] titles, artists; /*= new String[] { "one", "two", "three", "four",  
			"five", "six", "seven", "eight", "nine", "ten", "eleven",  
			"twelve", "thirteen", "fourteen", "fifteen" };  */

	String[] numbers_digits = new String[] { "1", "2", "3", "4", "5", "6", "7",  
			"8", "9", "10", "11", "12", "13", "14", "15" };  

	// DEBUG
	private static final String TAG = MainActivity.class.getName();
	private static final boolean D = true;
	
	// ------------------------------------------------------------------------
	
	@Override  
	public void onListItemClick(ListView l, View v, int position, long id) {  

		new CustomToast(getActivity(), artists[(int) id] +" - "+ titles[(int) id], R.drawable.ic_launcher, 600);     
	}  

	// ------------------------------------------------------------------------
	
	@Override  
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) { 

//		if(D) Log.i(TAG, "PlaylistFragment onCreateView()");
		
		// Get TrackList form database
		this.db = new DatabaseManager(getActivity());
		this.tracks = db.getAllTracks();
		
		this.setTrackList();
		
		if(D) Log.i(TAG, "#### # # # # ## # # # # #  LENGE: " + this.tracks.size());
		
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(inflater.getContext(), android.R.layout.simple_list_item_1, titles);  
		setListAdapter(adapter);  

		new CustomToast(getActivity(),"PlaylistFragment", R.drawable.ic_launcher, 600); 
		
	
		
		return super.onCreateView(inflater, container, savedInstanceState);  
	}  

	// ------------------------------------------------------------------------
	
	private void setTrackList(){
		
		this.titles = new String[this.tracks.size()];
		this.artists = new String[this.tracks.size()];
		
		for(int i = 0; i < this.tracks.size(); i++){

			this.titles[i] = this.tracks.get(i).getTitle();
			this.artists[i] = this.tracks.get(i).getArtist();
		}

	}

}  
