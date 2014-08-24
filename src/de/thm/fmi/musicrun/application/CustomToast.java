package de.thm.fmi.musicrun.application;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import de.thm.fmi.musicrun.R;

public class CustomToast {

	Context context;
	
	// ------------------------------------------------------------------------
	
	public CustomToast(Context context, String msg, int resourceImgId, int duration){
		this.context = context;
		this.customToast(msg, resourceImgId, duration);
	}
	
	// ------------------------------------------------------------------------

	private void customToast(String msg, int resourceImgId, int duration){
		
		// get the custom_toast.xml Layout
		LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );

		View view = inflater.inflate( R.layout.custom_toast, null);//(ViewGroup) findViewById(R.id.custom_toast_layout_id));

		// set image
		ImageView image = (ImageView) view.findViewById(R.id.toast_img);
		image.setImageResource(resourceImgId);

		// set message
		TextView text = (TextView) view.findViewById(R.id.toast_msg);
		text.setText(msg);

		// Toast Message Box
		Toast toast = new Toast(this.context);
		toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
		toast.setDuration(duration);
		toast.setView(view);
		toast.show();
		
	}
}
