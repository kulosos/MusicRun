package de.thm.fmi.musicrun.application;

import android.app.Activity;
import android.graphics.Typeface;
import android.util.Log;

public class TypefaceManager {

	// Activity
	Activity activity;
	
	// Constants
	public static final String DEFAULT_NORMAL_FONT_FILENAME = "fonts/Roboto-Thin.ttf";
	public static final String DEFAULT_BOLD_FONT_FILENAME = "fonts/Roboto-Bold.ttf";
	public static final String DEFAULT_ITALIC_FONT_FILENAME = "fonts/Roboto-ThinItalic.ttf";
	public static final String DEFAULT_BOLD_ITALIC_FONT_FILENAME = "fonts/Roboto-BoldItalic.ttf";

	// Typefaces
	private Typeface regular;
    private Typeface bold;
    private Typeface italic;
    private Typeface boldItalic;
	public enum FontStyle { REGULAR, BOLD, ITALIC, ITALIC_BOLD; }
	
	// DEBUG
	private static final String TAG = MainActivity.class.getName();
	private static final boolean D = true;
	
	// ------------------------------------------------------------------------
	
	public TypefaceManager(Activity activity){
		
		this.activity = activity;
	}
	
	// ------------------------------------------------------------------------
	
	// sets the custom typefaces global, except the navigation bar
	public void setDefaultFont() {

	    try {
	    	this.regular = Typeface.createFromAsset(this.activity.getAssets(),DEFAULT_NORMAL_FONT_FILENAME);
	        this.bold = Typeface.createFromAsset(this.activity.getAssets(), DEFAULT_BOLD_FONT_FILENAME);
	        this.italic = Typeface.createFromAsset(this.activity.getAssets(), DEFAULT_ITALIC_FONT_FILENAME);
	        this.boldItalic = Typeface.createFromAsset(this.activity.getAssets(), DEFAULT_BOLD_ITALIC_FONT_FILENAME);
	       
	        java.lang.reflect.Field DEFAULT = Typeface.class.getDeclaredField("DEFAULT");
	        DEFAULT.setAccessible(true);
	        DEFAULT.set(null, this.regular);

	        java.lang.reflect.Field DEFAULT_BOLD = Typeface.class.getDeclaredField("DEFAULT_BOLD");
	        DEFAULT_BOLD.setAccessible(true);
	        DEFAULT_BOLD.set(null, this.bold);

	        java.lang.reflect.Field sDefaults = Typeface.class.getDeclaredField("sDefaults");
	        sDefaults.setAccessible(true);
	        sDefaults.set(null, new Typeface[]{
	                this.regular, this.bold, this.italic, this.boldItalic
	        });

	    } catch (NoSuchFieldException e) {
	        Log.e(TAG, e.toString());
	    } catch (IllegalAccessException e) {
	    	Log.e(TAG, e.toString());
	    } catch (Throwable e) {
	        //cannot crash app if there is a failure with overriding the default font!
	    	Log.e(TAG, e.toString());
	    }
	}	
	
	// ------------------------------------------------------------------------
	
	public Typeface getTypeface(FontStyle style){
	
		switch(style){
		case REGULAR: 		return this.regular;
		case BOLD:			return this.bold;
		case ITALIC:		return this.italic;
		case ITALIC_BOLD:	return this.boldItalic;
		default:			return this.regular;
		}
	}
	
	
}
