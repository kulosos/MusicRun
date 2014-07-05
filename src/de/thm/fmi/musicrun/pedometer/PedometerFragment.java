package de.thm.fmi.musicrun.pedometer;

import java.text.DateFormatSymbols;
import java.util.Calendar;

import de.thm.fmi.musicrun.R;
import de.thm.fmi.musicrun.application.MainActivity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class PedometerFragment extends Fragment implements IStepDetectionObserver {

	private StepDetector stepDetector;
	private int stepcount = 0;	
	private int totalTime;
	private Float stepFrequencyPerMinute;
	
	// TextViews
	private TextView tvStepsTotal;
	private TextView tvStepsTotalSinceStart;
	private TextView tvStepsAverage;
	private TextView tvStepsPerMinute;
	private TextView tvStepDetectionDuration;
	
	// Timer
	private Handler customHandlerPerSecond;
	
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
		
		// Initialize StepDetector
		this.stepDetector = new StepDetector(this.getActivity().getSystemService(Context.SENSOR_SERVICE));
		// attach Observer to this activity
		this.stepDetector.attachObserver(this);	
		
		// get the fragment view
		View view = inflater.inflate(R.layout.fragment_pedometer, container, false);
		
		// get the textViews on fragment
		this.tvStepsTotal 				= (TextView) view.findViewById(R.id.tvStepsTotal);
		this.tvStepsTotalSinceStart 	= (TextView) view.findViewById(R.id.tvStepsTotalSinceStart);
		this.tvStepsPerMinute			= (TextView) view.findViewById(R.id.tvStepsPerMinute);
		this.tvStepsAverage 			= (TextView) view.findViewById(R.id.tvStepsAverage);
		this.tvStepDetectionDuration 	= (TextView) view.findViewById(R.id.tvStepDetectionDuration);
		
		// time interval, needed for average step calculation
	    this.customHandlerPerSecond = new Handler();
        customHandlerPerSecond.postDelayed(updateTimerPerSecond, 0);

		return view;
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
	        	
	        	setTime();
	        }
	};
    
	// ------------------------------------------------------------------------
	
	// Step Detection Observer Update
	@Override
	public void update() {

		// count the recognized steps
		this.stepcount += 1;
		if(D) Log.i(TAG, "Stepcount: " + this.stepcount); // DEBUG
		this.tvStepsTotal.setText(Float.toString(this.stepcount));
	}

	// ------------------------------------------------------------------------
	
	@Override
	public void onResume(){
		super.onResume();
		
		// Start StepDetection
		this.stepDetector.setActivityRunning(true);
		this.stepDetector.registerSensorManager();
	}
	
	// ------------------------------------------------------------------------

	@Override
	public void onPause(){
		super.onPause();
		
		// Stop StepDetection
		this.stepDetector.setActivityRunning(false);
	}
	
	// ------------------------------------------------------------------------
	
	public void setTime() {
	    Calendar cal = Calendar.getInstance();
	    int minutes = cal.get(Calendar.MINUTE);

	    if (DateFormat.is24HourFormat(getActivity())) {
	        int hours = cal.get(Calendar.HOUR_OF_DAY);
	        this.tvStepDetectionDuration.setText((hours < 10 ? "0" + hours : hours) + ":" + (minutes < 10 ? "0" + minutes : minutes));
	    }
	    else {
	        int hours = cal.get(Calendar.HOUR);
	        this.tvStepDetectionDuration.setText(hours + ":" + (minutes < 10 ? "0" + minutes : minutes) + " " + new DateFormatSymbols().getAmPmStrings()[cal.get(Calendar.AM_PM)]);
	    }
	}
	 
	 // -----------------------------------------------------------------------
}
