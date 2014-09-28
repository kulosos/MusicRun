package de.thm.fmi.musicrun.application;

import de.thm.fmi.musicrun.R;
import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.app.FragmentTransaction;
import android.content.SharedPreferences;

public class MainActivity extends Activity implements ActionBar.TabListener {

	// App Navigation
	public ViewPager mViewPager;

	// Typefaces
	TypefaceManager typefaceMgr;
	
	// DEBUG
	private static final String TAG = MainActivity.class.getName();
	private static final boolean D = false;

	// ------------------------------------------------------------------------
	
	public MainActivity(){
	}
	
	// ------------------------------------------------------------------------
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		if(D) Log.d(TAG, "DEBUG - onCreate"); // DEBUG
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// init Singleton classes
		SectionsPagerAdapter.initInstance(getFragmentManager(), this);
		DatabaseManager.initInstance(this);
		PreferencesManager.initInstance(this);
		
		// Set up the action bar.
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		
		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(SectionsPagerAdapter.getInstance());
		
		// -----------------------
		// Set the number of pages that should be retained to either side 
		// of the current page in the view hierarchy in an idle state.
		mViewPager.setOffscreenPageLimit(SectionsPagerAdapter.getInstance().getCount());
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
		for (int i = 0; i < SectionsPagerAdapter.getInstance().getCount(); i++) {
			// Create a tab with text corresponding to the page title defined by
			// the adapter. Also specify this Activity object, which implements
			// the TabListener interface, as the callback (listener) for when
			// this tab is selected.
			
			switch(i){
			case 0: 
				actionBar.addTab(actionBar.newTab().setText("").setIcon(R.drawable.ic_player1).setTabListener(this));
				break;
			case 1: 
				actionBar.addTab(actionBar.newTab().setText("").setIcon(R.drawable.ic_player1).setTabListener(this));
				break;
			case 2: 
				actionBar.addTab(actionBar.newTab().setText("").setIcon(R.drawable.ic_pedometer3).setTabListener(this));
				break;
			case 3:
				actionBar.addTab(actionBar.newTab().setText("").setIcon(R.drawable.ic_maps2).setTabListener(this));
				break;
			case 4:
				actionBar.addTab(actionBar.newTab().setText("").setIcon(R.drawable.ic_settings).setTabListener(this));
				break;
			}
		}
		
		// ....................................................................
		
		// Set global Typefaces
		this.typefaceMgr = new TypefaceManager(this);
		this.typefaceMgr.setDefaultFont();
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
		
		
		// When the given tab is selected, switch to the corresponding page in
		// the ViewPager.steamssteamdfaglkadfälgjfdäajgdsagdsgjjj
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
	
	
}
