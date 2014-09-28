package de.thm.fmi.musicrun.application;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import de.thm.fmi.musicrun.R;
import de.thm.fmi.musicrun.pedometer.PedometerController;
import wseemann.media.FFmpegMediaMetadataRetriever;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class PlayerController implements IPlaylistObserver, OnCompletionListener {

	private static PlayerController instance;

	// Fragment
	public PlayerFragment playerFragment;
	Context context;
	
	// MediaPlayer
	private MediaPlayer mediaPlayer;
	private Track currentPlayingTrack;

	ProgressDialog progress;
	Message msg;
	public Handler handler, seekbarHandler;

	// Preferences
	PreferencesManager prefsManager;

	// DEBUG
	private static final String TAG = MainActivity.class.getName();
	private static final boolean D = true;

	// ------------------------------------------------------------------------

	private PlayerController(Context context, PlayerFragment pf){

		this.context = context;
		this.playerFragment = pf;
		
		// TODO
		// It would be great if it would working without this hack here
		// its neccessary to avoid the NullPointer Expection
		// because the PlaylistController instantiates later then PlayerController
		// and at now if wasn't able to find the error source
		PlaylistFragment plf = (PlaylistFragment)SectionsPagerAdapter.getInstance().getItem(1);
		PlaylistController.initInstance(plf, context);

		// register observer
		
		PlaylistController.getInstance().attachObserver(this);
		
		// MusicPlayer
		this.mediaPlayer = new MediaPlayer();
		this.mediaPlayer.setOnCompletionListener(this);
		
		// background thread for seekbar song playback updating
		this.seekbarHandler = new Handler();
		
		// Preferences
		this.prefsManager = new PreferencesManager(this.context);
		
	}

	// ------------------- SINGLETON METHODS ----------------------------------

	public static void initInstance(Context context, PlayerFragment pf){
		if(instance == null){
			instance = new PlayerController(context, pf);
		}
	}

	public static PlayerController getInstance(){
		return instance;
	}

	// ------------------------------------------------------------------------

	// Observer Update
	@Override
	public void updateCurrentPlayingTrack(Track track) {

		this.currentPlayingTrack = track;
		
		this.playerFragment.getLabelTitle().setText(track.getTitle());
		this.playerFragment.getLabelArtist().setText(track.getArtist());
		
	}
	
	// ------------------------------------------------------------------------
	
	public void playTrackFromPlaylist(Track track){
		
		this.currentPlayingTrack = track;

		this.stopMusic();
		this.playMusic(this.currentPlayingTrack);
		PedometerController.getInstance().startStepDetection();

		// change PauseButton to PlayIcon
//		this.playerFragment.getBtnPlay().setImageDrawable(this.context.getResources().getDrawable(R.drawable.btn_pause_white));

	}
	
	// ------------------------------------------------------------------------
	
	private void playMusic(Track track){

		// check for external storage isReadable
		if(this.isExternalStorageReadable()){

			// change Play Button to PauseIcon
			this.playerFragment.getBtnPlay().setImageDrawable(this.context.getResources().getDrawable(R.drawable.btn_pause_white));

			String fileName = track.getFilepath();
			String filePath = this.prefsManager.getMusicFilepath() + fileName; 

			new CustomToast(this.context, fileName, R.drawable.ic_launcher, 400);

			try {
				this.mediaPlayer.setDataSource(filePath);
				this.mediaPlayer.prepare();
				this.mediaPlayer.start();

				// set Progress bar values
				this.playerFragment.getSongProgressSeekBar().setProgress(0);
				this.playerFragment.getSongProgressSeekBar().setMax(100);

				// updating progress bar
				this.updateProgressBar();

			} catch (Exception e) {
				e.printStackTrace();
				if(D) Log.e(TAG, e.toString());
			}

		}
		else{
			Log.e(TAG, "EXTERNAL STORAGE IS NOT READABLE");
		}
	}

	// ------------------------------------------------------------------------

	public void pauseMusic() {
				
		if(this.mediaPlayer.isPlaying()){
			
			this.mediaPlayer.pause();

			PedometerController.getInstance().pauseStepDetection();

			// change PauseButton to PlayIcon
			this.playerFragment.getBtnPlay().setImageDrawable(this.context.getResources().getDrawable(R.drawable.btn_play_white));
		}
		else{
			this.mediaPlayer.start();
			PedometerController.getInstance().startStepDetection();

			// change Play Button to PauseIcon
			this.playerFragment.getBtnPlay().setImageDrawable(this.context.getResources().getDrawable(R.drawable.btn_pause_white));
		}
	}

	// ------------------------------------------------------------------------

	public void stopMusic() {
		this.mediaPlayer.stop();
		this.mediaPlayer.reset();
	}

	// ------------------------------------------------------------------------
	
	public void playLastTrack(){
		
		
	}
	
	// ------------------------------------------------------------------------

	public void playNextTrack(){

	}
	
	// ------------------------------------------------------------------------
	
	public void setCurrentSongPlaybackPosition(int positionPercentage){
		
		Track track = this.currentPlayingTrack;
		long duration = Math.round(Integer.parseInt(track.getDurationInMilliseconds()));
		int pos = (int)(duration * positionPercentage / 100);
		this.mediaPlayer.seekTo(pos); 
	}
	
	// ------------------------------------------------------------------------

	// Update playback time on seekbar in PlayerFragment
	public void updateProgressBar() {
		this.seekbarHandler.postDelayed(updateSeekBarTimeProgress, 100);
	}   

	// ------------------------------------------------------------------------
	
	// backround thread for Update playback time on seekbar in PlayerFragment
	private Runnable updateSeekBarTimeProgress = new Runnable() {
		
		
		public void run() {
			long totalDuration = mediaPlayer.getDuration();
			long currentDuration = mediaPlayer.getCurrentPosition();
			int progress = (int)(getProgressPercentage(currentDuration, totalDuration));
			
			playerFragment.getSongProgressSeekBar().setProgress(progress);

			// TODO
			// this will spam the logcat console
			// Running this thread after 1000 milliseconds
			seekbarHandler.postDelayed(this, 1000);
		}
	};

	// ------------------------------------------------------------------------

	// percentage Value, needed for update playback time on seekbar in PlayerFragment
	public int getProgressPercentage(long currentDuration, long totalDuration){
		Double percentage = (double) 0;

		long currentSeconds = (int) (currentDuration / 1000);
		long totalSeconds = (int) (totalDuration / 1000);

		// calculating percentage
		percentage =(((double)currentSeconds)/totalSeconds)*100;

		// return percentage
		return percentage.intValue();
	}
	
	// ------------------------------------------------------------------------
	
	public int progressToTimer(int progress, int totalDuration) {
		int currentDuration = 0;
		totalDuration = (int) (totalDuration / 1000);
		currentDuration = (int) ((((double)progress) / 100) * totalDuration);

		// return current duration in milliseconds
		return currentDuration * 1000;
	}
	
	// ------------------------------------------------------------------------
	
	// remove message Handler from updating progress bar
	public void updateSeekbarRemoveCallbacks(){
		this.seekbarHandler.removeCallbacks(updateSeekBarTimeProgress);
	}
	
	// ------------------------------------------------------------------------
	
	public void updateSeekbarPosition(){
		int totalDuration = this.mediaPlayer.getDuration();
		int currentPosition = this.progressToTimer(this.playerFragment.getSongProgressSeekBar().getProgress(), totalDuration);
		
		this.updateProgressBar();
	}
	
	// ------------------------------------------------------------------------
	
	// listen for playback end of track
	@Override
	public void onCompletion(MediaPlayer mp) {

		new CustomToast(this.context, "MP onCompletion BAMERAM", R.drawable.ic_player1, 500);
		this.playerFragment.getBtnPlay().setImageDrawable(this.context.getResources().getDrawable(R.drawable.btn_play_white));
	}
	
	// ------------------------------------------------------------------------

	public File[] getFileList(){

		File file = new File(this.prefsManager.getMusicFilepath()) ; 		
		return file.listFiles();
	}

	// ------------------------------------------------------------------------

	public void scanMusicFolder(){

		// delete Table TRACK before adding new tracks by scanning folder
		DatabaseManager.getInstance().deleteAllTracks();

		this.msg = new Message();

		this.progress = new ProgressDialog(this.context);

		progress.setMessage("Scanning music folder");
		progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		progress.setIndeterminate(false);
		progress.setCancelable(false);
		progress.setIcon(R.drawable.ic_folderscan_blue_50);

		final int filesInFolder = this.getFileList().length;
		progress.setMax(filesInFolder);
		progress.show();

		final Thread t = new Thread(){

			@Override
			public void run(){

				for(int i=1; i < filesInFolder; i++){

					List<String> musicfiles = new ArrayList<String>();
					musicfiles.add(getFileList()[i].getName());

					FFmpegMediaMetadataRetriever mmr = new FFmpegMediaMetadataRetriever();
					mmr.setDataSource(prefsManager.getMusicFilepath() + getFileList()[i].getName());
					
//					MediaMetadataRetriever mmr2 = new MediaMetadataRetriever();
//					mmr2.setDataSource(prefsManager.getMusicFilepath() + getFileList()[i].getName());
					
					String title = mmr.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_TITLE);
					String artist = mmr.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_ARTIST);
					String album = mmr.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_ALBUM);
					String year = mmr.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_DATE);
					
					//TODO
					//bpm and category are temp hard coded here
//					int bpm = 120;
//					String bpm = mmr.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_COMMENT);
					String bpm = getBpmString(getFileList()[i].getName());
					
					String category = "category";
					String mimetype = mmr.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_ENCODER);
					String filepath = getFileList()[i].getName();
					String duration = mmr.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_DURATION);

					Bitmap b = mmr.getFrameAtTime(2000000, FFmpegMediaMetadataRetriever.OPTION_CLOSEST); // frame at 2 seconds
					byte [] artwork = mmr.getEmbeddedPicture();

					mmr.release();

					DatabaseManager.getInstance().addTrack(new Track(i, title, artist, album, year, bpm, category, mimetype, filepath, duration));

					progress.setProgress(i);

					if(i == filesInFolder-1){
						progress.dismiss();

					}
				}

				// handler message
				msg.obj=filesInFolder;
				handler.sendMessage(msg);
			}

		};
		t.start();

		// start handler msg box after scanning
		handler = new Handler(new Handler.Callback() {

			@Override
			public boolean handleMessage(Message msg) {

				// Toast.makeText(context, msg.obj.toString() + " files scanned", Toast.LENGTH_LONG).show();
				String toastMsg = context.getResources().getString(R.string.dialog_label_musicplayer_libraryscan_postDialog_desc);
				new CustomToast(context, msg.obj.toString() + " " + toastMsg, R.drawable.ic_folderscan_blue_50, 600);

				// refresh Playlist after scanning
				PlaylistController.getInstance().onScannedMusicFilesChanged();

				return false;
			}
		});
	}

	// ------------------------------------------------------------------------

	public void getAllTracks(){

		List<Track> playlist = DatabaseManager.getInstance().getAllTracks();
		new CustomToast(context, playlist.size() + " files in database", R.drawable.ic_folderscan_blue_50, 600);
	}
	
	// ------------------------------------------------------------------------

	/* Checks if external storage is available for read and write */
	public boolean isExternalStorageWritable() {
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			return true;
		}
		return false;
	}

	// ------------------------------------------------------------------------

	/* Checks if external storage is available to at least read */
	public boolean isExternalStorageReadable() {
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state) ||
				Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
			return true;
		}
		return false;
	}
	
	// ------------------------------------------------------------------------
	
	public String getBpmString(String str){
		
		String bpmString = str;
		
		if(bpmString.contains("_") && bpmString.contains(".")){
			String[] parts = bpmString.split("_");
			String[] parts2 = parts[1].split("\\.");
			
			if(this.isNumeric(parts2[0])){
				return parts2[0];
			}
		}
		return "0";
	}
	
	// ------------------------------------------------------------------------
	
	//Check for numeric
	public static boolean isNumeric(String str)  
	{  
		try  {  int i = Integer.parseInt(str);  }  
		catch(NumberFormatException nfe) {  return false;  }  
		return true;  
	}

}
