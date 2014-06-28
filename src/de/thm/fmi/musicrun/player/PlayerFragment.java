package de.thm.fmi.musicrun.player;

import de.thm.fmi.musicrun.R;
import de.thm.fmi.musicrun.pedometer.IStepDetectionObserver;
import de.thm.fmi.musicrun.pedometer.StepDetector;
import android.annotation.SuppressLint;
import android.content.Context;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

@SuppressLint("ValidFragment")
public class PlayerFragment extends Fragment implements IStepDetectionObserver {

	private StepDetector stepDetector;
	
	private TextView tvCount;
	private int stepcount = 0;	

	// ------------------------------------------------------------------------

	public PlayerFragment(Object systemService){

		// Initialize StepDetector
		this.stepDetector = new StepDetector(systemService);

		// attach Observer to this activity
		this.stepDetector.attachObserver(this);

//		tvCount = (TextView) getView().findViewById(R.id.textView1);

	}

	// -----------------------------------------------------------------------
	
	@Override
	public View onCreateView(LayoutInflater inflater,
			ViewGroup container, Bundle savedInstanceState) {
		/**
		 * Inflate the layout for this fragment
		 */
		return inflater.inflate(
				R.layout.fragment_main, container, false);
	}
	
	// ------------------------------------------------------------------------
	
	// Step Detection Observer Update
	@Override
	public void update() {

		// counts the recognized steps
		this.stepcount = this.stepcount +1;
//		tvCount.setText( Integer.toString(this.stepcount));
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
	
	
}
