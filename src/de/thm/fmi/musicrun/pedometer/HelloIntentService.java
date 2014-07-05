package de.thm.fmi.musicrun.pedometer;

import de.thm.fmi.musicrun.application.MainActivity;
import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

public class HelloIntentService extends IntentService {


	// DEBUG
	private static final String TAG = MainActivity.class.getName();
	private static final boolean D = true;


	/**
	 * A constructor is required, and must call the super IntentService(String)
	 * constructor with a name for the worker thread.
	 */
	public HelloIntentService() {
		super("HelloIntentService");
	}

	/**
	 * The IntentService calls this method from the default worker thread with
	 * the intent that started the service. When this method returns, IntentService
	 * stops the service, as appropriate.
	 */
	@Override
	protected void onHandleIntent(Intent intent) {
		
		if(D) Log.i(TAG, "HelloIntentService onHandleIntent Method");
		
		// Normally we would do some work here, like download a file.
		// For our sample, we just sleep for 5 seconds.
		long endTime = System.currentTimeMillis() + 5*1000;
		while (System.currentTimeMillis() < endTime) {
			synchronized (this) {
				try {
					if(D) Log.i(TAG, "############### HELLO HELLO HERE I AM!!!! ###################");
					wait(endTime - System.currentTimeMillis());
				} catch (Exception e) {
				}
			}
		}
	}
}