package de.thm.fmi.musicrun.application;

import java.util.concurrent.TimeUnit;

import android.widget.ImageView;

public class Track {

	private int id;
	private String title;
	private String artist;
	private String album;
	private String year;
	private int bpm;
	private String category;
	private String mimeType;
	private ImageView img;
	private String filepath;
	private String duration;

	
	// ------------------------------------------------------------------------
	
	public Track(){
		super();
	}
	
	// ------------------------------------------------------------------------
	
	public Track(int id, String title, String artist, String album, String year, int bpm, String category, String mimeType, String filepath, String duration){
		super();
		this.id = id;
		this.title = title;
		this.artist = artist;
		this.album = album;
		this.year = year;
		this.bpm = bpm;
		this.category = category;
		this.mimeType = mimeType;
		this.filepath = filepath;
		this.duration = duration;
//		this.img = findViewById(R.drawable.ic_player1);
		
	}
	
	// ------------------------------------------------------------------------
	
	public String toString(){
		return "[id=" + this.id + ", " + this.title + ", " + this.artist + ", " + this.album + ", " + this.year + ", " +
				this.duration + ", " + this.bpm + ", " + this.category + ", " + this.mimeType + ", " + this.filepath + "]"; 
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

	public String getFilepath() {
		return filepath;
	}

	public void setFilepath(String filepath) {
		this.filepath = filepath;
	}

	public String getDuration() {
	
		int millis = Integer.parseInt(this.duration);
		long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
		long seconds = TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis));	
		String dur;
		
		if(seconds < 10){
			String min = String.format("%d", minutes);
			String sec = String.format("%d", seconds);
			return min + ":0" + sec;
		}
		else{
			return String.format("%d:%d", minutes, seconds);
		}
	}
	
	public void setDuration(String duration) {
		this.duration = duration;
	}
	
	
	
	
}
