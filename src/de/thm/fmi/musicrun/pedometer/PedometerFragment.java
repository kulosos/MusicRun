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
public class PedometerFragment extends Fragment {
	
	// TextViews
	private TextView tvStepsTotal;
	private TextView tvStepsTotalSinceStart;
	private TextView tvStepsAverage;
	private TextView tvStepsPerMinute;
	private TextView tvStepDetectionDuration;
	private TextView tvCurrentIntervall;
	private TextView tvStepsLastIntervall;
	
	// Buttons
	private Button btnStart;
	private Button btnReset;
	 
	// DEBUG
	private static final String TAG = MainActivity.class.getName();
	private static final boolean D = false;
	
	
	// ------------------------------------------------------------------------

	public PedometerFragment(){	
	}

	// -----------------------------------------------------------------------
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	
		if(D) Log.i(TAG, "PedometerFragment onCreateView()");
		
		// get the fragment view
		View view = inflater.inflate(R.layout.fragment_pedometer, container, false);
		
		// init buttons and text views
		this.initGuiElements(view);
		
		// init PedometerController singleton
		PedometerController.initInstance(getActivity(), this);
		
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
	
	private void initGuiElements(View view){

		// get the textViews on fragment
		this.tvStepsTotal 				= (TextView) view.findViewById(R.id.tvStepsTotal);
		this.tvStepsTotalSinceStart 	= (TextView) view.findViewById(R.id.tvStepsTotalSinceStart);
		this.tvStepsPerMinute			= (TextView) view.findViewById(R.id.tvStepsPerMinute);
		this.tvStepsAverage 			= (TextView) view.findViewById(R.id.tvStepsAverage);
		this.tvStepDetectionDuration 	= (TextView) view.findViewById(R.id.tvStepDetectionDuration);
		this.tvCurrentIntervall			= (TextView) view.findViewById(R.id.tvCurrentIntervall);
		this.tvStepsLastIntervall		= (TextView) view.findViewById(R.id.tvStepsLastIntervall);
		
		// start button on fragment and add listener
		this.btnStart = (Button) view.findViewById(R.id.btn_start);
		this.btnStart.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
			
				// start / stop step detection
				if(PedometerController.getInstance().isRunning){
					PedometerController.getInstance().pauseStepDetection();
				}
				else{
					PedometerController.getInstance().startStepDetection();
				}
			}
		});
		
		// reset button on fragment and add listener
		this.btnReset = (Button) view.findViewById(R.id.btn_reset);
		this.btnReset.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				PedometerController.getInstance().resetStepDetection();
			}
		}); 
	}
	
	// ---------------------- SETTERS / GETTERS -------------------------------
	
	public TextView getTvStepsTotal() {
		return tvStepsTotal;
	}

	public void setTvStepsTotal(TextView tvStepsTotal) {
		this.tvStepsTotal = tvStepsTotal;
	}

	public TextView getTvStepsTotalSinceStart() {
		return tvStepsTotalSinceStart;
	}

	public void setTvStepsTotalSinceStart(TextView tvStepsTotalSinceStart) {
		this.tvStepsTotalSinceStart = tvStepsTotalSinceStart;
	}

	public TextView getTvStepsAverage() {
		return tvStepsAverage;
	}

	public void setTvStepsAverage(TextView tvStepsAverage) {
		this.tvStepsAverage = tvStepsAverage;
	}

	public TextView getTvStepsPerMinute() {
		return tvStepsPerMinute;
	}

	public void setTvStepsPerMinute(TextView tvStepsPerMinute) {
		this.tvStepsPerMinute = tvStepsPerMinute;
	}

	public TextView getTvStepDetectionDuration() {
		return tvStepDetectionDuration;
	}

	public void setTvStepDetectionDuration(TextView tvStepDetectionDuration) {
		this.tvStepDetectionDuration = tvStepDetectionDuration;
	}

	public Button getBtnStart() {
		return btnStart;
	}

	public void setBtnStart(Button btnStart) {
		this.btnStart = btnStart;
	}

	public Button getBtnReset() {
		return btnReset;
	}

	public void setBtnReset(Button btnReset) {
		this.btnReset = btnReset;
	}

	public TextView getTvCurrentIntervall() {
		return tvCurrentIntervall;
	}

	public void setTvCurrentIntervall(TextView tvCurrentIntervall) {
		this.tvCurrentIntervall = tvCurrentIntervall;
	}

	public TextView getTvStepsLastIntervall() {
		return tvStepsLastIntervall;
	}

	public void setTvStepsLastIntervall(TextView tvStepsLastIntervall) {
		this.tvStepsLastIntervall = tvStepsLastIntervall;
	}

	
}
