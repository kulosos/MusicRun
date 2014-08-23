package de.thm.fmi.musicrun.application;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentPagerAdapter;
import android.util.Log;
import de.thm.fmi.musicrun.R;
import de.thm.fmi.musicrun.maps.MapsFragment;
import de.thm.fmi.musicrun.pedometer.PedometerFragment;

/**
 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {
	
	private List<Fragment> sections = new ArrayList<Fragment>();
	private Activity activity;
	
	// DEBUG
	private static final String TAG = MainActivity.class.getName();
	private static final boolean D = false;
	
	// --------------------------------------------------------------------
	
	public SectionsPagerAdapter(FragmentManager fm, Activity activity) {
	
		super(fm);
		
		if(D)Log.i(TAG, "SectionPagerAdapter CONSTRUCTOR");
		this.activity = activity;
		this.setSections();
	}
	
	// --------------------------------------------------------------------
	
	public void setSections(){
		
		if(D)Log.i(TAG, "SectionPagerAdapter setSections");
		
		this.sections.add(new PlayerFragment());
		this.sections.add(new PedometerFragment()); 
		this.sections.add(new MapsFragment()); 
		this.sections.add(new SettingsFragment()); 
//		this.sections.add(PlaceholderFragment.newInstance(1));
	}
	
	// --------------------------------------------------------------------

	@Override
	public Fragment getItem(int position) {

		if(D)Log.i(TAG, "SectionPagerAdapter - getItem - Instantiate sections from List on position: " + position);
		return this.sections.get(position);
	}

	// --------------------------------------------------------------------
	
	@Override
	public int getCount() {
		return this.sections.size();
	}

	// --------------------------------------------------------------------
	
	@Override
	public CharSequence getPageTitle(int position) {
	
		Locale l = Locale.getDefault();
		switch (position) {
		case 0:
			return this.activity.getString(R.string.title_section1).toUpperCase(l);
		case 1:
			return this.activity.getString(R.string.title_section2).toUpperCase(l);
		case 2:
			return this.activity.getString(R.string.title_section3).toUpperCase(l);
		case 3:
			return this.activity.getString(R.string.title_section4).toUpperCase(l);
		}
		return null;
	}
}
