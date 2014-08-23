package de.thm.fmi.musicrun.application;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import wseemann.media.FFmpegMediaMetadataRetriever;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;

public class Playlist {

	// Database
	DatabaseManager db;

	// Preferences
	PreferencesManager prefsManager;
	
	// ------------------------------------------------------------------------

	public Playlist(Context context){
		
		// DATABASE
		this.db = new DatabaseManager(context);
		
		// Preferences
		this.prefsManager = new PreferencesManager(context);

	}

	// ------------------------------------------------------------------------

	public List<String> getPlayList(){

		List<String> playlist = new ArrayList<String>();

		File file = new File(this.prefsManager.getMusicFilepath()) ;       
		File list[] = file.listFiles();

		for(int i=0; i< list.length; i++){
			playlist.add(list[i].getName());
			//				if(D) Log.d(TAG, "LIST" + i + " :" + list[i].getName());

			FFmpegMediaMetadataRetriever mmr = new FFmpegMediaMetadataRetriever();

			mmr.setDataSource(this.prefsManager.getMusicFilepath() + list[i].getName());

			String title = mmr.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_TITLE);
			String artist = mmr.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_ARTIST);
			String album = mmr.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_ALBUM);
			String year = mmr.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_DATE);
			int bpm = 120;
			String category = "category";
			String mimetype = mmr.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_ENCODER);

			Bitmap b = mmr.getFrameAtTime(2000000, FFmpegMediaMetadataRetriever.OPTION_CLOSEST); // frame at 2 seconds
			byte [] artwork = mmr.getEmbeddedPicture();

			mmr.release();


			//				MediaMetadataRetriever mmr = new MediaMetadataRetriever();
			//				mmr.setDataSource(this.musicFilepath + list[i].getName());
			//
			//				String title = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
			//				String artist = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
			//				String album = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
			//				String year = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_YEAR);
			//				int bpm = 120;
			//				String category = "category";
			//				String mimetype = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_MIMETYPE);

			db.addTrack(new Track(i, title, artist, album, year, bpm, category, mimetype));
		}



		//			if(D) Log.d(TAG, "ID3-TAG: Artist: " + artistName + ", Title: " + titleName + ", Album: " + albumName + ", Mimetype: " + bpm);


		//			db.getTrack(23);
		//			if(D) Log.i(TAG, "########################################################");

		//			db.getAllTracks();


		return playlist;
	}

	// ------------------------------------------------------------------------

	private void getPlaylist2(){

		//			List<Track> playlist = db.getAllTracks();
		//			
		//			for(int i = 0; i < playlist.size(); i++){
		//				Track t;
		//				t = this.db.getTrack(i);
		//				Log.i(TAG, t.getArtist() + " - " + t.getTitle() + " - " + t.getAlbum());
		//			}

//		ProgressDialog progress = new ProgressDialog(getActivity());
//
//		//			public void open(View view){
//		progress.setMessage("Downloading Music :) ");
//		progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
//		progress.setIndeterminate(true);
//		progress.show();
//
//		final int totalProgressTime = 100;
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


	}

}
