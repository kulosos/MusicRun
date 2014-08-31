package de.thm.fmi.musicrun.application;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import de.thm.fmi.musicrun.R;
import wseemann.media.FFmpegMediaMetadataRetriever;
import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.widget.ImageView;

public class PlayerController {

	// Fragment
	PlayerFragment playerFragment;
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

	// Constructor
	public PlayerController(Context context){
		
		this.context = context;
		// DATABASE
		this.db = new DatabaseManager(this.context);
		// Preferences
		this.prefsManager = new PreferencesManager(this.context);

	}
	
	// Override Constructor
	public PlayerController(Context context, PlayerFragment fragment){
		
		this.playerFragment = fragment;
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

		// delete Table TRACK before adding new tracks by scanning folder
		db.deleteAllTracks();
		
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
		        	new CustomToast(context, msg.obj.toString() + " " + toastMsg, R.drawable.ic_folderscan_blue_50, 600);
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
//		for(int i = 0; i < playlist.size(); i++){
//			Track t;
//			t = this.db.getTrack(i);
//			Log.i(TAG, t.getArtist() + " - " + t.getTitle() + " - " + t.getAlbum());
//		}
		
		new CustomToast(context, playlist.size() + " files in database", R.drawable.ic_folderscan_blue_50, 600);
	}

	// ------------------------------------------------------------------------
	
	public void getPlaylistFragment(){
		
		// Create new fragment and transaction
		PlaylistFragment newFragment = new PlaylistFragment();
		android.app.FragmentTransaction transaction = ((Activity) this.context).getFragmentManager().beginTransaction();
		
		// Replace whatever is in the fragment_container view with this fragment,
		// and add the transaction to the back stack
		transaction.replace(R.id.player_frame, newFragment);

		transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
		
		transaction.addToBackStack(null);
		
		// Commit the transaction
		transaction.commit();
		
		//TODO
		// this is pretty dirty coded;
		// because the replacing of the playerFragment replaces only the parent node in xml
		// not the children. so here every single child is deactivating here 
		this.playerFragment.getBtnLast().setVisibility(ImageView.INVISIBLE);
		this.playerFragment.getBtnNext().setVisibility(ImageView.INVISIBLE);
		this.playerFragment.getBtnPlay().setVisibility(ImageView.INVISIBLE);
		this.playerFragment.getBtnList().setVisibility(ImageView.INVISIBLE);
		this.playerFragment.getBtnTrackImage().setVisibility(ImageView.INVISIBLE);

	}
}
