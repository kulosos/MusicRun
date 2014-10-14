package de.thm.fmi.musicrun.pedometer;

import java.util.ArrayList;
import java.util.List;

import com.jjoe64.graphview.GraphView.GraphViewData;

import de.thm.fmi.musicrun.R;
import de.thm.fmi.musicrun.application.MainActivity;
import de.thm.fmi.musicrun.application.PreferencesManager;
import de.thm.fmi.musicrun.application.StatisticsController;
import de.thm.fmi.musicrun.application.TypefaceManager;
import de.thm.fmi.musicrun.application.TypefaceManager.FontStyle;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Handler;
import android.util.Log;

public class PedometerController implements IStepDetectionObserver {

	private static PedometerController instance;

	// Fragment
	public PedometerFragment pedometerFragment;
	private Context context;
	private Activity activity;

	// StepDetection
	private StepDetector stepDetector;
	private int stepcount = 0;	
	private int stepcountAPI19 = 0;
	private int stepcountPerIntervall = 0;
	private int totalTime;
	private Float stepFrequencyPerMinute;
	private Float stepFrequencyPerMinuteLastIntervall;
	public boolean isRunning = false;
	private Float distance;
	private int intervallTimer = 0;
	private List<Integer> stepsPerIntervallHistory = new ArrayList<Integer>();
	private int graphValueCounter = 3;

	// Runnable Thread / Timer
	private Handler customHandlerPerSecond;
	StopWatch sw = new StopWatch();
	public boolean stepDetecionIsPaused = true;

	// TypefaceManager
	TypefaceManager typefaceMgr;
	
	// DEBUG
	private static final String TAG = MainActivity.class.getName();
	private static final boolean D = false;

	// ------------------------------------------------------------------------

	private PedometerController(Activity activity, PedometerFragment pmf){
		this.activity = activity;
		this.context = activity;
		this.pedometerFragment = pmf;
		
		// TODO: 
		// find a better way, this is not a good implementation for bold fonts here
		// set seperate Typeface for Buttons (a little bit wider)
	    this.typefaceMgr = new TypefaceManager(this.activity);
		Typeface fontBold = Typeface.createFromAsset(this.context.getAssets(), this.typefaceMgr.getTypeface(FontStyle.BOLD));
		this.pedometerFragment.getBtnStart().setTypeface(fontBold);
		this.pedometerFragment.getBtnReset().setTypeface(fontBold);
	}
	
	// ------------------- SINGLETON METHODS ----------------------------------

	public static void initInstance(Activity activity, PedometerFragment pmf){
		if(instance == null){
			instance = new PedometerController(activity, pmf);
		}
	}

	public static PedometerController getInstance(){
		return instance;
	}

	// ------------------------------------------------------------------------

	public void startStepDetection(){

		if(this.stepDetecionIsPaused){
			this.stepDetecionIsPaused = false;

			// time interval, needed for average step calculation
			if(customHandlerPerSecond==null){
				this.customHandlerPerSecond = new Handler();
				this.customHandlerPerSecond.postDelayed(updateTimerPerSecond, 0);
			}

			// start the stopWatch
			this.sw.resume();

			// Initialize StepDetector
			if(stepDetector==null){
				stepDetector = new StepDetector(this.context.getSystemService(Context.SENSOR_SERVICE));
			}

			// Start StepDetection
			this.stepDetector.setActivityRunning(true);
			this.stepDetector.registerSensorManager();
			this.isRunning = true;

			// attach Observer to this activity
			this.stepDetector.attachObserver(this);

			// change the button label to stop
			this.pedometerFragment.getBtnStart().setText(this.context.getResources().getString(R.string.btn_pedometer_pause));

			Resources res = this.context.getResources();
			int color = res.getColor(R.color.red);
			pedometerFragment.getBtnStart().setTextColor(color);

			// deactivate when api level < 19
			if(this.stepDetector.getApiLevel()<19){
				this.pedometerFragment.getTvStepsTotalSinceStart().setText("n.a.");
			}
		}
	}

	// ------------------------------------------------------------------------

	public void pauseStepDetection(){

		if(!this.stepDetecionIsPaused){
			this.stepDetecionIsPaused = true;

			// start the stopWatch
			this.sw.pause();

			// attach Observer to this activity
			this.stepDetector.detachObserver(this);
			this.stepDetector.setActivityRunning(false);
			this.isRunning = false;

			// change the button label to start
			this.pedometerFragment.getBtnStart().setText(this.context.getResources().getString(R.string.btn_pedometer_start));

			Resources res = this.context.getResources();
			int color = res.getColor(R.color.systemLightBlue);
			this.pedometerFragment.getBtnStart().setTextColor(color);
		}
	}

	// ------------------------------------------------------------------------

	public void resetStepDetection(){

		if(this.totalTime > 0){

			this.pauseStepDetection();

			// clear the stopWatch
			this.sw.clear();

			this.totalTime = 0;
			this.stepcount = 0;
			this.stepFrequencyPerMinute = 0.0f;
			this.stepcountAPI19 = 0;

			// clear all textViews
			this.pedometerFragment.getTvStepDetectionDuration().setText("00:00:00");
			this.pedometerFragment.getTvStepsAverage().setText("0");
			this.pedometerFragment.getTvStepsPerMinute().setText("0");
			this.pedometerFragment.getTvStepsTotal().setText("0");
			this.pedometerFragment.getTvStepsTotalSinceStart().setText("0");
			this.pedometerFragment.getTvCurrentIntervall().setText(Integer.toString(PreferencesManager.getInstance().getMinimumPlaybackTime()));
			this.pedometerFragment.getTvStepsLastIntervall().setText("0");
			
			// statistics graph
			this.graphValueCounter = 0;
//			StatisticsController.getInstance().getGraphView().removeSeries(StatisticsController.getInstance().getGraphSeries());
		}
	}

	// ------------------------------------------------------------------------

	// Step Detection Observer Update
	@Override
	public void update() {

		if(!stepDetecionIsPaused){
			// count the recognized steps
			this.stepcount += 1;
			this.stepcountPerIntervall += 1;
			//			if(D) Log.i(TAG, "Stepcount: " + this.stepcount); // DEBUG
			this.pedometerFragment.getTvStepsTotal().setText(Integer.toString(this.stepcount));

			// calculate run distance
			this.distance = (this.stepcount * (PreferencesManager.getInstance().getStepLength()*0.01f)) / 1000;
			this.pedometerFragment.getTvStepsPerMinute().setText(String.format("%.3f", this.distance));
		}
	}

	// ------------------------------------------------------------------------

	// Step Detection Observer Update
	@Override
	public void updateForAPILevel19() {

		if(!stepDetecionIsPaused){
			// count the recognized steps
			this.stepcountAPI19 += 1;
			//			if(D) Log.i(TAG, "Stepcount: " + this.stepcount); // DEBUG
			this.pedometerFragment.getTvStepsTotalSinceStart().setText(Integer.toString(this.stepcountAPI19));
		}
	}

	// ------------------------------------------------------------------------

	// seperate thread for timer
	private Runnable updateTimerPerSecond = new Runnable()
	{
		public void run()
		{
			int intervallTime = 1000; //milliseconds
			intervallTimer += intervallTime/1000;
			
			// calculate step frequency f=n/t
			stepFrequencyPerMinute = (float)((float)stepcount / (float)totalTime) * 60f; // for total time
			stepFrequencyPerMinuteLastIntervall =  (float)((float)stepcountPerIntervall / (float)PreferencesManager.getInstance().getMinimumPlaybackTime()) * 60f;
			
			// sets the stopWatch, timer textview and counts the totaltime (in stopWach method)
			setStopWatch();
			pedometerFragment.getTvStepsAverage().setText(Integer.toString(((int)Math.round(stepFrequencyPerMinute))));

			customHandlerPerSecond.postDelayed(this, intervallTime);
			
			// TODO
			// checks every second, when its not neccessary, too
			// check for changes in settings
			// show configured intervall time (from preferences minPlaybackTime)
			pedometerFragment.getTvCurrentIntervall().setText(Integer.toString(PreferencesManager.getInstance().getMinimumPlaybackTime()));

			if(PreferencesManager.getInstance().getMinimumPlaybackTime() == intervallTimer){
				
				int lastValue = (int)Math.round(stepFrequencyPerMinuteLastIntervall);
				pedometerFragment.getTvStepsLastIntervall().setText(Integer.toString((lastValue)));
				stepsPerIntervallHistory.add((int)Math.round(lastValue));
				
				graphValueCounter += 1;
				
				// append new data to graphView and redraw
				StatisticsController.getInstance().getGraphSeries().appendData(new GraphViewData(graphValueCounter, Math.round(lastValue)), false, 500);
				StatisticsController.getInstance().getGraphView().redrawAll();
				
				// reset values
				intervallTimer = 0;
				stepcountPerIntervall = 0;
				stepFrequencyPerMinuteLastIntervall = 0.0f;
			}
		}
	};

	// ------------------------------------------------------------------------

	public void setStopWatch() {

		//		if(D) Log.i(TAG, "StopWatch: " + sw.getElapsedTimeHour() + ":" + sw.getElapsedTimeMin() + ":" +  sw.getElapsedTimeSecs() + ":" + sw.getElapsedTimeMili());

		if(!stepDetecionIsPaused){
			String hours = "", minutes = "", seconds = "";

			if(sw.getElapsedTimeHour()<10){
				hours = "0" + Long.toString(sw.getElapsedTimeHour());}
			else{ hours = Long.toString(sw.getElapsedTimeHour()); }

			if(sw.getElapsedTimeMin()<10){
				minutes = "0" + Long.toString(sw.getElapsedTimeMin());	}
			else{ minutes = Long.toString(sw.getElapsedTimeMin());}

			if(sw.getElapsedTimeSecs()<10){
				seconds = "0" + Long.toString(sw.getElapsedTimeSecs());}
			else{seconds = Long.toString(sw.getElapsedTimeSecs());}

			// set the textView
			this.pedometerFragment.getTvStepDetectionDuration().setText(hours + ":" + minutes + ":" + seconds);

			// count the total seconds
			this.totalTime += 1;
			//			this.tvStepsTotalSinceStart.setText(Float.toString(totalTime));
		}
	}
	
	// -------------------- SETTER / GETTER -----------------------------------
	
	public List<Integer> getStepsPerIntervallHistory() {
		return stepsPerIntervallHistory;
	}

	public void setStepsPerIntervallHistory(List<Integer> stepsPerIntervallHistory) {
		this.stepsPerIntervallHistory = stepsPerIntervallHistory;
	}

	
}
