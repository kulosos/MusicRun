package de.thm.fmi.musicrun.application;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import de.thm.fmi.musicrun.R;
import de.thm.fmi.musicrun.maps.MapsFragment;
import de.thm.fmi.musicrun.pedometer.PedometerFragment;

/**
 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentStatePagerAdapter {
	
	private static SectionsPagerAdapter instance;
	
	private List<Fragment> sections = new ArrayList<Fragment>();
	private Activity activity;
	
	// DEBUG
	private static final String TAG = MainActivity.class.getName();
	private static final boolean D = false;
	
	// ------------------------------------------------------------------------
	
	private SectionsPagerAdapter(FragmentManager fm, Activity activity) {
	
		super(fm);
		
		if(D)Log.i(TAG, "SectionPagerAdapter CONSTRUCTOR");
		this.activity = activity;
		this.setSections();
	}
	
	// ------------------- SINGLETON METHODS ----------------------------------
	
	public static void initInstance(FragmentManager fm, Activity activity){
		if(instance == null){
			instance = new SectionsPagerAdapter(fm, activity);
		}
	}
	
	public static SectionsPagerAdapter getInstance(){
		return instance;
	}
	
	// ------------------------------------------------------------------------
	
	public void setSections(){
		
		PlaylistFragment plf = new PlaylistFragment();
		PlayerFragment pf = new PlayerFragment();
		
		this.sections.add(pf);
		this.sections.add(plf);	
		this.sections.add(new PedometerFragment()); 
		this.sections.add(new MapsFragment()); 
		this.sections.add(new SettingsFragment()); 
		
//		this.sections.add(PlaceholderFragment.newInstance(1));
	}
	
	// ------------------------------------------------------------------------
	
	public void addSectionAtPosition(Fragment fragment, int position){
		this.sections.add(position, fragment);
	}
	
	// ------------------------------------------------------------------------

	@Override
	public Fragment getItem(int position) {

		return this.sections.get(position);
	}

	// ------------------------------------------------------------------------
	
	@Override
	public int getCount() {
		return this.sections.size();
	}

	// ------------------------------------------------------------------------
	
	@Override
	public CharSequence getPageTitle(int position) {

		Locale l = Locale.getDefault();
		switch (position) {
		case 0:
			return this.activity.getString(R.string.title_section_player).toUpperCase(l);
		case 1:
			return this.activity.getString(R.string.title_section_playlist).toUpperCase(l);
		case 2:
			return this.activity.getString(R.string.title_section_pedometer).toUpperCase(l);
		case 3:
			return this.activity.getString(R.string.title_section_maps).toUpperCase(l);
		case 4:
			return this.activity.getString(R.string.title_section_settings).toUpperCase(l);
		}
		return null;
	}
}
