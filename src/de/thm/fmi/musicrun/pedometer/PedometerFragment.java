package de.thm.fmi.musicrun.pedometer;

import java.util.ArrayList;
import java.util.List;

import de.thm.fmi.musicrun.R;
import android.annotation.SuppressLint;
import android.graphics.Typeface;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

@SuppressLint("ValidFragment")
public class PedometerFragment extends Fragment implements IStepDetectionObserver {

	private StepDetector stepDetector;
	private int stepcount = 0;	
	private List<TextView> uiTextElements;
	
	// ------------------------------------------------------------------------

	public PedometerFragment(Object systemService){

		// Initialize StepDetector
		this.stepDetector = new StepDetector(systemService);

		// attach Observer to this activity
		this.stepDetector.attachObserver(this);

//		tvCount = (TextView) getView().findViewById(R.id.textView1);

	}

	// -----------------------------------------------------------------------
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	
		View view = inflater.inflate(R.layout.fragment_pedometer, container, false);
		
		// set Font Typefaces for all TextViews on Fragment
		this.setUiTextElements(view);
		this.setTypefaces(view);
		
		return view;
	}
	
	// ------------------------------------------------------------------------
	
	// Step Detection Observer Update
	@Override
	public void update() {

		// counts the recognized steps
		this.stepcount = this.stepcount +1;
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
		this.uiTextElements.add((TextView) view.findViewById(R.id.tvStepsSum));
		this.uiTextElements.add((TextView) view.findViewById(R.id.tvStepsSinceStart));
	}
	
	// -----------------------------------------------------------------------
	
	// sets typeface for all TextView in uiTextElementsList
	private void setTypefaces(View view){
		
		Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Lato-Regular.ttf");
		
		for(int i = 0; i < this.uiTextElements.size(); i++){
			//set text
			this.uiTextElements.get(i).setText("0");
			//set Typeface
			this.uiTextElements.get(i).setTypeface(font);
		}
	}
	
}
