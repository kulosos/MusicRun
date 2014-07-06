package de.thm.fmi.musicrun.application;

import de.thm.fmi.musicrun.R;
import android.app.Activity;
import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.app.FragmentTransaction;
import android.graphics.Typeface;

public class MainActivity extends Activity implements ActionBar.TabListener {

	// App Navigation
	SectionsPagerAdapter mSectionsPagerAdapter;
	ViewPager mViewPager;
	
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
	
	// DEBUG
	private static final String TAG = MainActivity.class.getName();
	private static final boolean D = true;
	
	// ------------------------------------------------------------------------
	
	public MainActivity(){
	}
	
	// ------------------------------------------------------------------------
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		if(D) Log.d(TAG, "DEBUG - onCreate"); // DEBUG
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Set up the action bar.
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the activity.
		mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager(), this);

		
		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);
		
		// -----------------------
		// Set the number of pages that should be retained to either side 
		// of the current page in the view hierarchy in an idle state.
		mViewPager.setOffscreenPageLimit(this.mSectionsPagerAdapter.getCount());
		// -----------------------

		// When swiping between different sections, select the corresponding
		// tab. We can also use ActionBar.Tab#select() to do this if we have
		// a reference to the Tab.
		mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						actionBar.setSelectedNavigationItem(position);
					}
				});

		// For each of the sections in the app, add a tab to the action bar.
		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
			// Create a tab with text corresponding to the page title defined by
			// the adapter. Also specify this Activity object, which implements
			// the TabListener interface, as the callback (listener) for when
			// this tab is selected.
			
			// create text only Tab
//			actionBar.addTab(actionBar.newTab().setText(mSectionsPagerAdapter.getPageTitle(i)).setTabListener(this));
			
			// create icon with text tab
//			actionBar.addTab(actionBar.newTab().setText("test").setIcon(R.drawable.ic_launcher).setTabListener(this));

//			// create only icon tab
//			actionBar.addTab(actionBar.newTab().setText("").setIcon(R.drawable.ic_launcher).setTabListener(this));

			switch(i){
			case 0: 
				actionBar.addTab(actionBar.newTab().setText("").setIcon(R.drawable.ic_player1).setTabListener(this));
				break;
			case 1: 
				actionBar.addTab(actionBar.newTab().setText("").setIcon(R.drawable.ic_pedometer3).setTabListener(this));
				break;
			case 2:
				actionBar.addTab(actionBar.newTab().setText("").setIcon(R.drawable.ic_maps2).setTabListener(this));
				break;
			case 3:
				actionBar.addTab(actionBar.newTab().setText("").setIcon(R.drawable.ic_settings).setTabListener(this));
				break;
			}
		}
		
		// ....................................................................
		
		this.setDefaultFont();
	}

	// ------------------------------------------------------------------------
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	// ------------------------------------------------------------------------
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		if(D)Log.i(TAG, "MainActivity- onOptionItemSelected - item: " + item.toString());
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	// ------------------------------------------------------------------------
	
	@Override
	public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
		
		if(D)Log.i(TAG, "MainActivity- onTabSelected - tab.getPosition: " + tab.getPosition());
		
		// When the given tab is selected, switch to the corresponding page in
		// the ViewPager.
		mViewPager.setCurrentItem(tab.getPosition());
	}

	// ------------------------------------------------------------------------
	
	@Override
	public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
	}

	// ------------------------------------------------------------------------
	
	@Override
	public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
	}
	
	// ------------------------------------------------------------------------
	
	// sets the custom typefaces global, except the navigation bar
	private void setDefaultFont() {

	    try {
	    	this.regular = Typeface.createFromAsset(getAssets(),DEFAULT_NORMAL_FONT_FILENAME);
	        this.bold = Typeface.createFromAsset(getAssets(), DEFAULT_BOLD_FONT_FILENAME);
	        this.italic = Typeface.createFromAsset(getAssets(), DEFAULT_ITALIC_FONT_FILENAME);
	        this.boldItalic = Typeface.createFromAsset(getAssets(), DEFAULT_BOLD_ITALIC_FONT_FILENAME);
	       
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
}
