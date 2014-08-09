package de.thm.fmi.musicrun.pedometer;

import de.thm.fmi.musicrun.R;
import de.thm.fmi.musicrun.application.MainActivity;
import de.thm.fmi.musicrun.application.StepLengthDialogFragment;
import de.thm.fmi.musicrun.application.TypefaceManager;
import de.thm.fmi.musicrun.application.TypefaceManager.FontStyle;
import android.annotation.SuppressLint;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

@SuppressLint("ResourceAsColor")
public class PedometerFragment extends Fragment implements IStepDetectionObserver, OnSharedPreferenceChangeListener {
	
	// TextViews
	private TextView tvStepsTotal;
	private TextView tvStepsTotalSinceStart;
	private TextView tvStepsAverage;
	private TextView tvStepsPerMinute;
	private TextView tvStepDetectionDuration;
	
	// Buttons
	private Button btnStart;
	private Button btnReset;
	
	// StepDetection
	private StepDetector stepDetector;
	private int stepcount = 0;	
	private int stepcountAPI19 = 0;
	private int totalTime;
	private Float stepFrequencyPerMinute;
	private boolean isRunning = false;
	private Float distance;
	
	// Runnable Thread / Timer
	private Handler customHandlerPerSecond;
	StopWatch sw = new StopWatch();
	private boolean isPaused = true;
	
    // TypefaceManager
    TypefaceManager typefaceMgr;
    
    // Preferences
    private SharedPreferences prefs;
    private Float stepLength = 120.0f;
    
	// DEBUG
	private static final String TAG = MainActivity.class.getName();
	private static final boolean D = true;
	
	
	// ------------------------------------------------------------------------

	public PedometerFragment(){	
	}

	// -----------------------------------------------------------------------
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	
		if(D) Log.i(TAG, "PedometerFragment onCreateView()");
		
		// get the fragment view
		View view = inflater.inflate(R.layout.fragment_pedometer, container, false);
		
		// get the textViews on fragment
		this.tvStepsTotal 				= (TextView) view.findViewById(R.id.tvStepsTotal);
		this.tvStepsTotalSinceStart 	= (TextView) view.findViewById(R.id.tvStepsTotalSinceStart);
		this.tvStepsPerMinute			= (TextView) view.findViewById(R.id.tvStepsPerMinute);
		this.tvStepsAverage 			= (TextView) view.findViewById(R.id.tvStepsAverage);
		this.tvStepDetectionDuration 	= (TextView) view.findViewById(R.id.tvStepDetectionDuration);
		
		// start button on fragment and add listener
		this.btnStart = (Button) view.findViewById(R.id.btn_start);
		this.btnStart.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
			
				// start / stop step detection
				if(isRunning){
					pauseStepDetection();
				}
				else{
					startStepDetection();
				}
			}
		});
		
		// reset button on fragment and add listener
		this.btnReset = (Button) view.findViewById(R.id.btn_reset);
		this.btnReset.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				resetStepDetection();
			}
		}); 
		
		// TODO: 
		// find a better way, this is not a good implementation for bold fonts here
		// set seperate Typeface for Buttons (a little bit wider)
	    this.typefaceMgr = new TypefaceManager(getActivity());
		Typeface fontBold = Typeface.createFromAsset(getActivity().getAssets(), this.typefaceMgr.getTypeface(FontStyle.BOLD));
	    this.btnStart.setTypeface(fontBold);
	    this.btnReset.setTypeface(fontBold);
	    
	    
	    // PREFERENCES
	    this.prefs = PreferenceManager.getDefaultSharedPreferences(this.getActivity());
        // register preference change listener
        prefs.registerOnSharedPreferenceChangeListener(this);
        // and set remembered preferences
        this.stepLength = Float.parseFloat((prefs.getString("pref_key_steplength", "120.0")));
	    
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
	
	// ------------------------------------------------------------------------
	
	private void startStepDetection(){
		
		this.isPaused = false;
		
		// time interval, needed for average step calculation
		if(customHandlerPerSecond==null){
			this.customHandlerPerSecond = new Handler();
			this.customHandlerPerSecond.postDelayed(updateTimerPerSecond, 0);
		}

        // start the stopWatch
        this.sw.resume();
		
		// Initialize StepDetector
		if(stepDetector==null){
			stepDetector = new StepDetector(getActivity().getSystemService(Context.SENSOR_SERVICE));
		}
		
		// Start StepDetection
		this.stepDetector.setActivityRunning(true);
		this.stepDetector.registerSensorManager();
		this.isRunning = true;

		// attach Observer to this activity
		this.stepDetector.attachObserver(this);
		
		
		// change the button label to stop
		this.btnStart.setText(this.getResources().getString(R.string.btn_pedometer_pause));
		
		Resources res = getResources();
		int color = res.getColor(R.color.red);
		this.btnStart.setTextColor(color);
		
	    // deactivate when api level < 19
	    if(this.stepDetector.getApiLevel()<19){
	    	this.tvStepsTotalSinceStart.setText("n.a.");
	    }
	}
	
	// ------------------------------------------------------------------------
	
	private void pauseStepDetection(){
		
		this.isPaused = true;
		
		 // start the stopWatch
        this.sw.pause();
        
		// attach Observer to this activity
		this.stepDetector.detachObserver(this);
		this.stepDetector.setActivityRunning(false);
		this.isRunning = false;
		
		// change the button label to start
		this.btnStart.setText(this.getResources().getString(R.string.btn_pedometer_start));
		
		Resources res = getResources();
		int color = res.getColor(R.color.systemLightBlue);
		this.btnStart.setTextColor(color);
	}
	
	// ------------------------------------------------------------------------
	
	private void resetStepDetection(){
		
		if(this.totalTime > 0){
			
			this.pauseStepDetection();
			
			// clear the stopWatch
			this.sw.clear();
			
			this.totalTime = 0;
			this.stepcount = 0;
			this.stepFrequencyPerMinute = 0.0f;
			this.stepcountAPI19 = 0;
			
			// clear all textViews
			this.tvStepDetectionDuration.setText("00:00:00");
			this.tvStepsAverage.setText("0");
			this.tvStepsPerMinute.setText("0");
			this.tvStepsTotal.setText("0");
			this.tvStepsTotalSinceStart.setText("0");
		}
	}
	
	// ------------------------------------------------------------------------
	
	// Step Detection Observer Update
	@Override
	public void update() {

		if(!isPaused){
			// count the recognized steps
			this.stepcount += 1;
//			if(D) Log.i(TAG, "Stepcount: " + this.stepcount); // DEBUG
			this.tvStepsTotal.setText(Integer.toString(this.stepcount));
			
			// calculate run distance
			this.distance = (this.stepcount * (this.stepLength*0.01f)) / 1000;
			this.tvStepsPerMinute.setText(String.format("%.3f", this.distance));
		}
	}
	
	// ------------------------------------------------------------------------
	
	// Step Detection Observer Update
	@Override
	public void updateForAPILevel19() {

		if(!isPaused){
			// count the recognized steps
			this.stepcountAPI19 += 1;
//			if(D) Log.i(TAG, "Stepcount: " + this.stepcount); // DEBUG
			this.tvStepsTotalSinceStart.setText(Integer.toString(this.stepcountAPI19));
		}
	}
    
	// ------------------------------------------------------------------------
	
	// single thread for timer
	private Runnable updateTimerPerSecond = new Runnable()
	{
		public void run()
		{
				int intervallTime = 1000; //milliseconds
				
				// calculate step frequency f=n/t
				stepFrequencyPerMinute = (float)((float)stepcount / (float)totalTime) * 60f;

				// sets the stopWatch, timer textview and counts the totaltime (in stopWach method)
				setStopWatch();
				tvStepsAverage.setText(Integer.toString(((int)Math.round(stepFrequencyPerMinute))));
				
				customHandlerPerSecond.postDelayed(this, intervallTime);
		}
	};
	
	// ------------------------------------------------------------------------
	
	// ------------------------------------------------------------------------
	
	public void setStopWatch() {

//		if(D) Log.i(TAG, "StopWatch: " + sw.getElapsedTimeHour() + ":" + sw.getElapsedTimeMin() + ":" +  sw.getElapsedTimeSecs() + ":" + sw.getElapsedTimeMili());

		if(!isPaused){
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
			this.tvStepDetectionDuration.setText(hours + ":" + minutes + ":" + seconds);
			
			// count the total seconds
			this.totalTime += 1;
//			this.tvStepsTotalSinceStart.setText(Float.toString(totalTime));
		}
	}
	
	// ------------------------------------------------------------------------

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,String key) {
		
		if (key.equals("pref_key_steplength")) {
            this.stepLength = Float.parseFloat((prefs.getString("pref_key_steplength", "120.0")));
        }

	}
	 
	// -----------------------------------------------------------------------
}
