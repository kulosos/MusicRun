package de.thm.fmi.musicrun.application;

import java.util.List;

import de.thm.fmi.musicrun.R;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

public class PlaylistController {

	private static PlaylistController instance;
	
	private Context context;
	public PlaylistFragment plf;
	
	private List<Track> tracks;
	private String[] titles, artists; 

	// DEBUG
	private static final String TAG = MainActivity.class.getName();
	private static final boolean D = true;
	
	// ------------------------------------------------------------------------

	private PlaylistController(PlaylistFragment plf, Context context){
		
		this.plf = plf;
		this.context = context;	
		
		// Get TrackList form database
		this.tracks = DatabaseManager.getInstance().getAllTracks();
		
		this.setTrackList();
	}
	
	// ------------------- SINGLETON METHODS ----------------------------------
	
	public static void initInstance(PlaylistFragment playlistFragment, Context context){
		if(instance == null){
			instance = new PlaylistController(playlistFragment, context);
		}
	}

	public static PlaylistController getInstance(){
		return instance;
	}
	
	// ------------------------------------------------------------------------

	public void initPlaylist(){
		
		//get titles and artists lists
		this.titles = this.getTitles();
		this.artists = this.getArtists();

		// set PlayListAdapter
		PlaylistAdapter adapter = new PlaylistAdapter(this.context, this.titles, this.artists);

		this.plf.getPlaylistView().setAdapter(adapter);

		// set list item listener
		this.plf.getPlaylistView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
				Toast.makeText(getInstance().context, titles[+ position] +" - "+ artists[+ position], Toast.LENGTH_SHORT).show();
			}
		});
	}
	
	// ------------------------------------------------------------------------
	
	// deletes the title and artists lists
	public void clearPlaylist(){
		this.titles = new String[0];
		this.artists = new String[0];
	}
	
	// ------------------------------------------------------------------------
	
	// called in settings dialog when music is renew scanning 
	public void onScannedMusicFilesChanged() {

		if(D) Log.i(TAG, "SCANNING ON CHANGED");
		
//		this.clearPlaylist();
		if(D) Log.i(TAG, "SCANNING ON CHANGED - BEFORE " + this.tracks.size() + " und " + this.titles.length);
		// Get current TrackList form database
		this.tracks = DatabaseManager.getInstance().getAllTracks();
		
		if(D) Log.i(TAG, "SCANNING ON CHANGED - AFTER " + this.tracks.size() + " und " + this.titles.length);
		
		this.setTrackList();
		this.initPlaylist();
	}
	
	// ------------------------------------------------------------------------
	
	// sets the arrays titles and artist 
	public void setTrackList(){

		this.titles = new String[this.tracks.size()];
		this.artists = new String[this.tracks.size()];

		for(int i = 0; i < this.tracks.size(); i++){
			this.titles[i] = this.tracks.get(i).getTitle();
			this.artists[i] = this.tracks.get(i).getArtist();
		}
	}
	
	// ------------------------------------------------------------------------
	
	// ListItemClickEvent
	public void playSelectedTrack(long id){
	
		new CustomToast(this.context, artists[(int) id] +" - "+ titles[(int) id], R.drawable.ic_launcher, 600);     
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
