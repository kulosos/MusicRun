package de.thm.fmi.musicrun.pedometer;

import de.thm.fmi.musicrun.application.MainActivity;
import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

public class TimerIntentService extends IntentService {


	// DEBUG
	private static final String TAG = MainActivity.class.getName();
	private static final boolean D = false;


	/**
	 * A constructor is required, and must call the super IntentService(String)
	 * constructor with a name for the worker thread.
	 */
	public TimerIntentService() {
		super("TimerIntentService");
	}

	/**
	 * The IntentService calls this method from the default worker thread with
	 * the intent that started the service. When this method returns, IntentService
	 * stops the service, as appropriate.
	 */
	@Override
	protected void onHandleIntent(Intent intent) {
		
		// Normally we would do some work here, like download a file.
		// For our sample, we just sleep for 5 seconds.
		long endTime = System.currentTimeMillis() + 5*1000;
		while (System.currentTimeMillis() < endTime) {
			synchronized (this) {
				try {
					if(D) Log.i(TAG, "############### TIMER INTENT SERVICE ###################");
					wait(endTime - System.currentTimeMillis());
				} catch (Exception e) {
				}
			}
		}
	}
}