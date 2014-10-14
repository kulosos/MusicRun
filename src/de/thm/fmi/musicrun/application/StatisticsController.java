package de.thm.fmi.musicrun.application;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.LineGraphView;
import com.jjoe64.graphview.GraphView.GraphViewData;

import android.content.Context;
import android.graphics.Color;

public class StatisticsController {
	
	private static StatisticsController instance;
	
	private StatisticsFragment statsFragment;
	private Context context;
	
	// Graph
	GraphViewSeries graphSeries;
	GraphView graphView;
	
	// DEBUG
	private static final String TAG = MainActivity.class.getName();
	private static final boolean D = false;
	
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
		this.graphSeries = new GraphViewSeries(new GraphViewData[] {
//		    new GraphViewData(1, 100.0d)
//		    , new GraphViewData(2, 90.5d)
		});
		
		this.graphView = new LineGraphView(this.context , "steps/min intervall history");
		
		// set styles
		this.graphView.getGraphViewStyle().setGridColor(Color.DKGRAY);
		this.graphView.getGraphViewStyle().setHorizontalLabelsColor(Color.WHITE);
		this.graphView.getGraphViewStyle().setVerticalLabelsColor(Color.WHITE);
		this.graphView.getGraphViewStyle().setTextSize(14.0f);
		this.graphView.getGraphViewStyle().setNumHorizontalLabels(0);
		this.graphView.getGraphViewStyle().setNumVerticalLabels(20);
//		this.graphView.getGraphViewStyle().setVerticalLabelsWidth(200);
		
		this.graphView.addSeries(this.graphSeries);
		
		this.statsFragment.getGraphLayout().addView(graphView);
	}
	
	// -------------------- SETTERS / GETTERS ---------------------------------

	public GraphViewSeries getGraphSeries() {
		return graphSeries;
	}
	
	public void setGraphSeries(GraphViewSeries graphSeries) {
		this.graphSeries = graphSeries;
	}

	public GraphView getGraphView() {
		return graphView;
	}

	public void setGraphView(GraphView graphView) {
		this.graphView = graphView;
	}

	
}
