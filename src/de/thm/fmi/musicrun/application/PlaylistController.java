package de.thm.fmi.musicrun.application;

import java.util.List;

import de.thm.fmi.musicrun.R;
import android.content.Context;
import android.util.Log;

public class PlaylistController {

	CustomPlaylistFragment plf;
	Context context;
	
	// DATABASE
	public DatabaseManager db;
	public List<Track> tracks;

	String[] titles, artists; /*= new String[] { "one", "two", "three", "four",  
			"five", "six", "seven", "eight", "nine", "ten", "eleven",  
			"twelve", "thirteen", "fourteen", "fifteen" };  */

	String[] numbers_digits = new String[] { "1", "2", "3", "4", "5", "6", "7",  
			"8", "9", "10", "11", "12", "13", "14", "15" };  

	
	// DEBUG
	private static final String TAG = MainActivity.class.getName();
	private static final boolean D = true;
	
	// ------------------------------------------------------------------------
	
	public PlaylistController(){
	}
	
	public PlaylistController(CustomPlaylistFragment plf, Context context){
		
		this.plf = plf;
		this.context = context;	
		
		
		// Get TrackList form database
		this.db = new DatabaseManager(this.context);
		this.tracks = db.getAllTracks();
		
		this.setTrackList();
		
	}
	
	// ------------------------------------------------------------------------

	public void onScannedMusicFilesChanged(Context context) {

		if(D) Log.i(TAG, "SCANNING ON CHANGED");
		
		// update playlist
		this.db = new DatabaseManager(context);
		this.tracks = this.db.getAllTracks();

		this.setTrackList();
	}
	
	// ------------------------------------------------------------------------
	
	public void playSelectedTrack(long id){
	
		new CustomToast(this.context, artists[(int) id] +" - "+ titles[(int) id], R.drawable.ic_launcher, 600);     
	}
	
	// ------------------------------------------------------------------------
	
	public void setTrackList(){
		
		this.titles = new String[this.tracks.size()];
		this.artists = new String[this.tracks.size()];
		
		for(int i = 0; i < this.tracks.size(); i++){

			this.titles[i] = this.tracks.get(i).getTitle();
			this.artists[i] = this.tracks.get(i).getArtist();
		}

	}
	
	// ------------------------------------------------------------------------
//	
//	public Track[] getListData(){
//		
//		if(D) Log.i(TAG, "TRACKLIST LIST SIZE: " + this.tracks.size());
//		
//		Track[] listData = new Track[this.tracks.size()];
//		
//		if(D) Log.i(TAG, "TRACKLIST ARRAY SIZE: " + listData.length);
//		
//		for(int i = 0; i < listData.length; i++){
//			
//			// convert the List<Track> to Track[]
//			// is needed for setting the List adapter
//			listData[i] = this.tracks.get(i);	
//		}
//		
//		return listData;
//	}
//	
	
	// ------------------------------------------------------------------------

	public DatabaseManager getDb() {
		return db;
	}

	public void setDb(DatabaseManager db) {
		this.db = db;
	}

	public List<Track> getTracks() {
		return tracks;
	}

	public void setTracks(List<Track> tracks) {
		this.tracks = tracks;
	}

	public String[] getTitles() {
		return titles;
	}

	public void setTitles(String[] titles) {
		this.titles = titles;
	}

	public String[] getArtists() {
		return artists;
	}

	public void setArtists(String[] artists) {
		this.artists = artists;
	}
	
	// ------------------------------------------------------------------------
	
	
}
