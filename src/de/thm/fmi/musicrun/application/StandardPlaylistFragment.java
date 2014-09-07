package de.thm.fmi.musicrun.application;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.app.ListFragment;

public  class StandardPlaylistFragment extends ListFragment {  

	//Controller
	private PlaylistController plc;
	
	// DEBUG
	private static final String TAG = MainActivity.class.getName();
	private static final boolean D = true;
	
	// ------------------------------------------------------------------------
	
	@Override  
	public void onListItemClick(ListView l, View v, int position, long id) {  

		plc.playSelectedTrack(id);
	}  

	// ------------------------------------------------------------------------
	
	@Override  
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) { 

		// instantiate PlaylistController
//		this.plc = new PlaylistController(this, getActivity());
		
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(inflater.getContext(), android.R.layout.simple_list_item_1, this.plc.getTitles());  
		setListAdapter(adapter);  

		return super.onCreateView(inflater, container, savedInstanceState);  
	}  

}  
