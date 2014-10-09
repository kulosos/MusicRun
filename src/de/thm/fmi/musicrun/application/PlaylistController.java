package de.thm.fmi.musicrun.application;

import java.util.List;
import java.util.Vector;
import android.content.Context;
import android.view.View;
import android.widget.AdapterView;

public class PlaylistController {

	private static PlaylistController instance;
	
	private Context context;
	public PlaylistFragment plf;
	
	private List<Track> tracks;
	private String[] titles, artists, durations, bpms; 

	// Observer
	public Vector observers;
	
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

		// instantiate observer vector
		this.observers = new Vector();
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

	// ------------------------- OBSERVER -------------------------------------

	public void attachObserver(IPlaylistObserver sdo){
		this.observers.addElement(sdo);
	}

	public void detachObserver(IPlaylistObserver sdo){
		observers.removeElement(sdo);
	}

	public void notifyObserver(Track track){
			
		for (int i=0; i< observers.size(); i++) {
			((IPlaylistObserver)(observers.elementAt(i))).updateCurrentPlayingTrack(track);
		}
	}
	
	// ------------------------------------------------------------------------

	public void initPlaylist(){
		
		//get titles and artists lists
//		this.titles = this.getTitles();
//		this.artists = this.getArtists();

		// set PlayListAdapter
		PlaylistAdapter adapter = new PlaylistAdapter(this.context, this.titles, this.artists, this.durations, this.bpms);

		this.plf.getPlaylistView().setAdapter(adapter);

		// set list item listener
		this.plf.getPlaylistView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
				playSelectedTrack(id);
				//Toast.makeText(getInstance().context,tracks.get(position).getFilepath(), Toast.LENGTH_SHORT).show();
			}
		});
	}
	
	// ------------------------------------------------------------------------
	
	// deletes the title and artists lists
	public void clearPlaylist(){
		this.titles = new String[0];
		this.artists = new String[0];
		this.durations = new String[0];
		this.bpms = new String[0];
	}
	
	// ------------------------------------------------------------------------
	
	// called in settings dialog when music is renew scanning 
	public void onScannedMusicFilesChanged() {

		this.clearPlaylist();

		// Get current TrackList form database
		this.tracks = DatabaseManager.getInstance().getAllTracks();

		this.setTrackList();
		this.initPlaylist();
	}
	
	// ------------------------------------------------------------------------
	
	// sets the arrays titles and artist 
	public void setTrackList(){

		this.titles = new String[this.tracks.size()];
		this.artists = new String[this.tracks.size()];
		this.durations = new String[this.tracks.size()];
		this.bpms = new String[this.tracks.size()];

		for(int i = 0; i < this.tracks.size(); i++){
			this.titles[i] = this.tracks.get(i).getTitle();
			this.artists[i] = this.tracks.get(i).getArtist();
			this.durations[i] = this.tracks.get(i).getDurationAsFormattedString();
			this.bpms[i] = this.tracks.get(i).getBpm();
		}
	}
	
	// ------------------------------------------------------------------------
	
	// ListItemClickEvent
	public void playSelectedTrack(long id){
		
		PlayerController.getInstance().prepareMusicPlayerThread(this.tracks.get((int)id));
		this.notifyObserver(this.tracks.get((int)id));
	}
	
	// ----------------------- SETTER / GETTER --------------------------------

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
	
}
