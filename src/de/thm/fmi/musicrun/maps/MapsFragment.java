package de.thm.fmi.musicrun.maps;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.LineGraphView;
import de.thm.fmi.musicrun.R;
import de.thm.fmi.musicrun.application.MainActivity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

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

		// get the fragment view
		View view = inflater.inflate(R.layout.fragment_maps, container, false);
		
		// init example series data
		GraphViewSeries exampleSeries = new GraphViewSeries(new GraphViewData[] {
		    new GraphViewData(1, 2.0d)
		    , new GraphViewData(2, 1.5d)
		    , new GraphViewData(3, 2.5d)
		    , new GraphViewData(4, 1.0d)
		});
		 
		GraphView graphView = new LineGraphView(getActivity() , "GraphViewDemo");
		graphView.addSeries(exampleSeries);
		 
		LinearLayout layout = (LinearLayout) view.findViewById(R.id.graph_layout);
		layout.addView(graphView);
		
		
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
