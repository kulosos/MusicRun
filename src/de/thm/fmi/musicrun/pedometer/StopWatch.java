package de.thm.fmi.musicrun.pedometer;

public class StopWatch {
	
	private long startTime = 0;
	private boolean running = false;
	private long currentTime = 0;
	
	// DEBUG
//	private static final String TAG = MainActivity.class.getName();
//	private static final boolean D = true;

	// ------------------------------------------------------------------------
	public void start() {
		this.startTime = System.currentTimeMillis();
		this.running = true;
	}

	// ------------------------------------------------------------------------
	
	public void stop() {
		this.running = false;
	}

	// ------------------------------------------------------------------------
	
	public void pause() {
		this.running = false;
		currentTime = System.currentTimeMillis() - startTime;
	}
	
	// ------------------------------------------------------------------------
	
	public void resume() {
		this.running = true;
		this.startTime = System.currentTimeMillis() - currentTime;
	}

	// ------------------------------------------------------------------------
	
	public void clear(){
		this.startTime = 0;
		this.running = false;
		this.currentTime = 0;
	}
	
	// ------------------------------------------------------------------------
	//elaspsed time in milliseconds
	public long getElapsedTimeMili() {
		long elapsed = 0;
		if (running) {
			elapsed =((System.currentTimeMillis() - startTime)/100) % 1000 ;
		}
		return elapsed;
	}

	// ------------------------------------------------------------------------
	//elaspsed time in seconds
	public long getElapsedTimeSecs() {
		long elapsed = 0;
		if (running) {
			elapsed = ((System.currentTimeMillis() - startTime) / 1000) % 60;
		}
		return elapsed;
	}

	// ------------------------------------------------------------------------
	//elaspsed time in minutes
	public long getElapsedTimeMin() {
		long elapsed = 0;
		if (running) {
			elapsed = (((System.currentTimeMillis() - startTime) / 1000) / 60 ) % 60;
		}
		return elapsed;
	}

	// ------------------------------------------------------------------------
	//elaspsed time in hours
	public long getElapsedTimeHour() {
		long elapsed = 0;
		if (running) {
			elapsed = ((((System.currentTimeMillis() - startTime) / 1000) / 60 ) / 60);
		}
		return elapsed;
	}
}
