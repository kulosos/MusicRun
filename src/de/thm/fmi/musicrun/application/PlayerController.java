package de.thm.fmi.musicrun.application;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import de.thm.fmi.musicrun.R;
import de.thm.fmi.musicrun.bpmDetecation.BPMDetectionManager;
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
	private Context context;
	
	// MediaPlayer
	private MediaPlayer mediaPlayerA, mediaPlayerB;
	private Thread mpThreadA, mpThreadB;
	public enum PlayerId { A, B }
	private Track currentPlayingTrack;
	private PlayerId activePlayerThread = PlayerId.A;
	private int pitchFactor = 0;
	
	// Volume control
	private static final float VOLUME_MIN = 0.0f;
	private static final float VOLUME_MAX = 1.0f;
	private float fadingDuration = 5000; // default milliseconds
	private long delayTime = 100; // default milliseconds
	private float changeValue = VOLUME_MAX / (fadingDuration / (float)delayTime);
	private float volumeMpOut, volumeMpIn;
	private boolean isFading = false;
	
	// music scan dialog
	private ProgressDialog progress;
	private Message musicScanResultMsg, fadingPostMsg;
	public Handler scanMusicPostHandler, seekbarHandler, postFadingHandler, onCompletionHandler;
	
	// DEBUG
	private static final String TAG = MainActivity.class.getName();
	private static final boolean D = false;

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
		
		// MusicPlayer (two threads)
		this.mediaPlayerA = new MediaPlayer();
		this.mediaPlayerB = new MediaPlayer();
		this.mediaPlayerA.setOnCompletionListener(this);
		this.mediaPlayerB.setOnCompletionListener(this);
		
		// background thread for seekbar song playback updating
		this.seekbarHandler = new Handler();
		
		// start on completion listener
		this.onCompletionHandler = new Handler();
		this.onCompletionHandler.postDelayed(onCompletionListenerThread, 1000);
		
		BPMDetectionManager.initInstance(this.context);
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
		this.playerFragment.getLabelDuration().setText(track.getDurationAsFormattedString());
		this.playerFragment.getLabelBPM().setText(track.getBpm());
		
		this.playerFragment.getBtnPlay().setImageDrawable(this.context.getResources().getDrawable(R.drawable.btn_pause_white));
	}

	// ------------------------------------------------------------------------

	public void prepareMusicPlayerThread(Track track){

		this.currentPlayingTrack = track;
		if(PreferencesManager.getInstance().isAutostartPedometer())	PedometerController.getInstance().startStepDetection();

		// PlayerA and PlayerB BOTH NOT PLAYING
		if(!this.mediaPlayerA.isPlaying() && !this.mediaPlayerB.isPlaying()){
			if(D) Log.i(TAG, "BOTH ARE STOPPED");
			this.stopMediaPlayer(PlayerId.A);
			this.startMusicPlayerThread(PlayerId.A);
			return;
		}
		
		// Player A ISPLAYING
		if(this.mediaPlayerA.isPlaying() && !this.mediaPlayerB.isPlaying()){
			if(D) Log.i(TAG, "PLAYER_A IS PLAYING. START PLAYER B");
			this.stopMediaPlayer(PlayerId.B);
			this.startMusicPlayerThread(PlayerId.B);
			this.mediaPlayerB.setVolume(VOLUME_MIN, VOLUME_MIN);
			this.crossFade(this.mediaPlayerA, this.mediaPlayerB);
			return;
		}
		
		// Player B ISPLAYLING
		if(!this.mediaPlayerA.isPlaying() && this.mediaPlayerB.isPlaying()){
			if(D) Log.i(TAG, "PLAYER_B IS PLAYING. START PLAYER A.");
			this.stopMediaPlayer(PlayerId.A);
			this.startMusicPlayerThread(PlayerId.A);
			this.mediaPlayerA.setVolume(VOLUME_MIN, VOLUME_MIN);
			this.crossFade(this.mediaPlayerB, this.mediaPlayerA);
			return;
		}

		// BOTH Players ARE PLAYING SIMULTANEOUSLY (e.g. while crossfading)
		if(this.mediaPlayerA.isPlaying() && this.mediaPlayerB.isPlaying() && this.isFading){
			if(D) Log.i(TAG, "BOTH MEDIAPLAYERS ARE PLAYING");
			
			if(activePlayerThread.equals(PlayerId.A)){
				this.stopMediaPlayer(PlayerId.B);
				this.startMusicPlayerThread(PlayerId.B);
				this.mediaPlayerB.setVolume(VOLUME_MIN, VOLUME_MIN);
				this.crossFade(this.mediaPlayerA, this.mediaPlayerB);
				return;
			}
		
			if(activePlayerThread.equals(PlayerId.B)){
				this.stopMediaPlayer(PlayerId.A);
				this.startMusicPlayerThread(PlayerId.A);
				this.mediaPlayerA.setVolume(VOLUME_MIN, VOLUME_MIN);
				this.crossFade(this.mediaPlayerB, this.mediaPlayerA);
				return;
			}
		}
	}
	
	// ------------------------------------------------------------------------

	private void startMusicPlayerThread(PlayerId playerId){
	
		if(!isFading){
			if(playerId.equals(PlayerId.A)){
				this.mpThreadA = new Thread(){
					@Override
					public void run() {
						playMusic(currentPlayingTrack, PlayerId.A);
						activePlayerThread = PlayerId.A;
					}
				};
				this.mpThreadA.start();
				return;
			}

			if(playerId.equals(PlayerId.B)){
				this.mpThreadB = new Thread(){
					@Override
					public void run() {
						playMusic(currentPlayingTrack, PlayerId.B);
						activePlayerThread = PlayerId.B;
					}
				};
				this.mpThreadB.start();
				return;
			}
		}
	}
	
	// ------------------------------------------------------------------------
	
	private void playMusic(Track track, PlayerId playerId){
		
		// check for external storage isReadable
		if(this.isExternalStorageReadable()){

			// change Play Button to PauseIcon
//			this.playerFragment.getBtnPlay().setImageDrawable(this.context.getResources().getDrawable(R.drawable.btn_pause_white));

			String fileName = track.getFilepath();
			String filePath = PreferencesManager.getInstance().getMusicFilepath() + fileName; 

			File f = new File(PreferencesManager.getInstance().getMusicFilepath());
			if(f.isDirectory()){
				try {
					if(playerId.equals(PlayerId.A)){
						this.mediaPlayerA.setDataSource(filePath);
						this.mediaPlayerA.prepare();
						this.mediaPlayerA.start();
					}
					if(playerId.equals(PlayerId.B)){
						this.mediaPlayerB.setDataSource(filePath);
						this.mediaPlayerB.prepare();
						this.mediaPlayerB.start();
					}

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
				if(D) Log.e(TAG, "ERROR: Music folder doesn't exisit.");
			}

		}
		else{
			Log.e(TAG, "EXTERNAL STORAGE IS NOT READABLE");
		}
	}

	// ------------------------------------------------------------------------

	public void pauseMusic() {
		
		// BOTH MEDIA PLAYERS ARE PLAYING (while crossfading)
		if(this.mediaPlayerA.isPlaying() && this.mediaPlayerB.isPlaying()){
			this.mediaPlayerA.pause();
			this.mediaPlayerB.pause();
			PedometerController.getInstance().pauseStepDetection();
			this.playerFragment.getBtnPlay().setImageDrawable(this.context.getResources().getDrawable(R.drawable.btn_play_white));
			return;
		}

		// ONE MEDIA PLAYER IS PLAYING
		if(this.mediaPlayerA.isPlaying()){
			this.mediaPlayerA.pause();
			PedometerController.getInstance().pauseStepDetection();
			this.playerFragment.getBtnPlay().setImageDrawable(this.context.getResources().getDrawable(R.drawable.btn_play_white));
			return;
		}

		if(this.mediaPlayerB.isPlaying()){
			this.mediaPlayerB.pause();
			PedometerController.getInstance().pauseStepDetection();
			this.playerFragment.getBtnPlay().setImageDrawable(this.context.getResources().getDrawable(R.drawable.btn_play_white));
			return;
		}

		// MEDIA PLAYER IS NOT PLAYING / STOPPED
		if(!this.mediaPlayerA.isPlaying() && this.currentPlayingTrack != null && this.activePlayerThread.equals(PlayerId.A)){
			this.mediaPlayerA.start();
			if(PreferencesManager.getInstance().isAutostartPedometer()){
				PedometerController.getInstance().startStepDetection();
			}
			this.playerFragment.getBtnPlay().setImageDrawable(this.context.getResources().getDrawable(R.drawable.btn_pause_white));
			return;
		}

		if(!this.mediaPlayerB.isPlaying() && this.currentPlayingTrack != null && this.activePlayerThread.equals(PlayerId.B)){
			this.mediaPlayerB.start();
			if(PreferencesManager.getInstance().isAutostartPedometer()){
				PedometerController.getInstance().startStepDetection();
			}
			this.playerFragment.getBtnPlay().setImageDrawable(this.context.getResources().getDrawable(R.drawable.btn_pause_white));
			return;
		}

		// INITIAL PLAYER STATE - BOTH MEDIA PLAYERS ARE NOT PLAYING
		if(!this.mediaPlayerA.isPlaying() && this.currentPlayingTrack == null && this.activePlayerThread.equals(PlayerId.A)){
			this.playRandomTrack();
			if(PreferencesManager.getInstance().isAutostartPedometer()){
				PedometerController.getInstance().startStepDetection();
			}
			this.playerFragment.getBtnPlay().setImageDrawable(this.context.getResources().getDrawable(R.drawable.btn_pause_white));
			return;
		}

		if(!this.mediaPlayerB.isPlaying() && this.currentPlayingTrack == null && this.activePlayerThread.equals(PlayerId.B)){
			this.playRandomTrack();
			if(PreferencesManager.getInstance().isAutostartPedometer()){
				PedometerController.getInstance().startStepDetection();
			}
			this.playerFragment.getBtnPlay().setImageDrawable(this.context.getResources().getDrawable(R.drawable.btn_pause_white));
			return;
		}	
	}

	// ------------------------------------------------------------------------

	public void stopMusic() {
		
		// stop both player threads
		if(this.mediaPlayerA != null){
			this.mediaPlayerA.stop();
			this.mediaPlayerA.reset();
		}

		if(this.mediaPlayerB != null){
			this.mediaPlayerB.stop();
			this.mediaPlayerB.reset();
		}
	}
	
	// ------------------------------------------------------------------------
	
	public void stopMediaPlayer(PlayerId playerId){
		
		if(playerId.equals(PlayerId.A)){
			if(this.mediaPlayerA != null){
				try{
					this.mediaPlayerA.stop();
					this.mediaPlayerA.reset();
				}catch(IllegalStateException e){
					e.printStackTrace();
					if(D) Log.e(TAG, e.toString());
				}
			}
		}
		if(playerId.equals(PlayerId.B)){
			if(this.mediaPlayerB != null){
				try{
					this.mediaPlayerB.stop();
					this.mediaPlayerB.reset();
				}catch(IllegalStateException e){
					e.printStackTrace();
					if(D) Log.e(TAG, e.toString());
				}
			}
		}
	}
	
	// ------------------------------------------------------------------------
	
	public void stopMusicPlayerThread(PlayerId playerId){
		
		if(playerId.equals(PlayerId.A)){
			this.mediaPlayerA.stop();
			this.mediaPlayerA.reset();
		}
		
		if(playerId.equals(PlayerId.B)){
			this.mediaPlayerB.stop();
			this.mediaPlayerB.reset();
		}
	}

	// ------------------------------------------------------------------------
	
	private void crossFade(MediaPlayer mediaPlayerFadeOut, MediaPlayer mediaPlayerFadeIn){
		
		final MediaPlayer mpOut = mediaPlayerFadeOut;
		final MediaPlayer mpIn = mediaPlayerFadeIn;
		
		if(!isFading){
			this.volumeMpOut = 1.0f;
			this.volumeMpIn = 0.0f;
		}
//		mpOut.setVolume(volumeMpOut, volumeMpOut);
//		mpIn.setVolume(volumeMpIn, volumeMpIn);
		
		// calculate values
		this.fadingDuration = (float)PreferencesManager.getInstance().getCrossfadingDuration()*1000; // parse to milliseconds
		this.delayTime = 100; // milliseconds
		this.changeValue = VOLUME_MAX / (fadingDuration / (float)delayTime);
		
		final Thread t = new Thread(){

			@Override
			public void run(){
				
				while(volumeMpOut > VOLUME_MIN && volumeMpIn < VOLUME_MAX){
					try {
						sleep(delayTime);
						isFading = true;
						if(isFading) if(D)Log.i(TAG, "DEBUG fading " + isFading);
						//fade out
						mpOut.setVolume(volumeMpOut, volumeMpOut);
						volumeMpOut = Math.max(0, Math.min(1, volumeMpOut - changeValue)); // clamped between 0 and 1
						// fade in
						mpIn.setVolume(volumeMpIn, volumeMpIn);
						volumeMpIn = Math.max(0, Math.min(1, volumeMpIn + changeValue)); // clamped between 0 and 1
						
						if(D)Log.i(TAG, "volumeOut: " + volumeMpOut);
						if(D)Log.i(TAG, "volumeIn: " + volumeMpIn);
						
//						if(activePlayerThread.equals(PlayerId.A))if(D)Log.i(TAG, "DEBUG PLAYER THREAD A");
//						if(activePlayerThread.equals(PlayerId.B))if(D)Log.i(TAG, "DEBUG PLAYER THREAD B");
						
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				mpOut.stop();
				mpOut.reset();
				isFading = false;
				if(!isFading) if(D)Log.i(TAG, "DEBUG --------- fading ready");
			}
		};
		t.start();	
		
		
	}
		
	// ------------------------------------------------------------------------
	
	public void playLastTrack(){
		
//		this.stopMusic();
//		this.stopMusicPlayerThread(PlayerId.A);
//		this.stopMusicPlayerThread(PlayerId.B);
		
//		this.mpThreadA.stop();
//		this.mpThreadB.stop();
		
		this.playRandomTrack();
		
	}
	
	// ------------------------------------------------------------------------

	public void playNextTrack(){
		
		this.playRandomTrack();
	}

	// ------------------------------------------------------------------------
	
	public void playRandomTrack(){
		
		if(PlaylistController.getInstance().getTracks() != null && PlaylistController.getInstance().getTracks().size() > 0){

			// get random track
			Track track = PlaylistController.getInstance().getTracks().get(this.getRandomRangeInt(0, PlaylistController.getInstance().getTracks().size())); 
			this.prepareMusicPlayerThread(track);

			// notify observer (this) directly
			this.updateCurrentPlayingTrack(track);
		}
	}
	
	// ------------------------------------------------------------------------
	
	// set seekbar to current time position
	public void setCurrentSongPlaybackPosition(int positionPercentage){
		
		Track track = this.currentPlayingTrack;
	
		long duration = Math.round(Integer.parseInt(track.getDurationInMilliseconds()));
		int pos = (int)(duration * positionPercentage / 100);
		
		if(this.activePlayerThread.equals(PlayerId.A)){
			this.mediaPlayerA.seekTo(pos); 
		}
		if(this.activePlayerThread.equals(PlayerId.B)){
			this.mediaPlayerB.seekTo(pos); 
		}
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
			
			if(activePlayerThread.equals(PlayerId.A)){
				long totalDuration = mediaPlayerA.getDuration();
				long currentDuration = mediaPlayerA.getCurrentPosition();
				int progress = (int)(getProgressPercentage(currentDuration, totalDuration));
				playerFragment.getSongProgressSeekBar().setProgress(progress);
			}
			
			if(activePlayerThread.equals(PlayerId.B)){
				long totalDuration = mediaPlayerB.getDuration();
				long currentDuration = mediaPlayerB.getCurrentPosition();
				int progress = (int)(getProgressPercentage(currentDuration, totalDuration));
				playerFragment.getSongProgressSeekBar().setProgress(progress);
			}
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
		// this is only useable for using of one mediaplayer only
		// for more than one mediaplayer object you need another solution
		// >> Runnable onCompletionListenerThread
	}

	// ------------------------------------------------------------------------
	
	// own implementation of listening playback end of track
	private	Runnable onCompletionListenerThread = new Runnable() {
		@Override
		public void run() {

			if(currentPlayingTrack != null){
				if(activePlayerThread.equals(PlayerId.A)){

					int endOfTrack = mediaPlayerA.getDuration()- (PreferencesManager.getInstance().getCrossfadingDuration()*1000)+1000;

					if(mediaPlayerA.getCurrentPosition() >= endOfTrack && !isFading){
						
						// search the track from tracklist, which bpm values is the closest to the lastPace
						List<Track> tracks = PlaylistController.getInstance().getTracks();
						int nextTrack = findBestMatchingTrack(tracks);
						Track track = tracks.get(nextTrack);
						prepareMusicPlayerThread(track);

						// notify observer (this) directly
						updateCurrentPlayingTrack(tracks.get(nextTrack));
					}	
				}

				if(activePlayerThread.equals(PlayerId.B)){

					int endOfTrack = mediaPlayerB.getDuration()- (PreferencesManager.getInstance().getCrossfadingDuration()*1000)+1000;

					if(mediaPlayerB.getCurrentPosition() >= endOfTrack && !isFading){

						// search the track from tracklist, which bpm values is the closest to the lastPace
						List<Track> tracks = PlaylistController.getInstance().getTracks();
						int nextTrack = findBestMatchingTrack(tracks);
						Track track = tracks.get(nextTrack);
						prepareMusicPlayerThread(track);

						// notify observer (this) directly
						updateCurrentPlayingTrack(tracks.get(nextTrack));
					}	
				}
			}
			
			onCompletionHandler.postDelayed(this, 1000);
		}
	};

	// ------------------------------------------------------------------------
	
	public int findBestMatchingTrack(List<Track> trackList){
		
		List<Track> tracks = trackList;
		int lastPace = 0;
		if(PedometerController.getInstance().getStepsPerIntervallHistory().size() > 0){
			lastPace = PedometerController.getInstance().getStepsPerIntervallHistory().get(PedometerController.getInstance().getStepsPerIntervallHistory().size()-1);
		}
		int playNext = -1;
		
		if(lastPace > 0){
			
			// Track trackWithClostestBpm;
			int closestValue = 10000;

			for(int i=0; i < tracks.size(); i++){

				int bpm = Integer.parseInt(tracks.get(i).getBpm()) + this.pitchFactor;

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
			return this.getRandomRangeInt(0, tracks.size()-1);
		}else{
			// play the best matching bpm track
			return playNext;
		}
	}
	
	// ------------------------------------------------------------------------
	
	public void pitchBPM(int pitch){
		
		this.pitchFactor = this.pitchFactor + (pitch);
		playerFragment.getLabelPitch().setText(Integer.toString(this.pitchFactor));
	}
	
	// ------------------------------------------------------------------------

	public File[] getFileList(){

		File file = new File(PreferencesManager.getInstance().getMusicFilepath()) ; 		
		return file.listFiles();
	}

	// ------------------------------------------------------------------------

	public void scanMusicFolder(){

		// Check if Folder exists
		File f = new File(PreferencesManager.getInstance().getMusicFilepath());
		if(f.isDirectory()){

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
						//					mmr2.setDataSource(PreferencesManager.getInstance().getMusicFilepath() + getFileList()[i].getName());

						String title = mmr.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_TITLE);
						String artist = mmr.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_ARTIST);
						String album = mmr.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_ALBUM);
						String year = mmr.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_DATE);

						//TODO
						//bpm and category are temp hard coded here
//						int bpm = 120;
//						String bpm = mmr.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_COMMENT);
//						String bpm = mmr.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_ENCODED_BY);
//						String bpm = mmr2.extractMetadata(MediaMetadataRetriever.);
						
						// Fallback: get the BPM from Filename (seek for expression "_123.mp3")
						String bpm = getBpmString(getFileList()[i].getName());

						String category = "category";
						String mimetype = mmr.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_ENCODER);
						String filepath = getFileList()[i].getName();
						String duration = mmr.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_DURATION);
						
						Bitmap b = mmr.getFrameAtTime(2000000, FFmpegMediaMetadataRetriever.OPTION_CLOSEST); // frame at 2 seconds
						byte [] artwork = mmr.getEmbeddedPicture();

						mmr.release();
						
						Track t = new Track(i, title, artist, album, year, bpm, category, mimetype, filepath, duration);
						DatabaseManager.getInstance().addTrack(t);

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
		else{
			new CustomToast(this.context, "Error\n\nMusic folder doesn't exisit.", R.drawable.ic_launcher, 800);
			Log.e(TAG, "ERROR: Music folder doesn't exisit.");
		}
	}

	// ------------------------------------------------------------------------

	public void dbCheck(){

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
