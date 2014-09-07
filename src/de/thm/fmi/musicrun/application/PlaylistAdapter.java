package de.thm.fmi.musicrun.application;

import de.thm.fmi.musicrun.R;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class PlaylistAdapter extends ArrayAdapter<String>{
	
	Context context;

	private final String[] titles, artists;
//	private final Integer[] imageId;

	// ------------------------------------------------------------------------

	public PlaylistAdapter(Context context, String[] titles, String[] artists) {
		super(context, R.layout.playlist_item, titles);
		this.context = context;
		this.titles = titles;
		this.artists = artists;
//		this.imageId = imageId;
	}
	
	// ------------------------------------------------------------------------
	
	@Override
	public View getView(int position, View view, ViewGroup parent) {
		
		LayoutInflater inflater = ((Activity) context).getLayoutInflater();
		
		View rowView= inflater.inflate(R.layout.playlist_item, null, true);
		
		TextView txtTitle = (TextView) rowView.findViewById(R.id.playlistitem_title);
		TextView txtArtist = (TextView) rowView.findViewById(R.id.playlistitem_artist);
		ImageView imageView = (ImageView) rowView.findViewById(R.id.playlistitem_image);
		
		txtTitle.setText(titles[position]);
		txtArtist.setText(artists[position]);
//		imageView.setImageResource(imageId[position]);
		imageView.setImageResource(R.drawable.ic_player1);
		
		return rowView;
	}
	
}
