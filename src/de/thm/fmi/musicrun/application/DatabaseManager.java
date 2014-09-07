package de.thm.fmi.musicrun.application;

import java.util.LinkedList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseManager extends SQLiteOpenHelper {
	
	private static DatabaseManager instance;
	
	// Database constants
	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "musicRunDb";

	private static final String TABLE_TRACKS = "track";
	
	private static final String TRACK_ID = "trackid";
	private static final String TRACK_TITLE = "tracktitle";
	private static final String TRACK_ALBUM = "trackalbum";
	private static final String TRACK_YEAR = "trackyear";
	private static final String TRACK_BPM = "trackbpm";
	private static final String TRACK_MIMETYPE = "trackmimetype";
	private static final String TRACK_ARTIST = "trackartist";
	private static final String TRACK_CATEGORY = "trackcategory";
	
	private static final String[] COLUMNS = {TRACK_ID, TRACK_TITLE, TRACK_ARTIST, TRACK_ALBUM, TRACK_YEAR, TRACK_BPM, TRACK_MIMETYPE, TRACK_CATEGORY};
	
	// DEBUG
	private static final String TAG = MainActivity.class.getName();
	private static final boolean D = false;

	// ------------------------------------------------------------------------
	
	private DatabaseManager(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	// ------------------- SINGLETON METHODS ----------------------------------
	
	public static void initInstance(Context context){
		if(instance == null){
			instance = new DatabaseManager(context);
		}
	}
	
	public static DatabaseManager getInstance(){
		return instance;
	}
	
	// ------------------------------------------------------------------------
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		
		// CREATE TABLES
//		String CREATE_ARTIST_TABLE = "CREATE TABLE artist(artistid INTEGER PRIMARY KEY,artistname TEXT);";
//		String CREATE_CATEGORY_TABLE = "CREATE TABLE category (categoryid INTEGER PRIMARY KEY,categoryname TEXT,categorydesc TEXT);";
//		String CREATE_TRACK = 	"CREATE TABLE track(trackid INTEGER PRIMARY KEY,tracktitle TEXT,trackalbum TEXT,trackyear TEXT,trackbpm INTEGER"+
//								",trackmimetype TEXT,trackartist INTEGER,trackcategory INTEGER,"+
//								"FOREIGN KEY(trackartist) REFERENCES artist(artistid),FOREIGN KEY(trackcategory) REFERENCES category(categoryid));";
//		

		String CREATE_TRACK = 	
				"CREATE TABLE track("
				+ "trackid INTEGER,"
				+ "tracktitle TEXT,"
				+ "trackartist TEXT, "
				+ "trackalbum TEXT,"
				+ "trackyear TEXT,"
				+ "trackbpm INTEGER,"
				+ "trackmimetype TEXT,"
				+ "trackcategory TEXT"
				+ ");";
		
		// CREATE DATABASE
		String CREATE_DATABASE_TABLES = /*CREATE_ARTIST_TABLE +  CREATE_CATEGORY_TABLE +*/ CREATE_TRACK;
		db.execSQL(CREATE_DATABASE_TABLES);
		
	}
	
	// ------------------------------------------------------------------------

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
//		db.execSQL("DROP TABLE IF EXISTS artist");
//		db.execSQL("DROP TABLE IF EXISTS category");
		db.execSQL("DROP TABLE IF EXISTS track");
		
		this.onCreate(db);
		
	}
	
	// ------------------------------------------------------------------------
	
	public void addTrack(Track track){
		
		if(D) Log.d(TAG, "addTrack: " + track.toString());
		
        SQLiteDatabase db = this.getWritableDatabase();
 
        ContentValues values = new ContentValues();
        values.put(TRACK_ID, track.getId());
        values.put(TRACK_TITLE, track.getTitle()); 
        values.put(TRACK_ARTIST, track.getArtist()); 
        values.put(TRACK_ALBUM, track.getAlbum());
        values.put(TRACK_YEAR, track.getYear());
        values.put(TRACK_MIMETYPE, track.getMimeType());
        values.put(TRACK_BPM, track.getBpm());
        values.put(TRACK_CATEGORY, track.getCategory());
 
        db.insert(TABLE_TRACKS, null, values); 
 
        db.close(); 		
	}
	
	// ------------------------------------------------------------------------

	public Track getTrack(int id){
		  
		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = 
				db.query(TABLE_TRACKS, // a. table
						COLUMNS, // b. column names
						" trackid = ?", // c. selections 
						new String[] { String.valueOf(id) }, // d. selections args
						null, // e. group by
						null, // f. having
						null, // g. order by
						null); // h. limit

		if (cursor != null)
			cursor.moveToFirst();

		Track track = new Track();
		if(cursor.getString(0) != null) track.setId(Integer.parseInt(cursor.getString(0))); else track.setId(0);
		
		track.setTitle(cursor.getString(1));
		track.setArtist(cursor.getString(2));
		track.setAlbum(cursor.getString(3));
		track.setYear(cursor.getString(4));
		
		if(cursor.getString(5) != null) track.setBpm(Integer.parseInt(cursor.getString(5))); else track.setBpm(0);
		
		track.setMimeType(cursor.getString(6));
		track.setCategory(cursor.getString(7));

		//log 
		if(D) Log.d("getTrack("+id+")", track.toString());

		return track;
	}
	
	// ------------------------------------------------------------------------
	
	public List<Track> getAllTracks() {
		
	       List<Track> tracks = new LinkedList<Track>();
	 
	       String query = "SELECT * FROM " + TABLE_TRACKS;
	 
	       SQLiteDatabase db = this.getWritableDatabase();
	       Cursor cursor = db.rawQuery(query, null);
	 
	       // go over each row, build track and add it to list
	       Track track = null;
	       if (cursor.moveToFirst()) {
	    	   do {
	    		   track = new Track();
	    		   
	    		   if(cursor.getString(0) != null) track.setId(Integer.parseInt(cursor.getString(0))); else track.setId(0);
//	    		   track.setId(Integer.parseInt(cursor.getString(0)));
	    		   track.setTitle(cursor.getString(1));
	    		   track.setArtist(cursor.getString(2));
	    		   track.setAlbum(cursor.getString(3));
	    		   track.setYear(cursor.getString(4));
	    		   if(cursor.getString(5) != null) track.setBpm(Integer.parseInt(cursor.getString(5))); else track.setBpm(0);
//	    		   track.setBpm(Integer.parseInt(cursor.getString(6)));
	    		   track.setMimeType(cursor.getString(6));
	    		   track.setCategory(cursor.getString(7));

	    		   tracks.add(track);
	    	   } while (cursor.moveToNext());
	       }

//	       Log.d("getAllTracks()", tracks.toString());
	 
	       return tracks;
	   }
	
	// ------------------------------------------------------------------------
	
	public void deleteAllTracks(){
		
		SQLiteDatabase db = this.getWritableDatabase();
		
		// DELETE TABLE before adding tracks by scanning the music folder
		// Query doesn't work with *
		db.execSQL("DELETE FROM track");
		
	}
}
