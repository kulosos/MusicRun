package de.thm.fmi.musicrun.application;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import de.thm.fmi.musicrun.R;
import wseemann.media.FFmpegMediaMetadataRetriever;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

public class PlayerController {

	// Fragment
	Context context;
	
	// Database
	DatabaseManager db;
	ProgressDialog progress;
	Message msg;
	Handler handler;
	
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
	
	public void scanMusicFolder(){

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

					Bitmap b = mmr.getFrameAtTime(2000000, FFmpegMediaMetadataRetriever.OPTION_CLOSEST); // frame at 2 seconds
					byte [] artwork = mmr.getEmbeddedPicture();

					mmr.release();

					db.addTrack(new Track(i, title, artist, album, year, bpm, category, mimetype));
					
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
		        	new CustomToast(context, msg.obj.toString() + " " + toastMsg, R.drawable.ic_folderscan_blue_50, 300);
		        return false;
		    }
		});

	}

	// ------------------------------------------------------------------------

	public void getAllTracks(){

		List<Track> playlist = db.getAllTracks();
		if(D) Log.i(TAG, "#########################################################");
		if(D) Log.i(TAG, "Database tuple: " + playlist.size());
		if(D) Log.i(TAG, "#########################################################");
		for(int i = 0; i < playlist.size(); i++){
			Track t;
			t = this.db.getTrack(i);
			Log.i(TAG, t.getArtist() + " - " + t.getTitle() + " - " + t.getAlbum());
		}
	}

}
