/**
 * Class StepDetector
 * @brief Detects steps and notifies all listeners (that implement StepListener).
 */

package de.thm.fmi.musicrun.pedometer;

import java.util.Vector;

import de.thm.fmi.musicrun.application.MainActivity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
//import android.widget.Toast;
//import android.content.Context;

public class StepDetector implements SensorEventListener
{

	// DEBUG
	private static final String TAG = MainActivity.class.getName();
	private static final boolean D = false;
	//    private final static String TAG = "StepDetector";

	public SensorManager sensorManager;
	private boolean isActivityRunning;

	private float   mLimit = 10;
	private float   mLastValues[] = new float[3*2];
	private float   mScale[] = new float[2];
	private float   mYOffset;

	private float   mLastDirections[] = new float[3*2];
	private float   mLastExtremes[][] = { new float[3*2], new float[3*2] };
	private float   mLastDiff[] = new float[3*2];
	private int     mLastMatch = -1;

	private Vector observers;

	private int apiLevel;
	private boolean apiComparison = true;

	// ------------------------------------------------------------------------

	public StepDetector(Object systemService) {
		if(D)Log.i(TAG, "DEBUG - StepDetector CONSTRUCTOR"); // DEBUG

		this.sensorManager = (SensorManager) systemService;

		int h = 480; // TODO: remove this constant
		mYOffset = h * 0.5f;
		mScale[0] = - (h * 0.5f * (1.0f / (SensorManager.STANDARD_GRAVITY * 2)));
		mScale[1] = - (h * 0.5f * (1.0f / (SensorManager.MAGNETIC_FIELD_EARTH_MAX)));

		// instantiate observer vector
		observers = new Vector();
	}

	public void setSensitivity(float sensitivity) {
		mLimit = sensitivity; // 1.97  2.96  4.44  6.66  10.00  15.00  22.50  33.75  50.62
	}

	// ------------------------------------------------------------------------

	public void registerSensorManager(){
		if(D)Log.i(TAG, "DEBUG - StepDetector CONSTRUCTOR"); // DEBUG

		isActivityRunning = true;

		//    	TODO: API Level Detection (TYPE_STEP_COUNTER for API Level 19 and higher)
		this.apiLevel = Integer.valueOf(android.os.Build.VERSION.SDK);
		if(D) Log.i(TAG, "Detected API version: " + apiLevel);
		
		// available in every device with sensor
		Sensor countSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		if (countSensor != null) {
			Log.i(TAG, "DEBUG - API Level < 19 - SUCCESS: Count sensoravailable"); // DEBUG
			sensorManager.registerListener(this, countSensor, SensorManager.SENSOR_DELAY_UI);
		} else {
			Log.w(TAG, "DEBUG - API Level < 19 - ERROR: Count sensor not available"); // DEBUG
			// Toast.makeText(Context.SENSOR_SERVICE, "Count sensor not available!", Toast.LENGTH_LONG).show();
		}

		// available up to API Level 19
		Sensor countSensor2 = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
		if (countSensor2 != null) {
			Log.i(TAG, "DEBUG - API Level >= 19 - SUCCESS: Count sensoravailable"); // DEBUG
			sensorManager.registerListener(this, countSensor2, SensorManager.SENSOR_DELAY_GAME);
		} else {
			Log.w(TAG, "DEBUG - API Level >= 19 - ERROR: Count sensor not available"); // DEBUG
			// Toast.makeText(Context.SENSOR_SERVICE, "Count sensor not available!", Toast.LENGTH_LONG).show();
		}


	}

	// ------------------------------------------------------------------------

	public void onSensorChanged(SensorEvent event) {
		//if(D) Log.d(TAG, "DEBUG - onSensorChanged"); // DEBUG

		if(this.apiLevel < 19 || this.apiComparison){

			if(this.isActivityRunning){
				Sensor sensor = event.sensor; 
				synchronized (this) {
					if (sensor.getType() == Sensor.TYPE_ORIENTATION) {
					}
					else {
						int j = (sensor.getType() == Sensor.TYPE_ACCELEROMETER) ? 1 : 0;
						if (j == 1) {
							float vSum = 0;
							for (int i=0 ; i<3 ; i++) {
								final float v = mYOffset + event.values[i] * mScale[j];
								vSum += v;
							}
							int k = 0;
							float v = vSum / 3;

							float direction = (v > mLastValues[k] ? 1 : (v < mLastValues[k] ? -1 : 0));
							if (direction == - mLastDirections[k]) {
								// Direction changed
								int extType = (direction > 0 ? 0 : 1); // minumum or maximum?
								mLastExtremes[extType][k] = mLastValues[k];
								float diff = Math.abs(mLastExtremes[extType][k] - mLastExtremes[1 - extType][k]);

								if (diff > mLimit) {

									boolean isAlmostAsLargeAsPrevious = diff > (mLastDiff[k]*2/3);
									boolean isPreviousLargeEnough = mLastDiff[k] > (diff/3);
									boolean isNotContra = (mLastMatch != 1 - extType);

									if (isAlmostAsLargeAsPrevious && isPreviousLargeEnough && isNotContra) {
										if(D)Log.i(TAG, "step - apilevel < 19");

										//notify Observes
										this.notifyObserver(true);

										mLastMatch = extType;
									}
									else {
										mLastMatch = -1;
									}
								}
								mLastDiff[k] = diff;
							}
							mLastDirections[k] = direction;
							mLastValues[k] = v;
						}
					}
				}
			}
		}

		if(this.apiLevel >= 19 && event.sensor.getType() == Sensor.TYPE_STEP_COUNTER){
			if(D)Log.i(TAG, "step - apilevel >= 19");
			this.notifyObserver(false);
		}
	}

	// ------------------------------------------------------------------------

	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
	}

	// ------------------------- OBSERVER -------------------------------------

	public void attachObserver(IStepDetectionObserver sdo){
		this.observers.addElement(sdo);
	}

	public void detachObserver(IStepDetectionObserver sdo){
		observers.removeElement(sdo);
	}

	public void notifyObserver(boolean apiOld){
		
		if(apiOld){
			for (int i=0; i< observers.size(); i++) {
				((IStepDetectionObserver)(observers.elementAt(i))).update();
			}
		}
		else {
			for (int i=0; i< observers.size(); i++) {
				((IStepDetectionObserver)(observers.elementAt(i))).updateForAPILevel19();
			}
		}
	}

	// -------------------------- SETTER / GETTER -----------------------------

	public void setActivityRunning(boolean b){
		this.isActivityRunning = b;
	}

	public boolean getActivityRunning(){
		return this.isActivityRunning;
	}
	
	public int getApiLevel(){
		return this.apiLevel;
	}

}