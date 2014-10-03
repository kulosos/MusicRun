package de.thm.fmi.musicrun.application;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
	private MediaPlayer mediaPlayerA, mediaPlayerB;
	private Track currentPlayingTrack;
	Thread mpThreadA, mpThreadB;

	// music scan dialog
	ProgressDialog progress;
	Message musicScanResultMsg;
	public Handler scanMusicPostHandler, seekbarHandler;

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
		this.mediaPlayerA = new MediaPlayer();
		this.mediaPlayerA.setOnCompletionListener(this);
		
		// background thread for seekbar song playback updating
		this.seekbarHandler = new Handler();
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
		
		this.playerFragment.getLabelTitle().setText(track.getTitle() + track.getBpm() + " BPM");
		this.playerFragment.getLabelArtist().setText(track.getArtist());
		
	}
	
	// ------------------------------------------------------------------------
	
	public void playTrackFromPlaylist(Track track){
		
		this.currentPlayingTrack = track;
		this.stopMusic();
		this.playMusic(this.currentPlayingTrack);
		if(PreferencesManager.getInstance().isAutostartPedometer()){
			PedometerController.getInstance().startStepDetection();
		}
	}
	
	// ------------------------------------------------------------------------
	
	private void playMusic(Track track){

		// check for external storage isReadable
		if(this.isExternalStorageReadable()){

			// change Play Button to PauseIcon
			this.playerFragment.getBtnPlay().setImageDrawable(this.context.getResources().getDrawable(R.drawable.btn_pause_white));

			String fileName = track.getFilepath();
			String filePath = PreferencesManager.getInstance().getMusicFilepath() + fileName; 

			new CustomToast(this.context, fileName, R.drawable.ic_launcher, 400);

			try {
				this.mediaPlayerA.setDataSource(filePath);
				this.mediaPlayerA.prepare();
				this.mediaPlayerA.start();

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
				
		if(this.mediaPlayerA.isPlaying()){
			
			this.mediaPlayerA.pause();

			PedometerController.getInstance().pauseStepDetection();

			// change PauseButton to PlayIcon
			this.playerFragment.getBtnPlay().setImageDrawable(this.context.getResources().getDrawable(R.drawable.btn_play_white));
		}
		else{
			this.mediaPlayerA.start();
			if(PreferencesManager.getInstance().isAutostartPedometer()){
				PedometerController.getInstance().startStepDetection();
			}
			// change Play Button to PauseIcon
			this.playerFragment.getBtnPlay().setImageDrawable(this.context.getResources().getDrawable(R.drawable.btn_pause_white));
		}
	}

	// ------------------------------------------------------------------------

	public void stopMusic() {
		this.mediaPlayerA.stop();
		this.mediaPlayerA.reset();
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
		this.mediaPlayerA.seekTo(pos); 
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
			long totalDuration = mediaPlayerA.getDuration();
			long currentDuration = mediaPlayerA.getCurrentPosition();
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
		int totalDuration = this.mediaPlayerA.getDuration();
		int currentPosition = this.progressToTimer(this.playerFragment.getSongProgressSeekBar().getProgress(), totalDuration);
		
		this.updateProgressBar();
	}
	
	// ------------------------------------------------------------------------
	
	// listen playback for end of track
	@Override
	public void onCompletion(MediaPlayer mp) {

//		new CustomToast(this.context, "MP onCompletion BAMERAM", R.drawable.ic_player1, 500);
		this.playerFragment.getBtnPlay().setImageDrawable(this.context.getResources().getDrawable(R.drawable.btn_play_white));
		
		// search the track from tracklist, which bpm values is the closest to the lastPace
		List<Track> tracks = PlaylistController.getInstance().getTracks();
		int nextTrack = this.findBestMatchingTrack(tracks);
		Track track = tracks.get(nextTrack);
		this.playTrackFromPlaylist(track);

		// notify observer (this) directly
		this.updateCurrentPlayingTrack(tracks.get(nextTrack));
	}
	
	// ------------------------------------------------------------------------
	
	public int findBestMatchingTrack(List<Track> trackList){
		
		List<Track> tracks = trackList;
		int lastPace = PedometerController.getInstance().getStepsPerIntervallHistory().get(PedometerController.getInstance().getStepsPerIntervallHistory().size()-1);
		int playNext = -1;
		
		if(lastPace > 0){
			
			// Track trackWithClostestBpm;
			int closestValue = 10000;

			for(int i=0; i < tracks.size(); i++){

				int bpm = Integer.parseInt(tracks.get(i).getBpm());

				// search the track from tracklist, which bpm values is the closest to the lastPace
				if(Math.abs(bpm - lastPace) < closestValue){
					closestValue = Math.abs(bpm - lastPace);
					// save the closest track
					playNext = i;
				}
			}
		}
		
		// play next track
		if(playNext == -1){
			// getRandom
			return this.getRandomRangeInt(0, tracks.size());
		}else{
			// play the best matching bpm track
			return playNext;
		}
		
	}
	
	// ------------------------------------------------------------------------

	public File[] getFileList(){

		File file = new File(PreferencesManager.getInstance().getMusicFilepath()) ; 		
		return file.listFiles();
	}

	// ------------------------------------------------------------------------

	public void scanMusicFolder(){

		// delete Table TRACK before adding new tracks by scanning folder
		DatabaseManager.getInstance().deleteAllTracks();

		this.musicScanResultMsg = new Message();

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
					mmr.setDataSource(PreferencesManager.getInstance().getMusicFilepath() + getFileList()[i].getName());
					
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
				musicScanResultMsg.obj=filesInFolder;
				scanMusicPostHandler.sendMessage(musicScanResultMsg);
			}
		};
		t.start();

		// start handler msg box after scanning
		scanMusicPostHandler = new Handler(new Handler.Callback() {

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
	
	// ------------------------------------------------------------------------
	
	public int getRandomRangeInt(int min, int max) {
		int randomNum = 0;
	    Random rand = new Random();
	    randomNum = rand.nextInt((max - min) + 1) + min;
	    return randomNum;
	}

}
