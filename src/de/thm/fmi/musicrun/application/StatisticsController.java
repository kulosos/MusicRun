package de.thm.fmi.musicrun.application;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.LineGraphView;
import com.jjoe64.graphview.GraphView.GraphViewData;
import android.content.Context;

public class StatisticsController {
	
	private static StatisticsController instance;
	
	private StatisticsFragment statsFragment;
	private Context context;
	
	// DEBUG
	private static final String TAG = MainActivity.class.getName();
	private static final boolean D = true;
	
	// ------------------------------------------------------------------------
	
	private StatisticsController(StatisticsFragment statisticsFragment, Context context){
		
		this.statsFragment = statisticsFragment;
		this.context = context;
	}
	
	// ------------------- SINGLETON METHODS ----------------------------------
	
	public static void initInstance(StatisticsFragment statsFragment, Context context){
		if(instance == null){
			instance = new StatisticsController(statsFragment, context);
		}
	}

	public static StatisticsController getInstance(){
		return instance;
	}

	// ------------------------------------------------------------------------
	
	public void createGraphView(){
		
		// init example series data
		GraphViewSeries exampleSeries = new GraphViewSeries(new GraphViewData[] {
		    new GraphViewData(1, 2.0d)
		    , new GraphViewData(2, 1.5d)
		    , new GraphViewData(3, 2.5d)
		    , new GraphViewData(4, 1.0d)
		});
		
		GraphView graphView = new LineGraphView(this.context , "GraphViewDemo");
		graphView.addSeries(exampleSeries);
		
		this.statsFragment.getGraphLayout().addView(graphView);
	}

}
