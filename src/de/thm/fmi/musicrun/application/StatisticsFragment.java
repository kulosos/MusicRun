package de.thm.fmi.musicrun.application;

import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import de.thm.fmi.musicrun.R;

public class StatisticsFragment extends Fragment {

	private LinearLayout graphLayout;
	
	// DEBUG
	private static final String TAG = MainActivity.class.getName();
	private static final boolean D = false;
	
	// ------------------------------------------------------------------------
	
	public StatisticsFragment(){	
	}
	
	// ------------------------------------------------------------------------
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		if(D) Log.i(TAG, "MapsFragment onCreate()");
		
		// get the fragment view
		View view = inflater.inflate(R.layout.fragment_statistics, container, false);
		
		StatisticsController.initInstance(this, this.getActivity());
	
		// Get GUI Elements
		this.graphLayout = (LinearLayout) view.findViewById(R.id.graph_layout);
		
		StatisticsController.getInstance().createGraphView();
		
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
	
	// ------------------- SETTERS / GETTERS ----------------------------------
	
	public LinearLayout getGraphLayout() {
		return graphLayout;
	}

	public void setGraphLayout(LinearLayout graphLayout) {
		this.graphLayout = graphLayout;
	}
	
}
