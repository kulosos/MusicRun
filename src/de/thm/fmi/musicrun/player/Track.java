package de.thm.fmi.musicrun.player;

public class Track {

	private int id;
	private String title;
	private String artist;
	private String album;
	private String year;
	private int bpm;
	private String category;
	private String mimeType;

	
	// ------------------------------------------------------------------------
	
	public Track(){
		
	}
	
//	public Track(int id, String title){
//		super();
//		this.id = id;
//		this.title = title;
//	}
	
	public Track(int id, String title, String artist, String album, String year, int bpm, String category, String mimeType){
		super();
		this.id = id;
		this.title = title;
		this.artist = artist;
		this.album = album;
		this.year = year;
		this.bpm = bpm;
		this.category = category;
		this.mimeType = mimeType;
	}
	
	// ------------------------------------------------------------------------
	
	public String toString(){
		return "[id=" + this.id + ", " + this.title + ", " + this.artist + ", " + this.album + ", " + this.year + ", " + this.bpm + ", " + this.category + ", " + this.mimeType; 
	}

	// -------------------- SETTER / GETTERS ----------------------------------
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setArtist(String artist) {
		this.artist = artist;
	}

	public void setAlbum(String album) {
		this.album = album;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public void setBpm(int bpm) {
		this.bpm = bpm;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	public String getTitle() {
		return title;
	}

	public String getArtist() {
		return artist;
	}

	public String getAlbum() {
		return album;
	}

	public String getYear() {
		return year;
	}

	public int getBpm() {
		return bpm;
	}

	public String getCategory() {
		return category;
	}

	public String getMimeType() {
		return mimeType;
	}
	
	
}
