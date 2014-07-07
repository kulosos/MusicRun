package de.thm.fmi.musicrun.pedometer;

import de.thm.fmi.musicrun.R;
import de.thm.fmi.musicrun.application.MainActivity;
import de.thm.fmi.musicrun.application.TypefaceManager;
import de.thm.fmi.musicrun.application.TypefaceManager.FontStyle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

@SuppressLint("ResourceAsColor")
public class PedometerFragment extends Fragment implements IStepDetectionObserver {
	
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
	private int totalTime;
	private Float stepFrequencyPerMinute;
	private boolean isRunning = false;
	
	// Runnable Thread / Timer
	private Handler customHandlerPerSecond;
	StopWatch sw = new StopWatch();
	private Object pauseLock = new Object();
	private boolean isPaused = true;
    private boolean isFinished = false;
    private Intent timerIntentService = null;
	
    // TypefaceManager
    TypefaceManager typefaceMgr;
    
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
//		this.btnReset.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//			
//			}
//		}); 
		
		// TODO:
		// find a better way, this is not a good implementation for bold fonts here
		// set seperate Typeface for Buttons (a little bit wider)
	    this.typefaceMgr = new TypefaceManager(getActivity());
		Typeface fontBold = Typeface.createFromAsset(getActivity().getAssets(), this.typefaceMgr.getTypeface(FontStyle.BOLD));
	    this.btnStart.setTypeface(fontBold);
	    this.btnReset.setTypeface(fontBold);
	   		

		return view;
	}

	// ------------------------------------------------------------------------
	
	@Override
	public void onResume(){
		super.onResume();
		// start intentService
		if(this.timerIntentService == null){
			this.timerIntentService = new Intent(getActivity(), TimerIntentService.class);
		}
		this.getActivity().startService(timerIntentService);
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
		
//		this.customHandlerPerSecond.notifyAll();

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
		
//		// start intentService
//		if(this.timerIntentService == null){
//			this.timerIntentService = new Intent(getActivity(), TimerIntentService.class);
//		}
//		getActivity().startService(timerIntentService);
	
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
		
        
        
		// stop runnable handler
//		this.customHandlerPerSecond.removeCallbacks(updateTimerPerSecond);
		
//		try {
//			this.customHandlerPerSecond.wait();
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}
	
	// ------------------------------------------------------------------------
	
	// Step Detection Observer Update
	@Override
	public void update() {

		if(!isPaused){
			// count the recognized steps
			this.stepcount += 1;
			if(D) Log.i(TAG, "Stepcount: " + this.stepcount); // DEBUG
			this.tvStepsTotal.setText(Float.toString(this.stepcount));
		}
	}
    
	// ------------------------------------------------------------------------
	
	// time interval, needed for average step calculation
	private Runnable updateTimerPerSecond = new Runnable()
	{

		public void run()
		{
				int intervallTime = 1000; //milliseconds
				//	        	if(D) Log.i(TAG, "TIMER INTERVAL"); 
				totalTime = totalTime + 1;
				tvStepsTotalSinceStart.setText(Float.toString(totalTime));

				customHandlerPerSecond.postDelayed(this, intervallTime);

				// calculate step frequency f=n/t
				stepFrequencyPerMinute = (float)Math.round(((float)stepcount / (float)totalTime) * 60f);

				tvStepsAverage.setText(Float.toString((stepFrequencyPerMinute)));

				setStopWatch();
			
//			int intervallTime = 1000; //milliseconds
//
//			while(!isFinished){
//				
//				totalTime = totalTime + 1;
//				tvStepsTotalSinceStart.setText(Float.toString(totalTime));
//
//				customHandlerPerSecond.postDelayed(this, intervallTime);
//
//				// calculate step frequency f=n/t
//				stepFrequencyPerMinute = (float)Math.round(((float)stepcount / (float)totalTime) * 60f);
//
//				tvStepsAverage.setText(Float.toString((stepFrequencyPerMinute)));
//
//				setStopWatch();
//				
//				synchronized (pauseLock){
//					while (isPaused){
//						try{
//							pauseLock.wait();
//						}
//						catch(Exception e){
//							if(D) Log.e(TAG, "PAUSELOCK - " + e);
//						}
//					}
//				}
//				
//			
//			}
//		}
//		
//		public void onPause() {
//			synchronized (pauseLock) {
//				isPaused = true;
//			}
//		}
//
//		public void onResume() {
//			synchronized (pauseLock) {
//				isPaused = false;
//				pauseLock.notifyAll();
//			}
		}
	};
	
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
		}
	}
	 // -----------------------------------------------------------------------
}
