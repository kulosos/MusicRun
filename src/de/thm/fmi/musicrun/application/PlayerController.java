package de.thm.fmi.musicrun.application;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import de.thm.fmi.musicrun.R;
import wseemann.media.FFmpegMediaMetadataRetriever;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class PlayerController {

	private static PlayerController instance;

	// Fragment
	PlayerFragment playerFragment;
	Context context;
	
	// MediaPlayer
	private MediaPlayer mediaPlayer;
	private Track currentPlayingTrack;

	ProgressDialog progress;
	Message msg;
	Handler handler;

	// Preferences
	PreferencesManager prefsManager;

	// DEBUG
	private static final String TAG = MainActivity.class.getName();
	private static final boolean D = true;

	// ------------------------------------------------------------------------

	private PlayerController(Context context, PlayerFragment pf){

		this.context = context;
		this.playerFragment = pf;
		
		// MusicPlayer
		this.mediaPlayer = new MediaPlayer();
		
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

	public void playTrackFromPlaylist(Track track){
		
//		this.currentPlayingTrack = track;

		this.stopMusic();
		this.playMusic(track);
	
	}
	
	// ------------------------------------------------------------------------
	
	private void playMusic(Track track){

//		if(!this.mediaPlayer.isPlaying()){
			// check for external storage isReadable
			if(this.isExternalStorageReadable()){
				
				// change Play Button to PauseIcon
				this.playerFragment.getBtnPlay().setImageDrawable(this.context.getResources().getDrawable(R.drawable.btn_pause_white));

				String fileName = track.getFilepath();
				String filePath = this.prefsManager.getMusicFilepath() + fileName; 

				new CustomToast(this.context, fileName, R.drawable.ic_launcher, 400);
				
				try {
					this.mediaPlayer.setDataSource(filePath);
				} catch (Exception e) {
					e.printStackTrace();
					if(D) Log.e(TAG, e.toString());
				}

				try {
					this.mediaPlayer.prepare();
				} catch (Exception e) {
					e.printStackTrace();
					if(D) Log.e(TAG, e.toString());
				} 

				this.mediaPlayer.start();

			}
			else{
				Log.e(TAG, "EXTERNAL STORAGE IS NOT READABLE");
			}
		}
//		else{
//			this.pauseMusic();
//		}
//	}

	// ------------------------------------------------------------------------

	public void pauseMusic() {

		if(this.mediaPlayer.isPlaying()){

			// change PauseButton to PlayIcon
			this.playerFragment.getBtnPlay().setImageDrawable(this.context.getResources().getDrawable(R.drawable.btn_play_white));

			this.mediaPlayer.pause();
		}
		else{
			this.mediaPlayer.start();
		}
	}

	// ------------------------------------------------------------------------

	public void stopMusic() {

		if(this.mediaPlayer.isPlaying()){
			this.mediaPlayer.stop();
			this.mediaPlayer.reset();
		}
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

					String title = mmr.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_TITLE);
					String artist = mmr.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_ARTIST);
					String album = mmr.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_ALBUM);
					String year = mmr.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_DATE);
					//TODO
					//bpm and category are temp hard coded here
					int bpm = 120;
					String category = "category";
					String mimetype = mmr.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_ENCODER);
					String filepath = getFileList()[i].getName();

					Bitmap b = mmr.getFrameAtTime(2000000, FFmpegMediaMetadataRetriever.OPTION_CLOSEST); // frame at 2 seconds
					byte [] artwork = mmr.getEmbeddedPicture();

					mmr.release();

					DatabaseManager.getInstance().addTrack(new Track(i, title, artist, album, year, bpm, category, mimetype, filepath));

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

				//		        	Toast.makeText(context, msg.obj.toString() + " files scanned", Toast.LENGTH_LONG).show();
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

}
