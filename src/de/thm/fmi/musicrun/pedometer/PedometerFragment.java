package de.thm.fmi.musicrun.pedometer;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import de.thm.fmi.musicrun.R;
import de.thm.fmi.musicrun.application.MainActivity;
import android.annotation.SuppressLint;
import android.graphics.Typeface;
import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

@SuppressLint("ValidFragment")
public class PedometerFragment extends Fragment implements IStepDetectionObserver {

	private StepDetector stepDetector;
	private int stepcount = 0;	
	private int totalTime;
	private Float stepFrequencyPerMinute;
	
	// TextViews
	private List<TextView> uiTextElements;
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

	public PedometerFragment(Object systemService){

		// Initialize StepDetector
		this.stepDetector = new StepDetector(systemService);

		// attach Observer to this activity
		this.stepDetector.attachObserver(this);
		
	}

	// -----------------------------------------------------------------------
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	
		View view = inflater.inflate(R.layout.fragment_pedometer, container, false);
		
		// set Font Typefaces for all TextViews on Fragment
//		this.setUiTextElements(view);
//		this.setTypefaces(view);
		
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
	
	// adds UI TextView Elements to List
	private void setUiTextElements(View view){
		
		this.uiTextElements = new ArrayList<TextView>();
		// adds TextView by Id
		this.uiTextElements.add((TextView) view.findViewById(R.id.tvStepsTotal));
		this.uiTextElements.add((TextView) view.findViewById(R.id.tvStepsTotalLabel));
		this.uiTextElements.add((TextView) view.findViewById(R.id.tvStepsTotalSinceStart));
		this.uiTextElements.add((TextView) view.findViewById(R.id.tvStepsTotalSinceStartLabel));
		this.uiTextElements.add((TextView) view.findViewById(R.id.tvStepsPerMinute));
		this.uiTextElements.add((TextView) view.findViewById(R.id.tvStepsPerMinuteLabel));
		this.uiTextElements.add((TextView) view.findViewById(R.id.tvStepsAverage));
		this.uiTextElements.add((TextView) view.findViewById(R.id.tvStepsAverageLabel));
	}
	
	// -----------------------------------------------------------------------
	
	// sets typeface for all TextView in uiTextElementsList
	private void setTypefaces(View view){
		
		Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Roboto-Thin.ttf");
		
		for(int i = 0; i < this.uiTextElements.size(); i++){
//			//set text
//			this.uiTextElements.get(i).setText("0");
			//set Typeface
			this.uiTextElements.get(i).setTypeface(font);
		}
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
