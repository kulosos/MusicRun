package de.thm.fmi.musicrun.application;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import wseemann.media.FFmpegMediaMetadataRetriever;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;

public class PlayerController {

	// Fragment
	Context context;
	
	// Database
	DatabaseManager db;
	ProgressDialog progress;
	
	// Preferences
	PreferencesManager prefsManager;
	
	// DEBUG
	private static final String TAG = MainActivity.class.getName();
	private static final boolean D = true;
	
	// ------------------------------------------------------------------------

	public PlayerController(Context context){
		
		this.context = context;
		
		// DATABASE
		this.db = new DatabaseManager(this.context);
		
		// Preferences
		this.prefsManager = new PreferencesManager(this.context);

	}

	// ------------------------------------------------------------------------

	public File[] getFileList(){
		
		File file = new File(this.prefsManager.getMusicFilepath()) ; 		
		return file.listFiles();
	}
	
	// ------------------------------------------------------------------------
	
	public List<String> scanMusicFolder(){

		List<String> musicfiles = new ArrayList<String>();

//		
//		this.progress = new ProgressDialog(this.context);
//
//		progress.setMessage("Scanning music folder");
//		progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
//		progress.setIndeterminate(true);
//		progress.show();
//
//		final int totalProgressTime = this.getFileList().length;
//
//		final Thread t = new Thread(){
//
//			@Override
//			public void run(){
//
//				int jumpTime = 0;
//				while(jumpTime < totalProgressTime){
//					try {
//						sleep(200);
//						jumpTime += 5;
//						progress.setProgress(jumpTime);
//						
//						
//					} catch (InterruptedException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//
//				}
//
//			}
//		};
//		t.start();
		
		
		
		
		
		
		
		
		for(int i=0; i< this.getFileList().length; i++){
			musicfiles.add(this.getFileList()[i].getName());

			FFmpegMediaMetadataRetriever mmr = new FFmpegMediaMetadataRetriever();

			mmr.setDataSource(this.prefsManager.getMusicFilepath() + this.getFileList()[i].getName());

			String title = mmr.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_TITLE);
			String artist = mmr.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_ARTIST);
			String album = mmr.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_ALBUM);
			String year = mmr.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_DATE);
			//TODO
			//bpm and category are temp hard coded here
			int bpm = 120;
			String category = "category";
			String mimetype = mmr.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_ENCODER);

			Bitmap b = mmr.getFrameAtTime(2000000, FFmpegMediaMetadataRetriever.OPTION_CLOSEST); // frame at 2 seconds
			byte [] artwork = mmr.getEmbeddedPicture();

			mmr.release();


			db.addTrack(new Track(i, title, artist, album, year, bpm, category, mimetype));
		}

		return musicfiles;
	}

	// ------------------------------------------------------------------------

//	private void getPlaylist2(){
//
//		//			List<Track> playlist = db.getAllTracks();
//		//			
//		//			for(int i = 0; i < playlist.size(); i++){
//		//				Track t;
//		//				t = this.db.getTrack(i);
//		//				Log.i(TAG, t.getArtist() + " - " + t.getTitle() + " - " + t.getAlbum());
//		//			}
//	}

}
