package de.thm.fmi.musicrun.bpmDetecation;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import wseemann.media.FFmpegMediaMetadataRetriever;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;
import de.thm.fmi.musicrun.R;
import de.thm.fmi.musicrun.application.CustomToast;
import de.thm.fmi.musicrun.application.DatabaseManager;
import de.thm.fmi.musicrun.application.MainActivity;
import de.thm.fmi.musicrun.application.PlayerController;
import de.thm.fmi.musicrun.application.PlayerFragment;
import de.thm.fmi.musicrun.application.PlaylistController;
import de.thm.fmi.musicrun.application.PreferencesManager;
import de.thm.fmi.musicrun.application.Track;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import de.thm.fmi.musicrun.bpmDetecation.BPM2SampleProcessor;
import de.thm.fmi.musicrun.bpmDetecation.EnergyOutputAudioDevice;

public class BPMDetectionManager {

	private static BPMDetectionManager instance;
	private Context context;
	
	// BPM Detection
	private BPM2SampleProcessor processor;
	private EnergyOutputAudioDevice output;
	private Player player;
	private Thread bpmDetectionThread;
	private String filePath = "";
	private File file;
	
	// Detection Progress
	private ProgressDialog progress;
	private Message detectBpmResultMsg;
	public Handler detectBpmPostHandler;
	
	// DEBUG
	private static final String TAG = MainActivity.class.getName();
	private static final boolean D = true;

	// ------------------------------------------------------------------------

	private BPMDetectionManager(Context context){
		
		this.context = context;
	}
	
	// ------------------- SINGLETON METHODS ----------------------------------

	public static void initInstance(Context context){
		if(instance == null){
			instance = new BPMDetectionManager(context);
		}
	}

	public static BPMDetectionManager getInstance(){
		return instance;
	}
	

	// ------------------------------------------------------------------------

	public void detectBpm(Track track) throws FileNotFoundException, JavaLayerException {

//		new CustomToast(this.context,"BPM DETECTION CLASS", R.drawable.ic_launcher, 500);
		this.processor = new BPM2SampleProcessor();
		this.processor.setSampleSize(1024);
		this.output = new EnergyOutputAudioDevice(processor);
		this.output.setAverageLength(1024);
		
		this.filePath = PreferencesManager.getInstance().getMusicFilepath() + track.getFilepath();

		if(D) Log.i(TAG, "DEBUG BPM TRACK: " + track.getTitle());
		
		this.file = new File(filePath);

		this.player = new Player(new FileInputStream(file), output);
		player.play();
		
		if(D) Log.i(TAG, "DEBUG BPM CLASS 4");
		
		if(D) Log.i(TAG, "DEBUG BPM calculated BPM: " + processor.getBPM());
	}
	
	// ------------------------------------------------------------------------
	
	public void detectBpmForAllFilesInList(){

		final List<Track> trackList = PlaylistController.getInstance().getTracks();
		
		this.detectBpmResultMsg = new Message();

		this.progress = new ProgressDialog(this.context);

		this.progress.setMessage("BPM Detection");
		this.progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		this.progress.setIndeterminate(false);
		this.progress.setCancelable(true);
		this.progress.setIcon(R.drawable.ic_folderscan_blue_50);

//		final int filesInFolder = PlayerController.getInstance().getFileList().length;
		final int filesInList = trackList.size();
		
		this.progress.setMax(filesInList);
		this.progress.show();

		this.bpmDetectionThread = new Thread(){

			@Override
			public void run(){

				for(int i=0; i < filesInList; i++){

//					new CustomToast(this.context,"BPM DETECTION CLASS", R.drawable.ic_launcher, 500);
					processor = new BPM2SampleProcessor();
					processor.setSampleSize(1024);
					output = new EnergyOutputAudioDevice(processor);
					output.setAverageLength(1024);

					filePath = PreferencesManager.getInstance().getMusicFilepath() + trackList.get(i).getFilepath();

					if(D) Log.i(TAG, "DEBUG BPM TRACK: " + trackList.get(i).getTitle());

					file = new File(filePath);

					if(filePath.endsWith(".mp3")){
						try {
							player = new Player(new FileInputStream(file), output);
							player.play();
						} catch (FileNotFoundException e) {
							e.printStackTrace();
						} catch (JavaLayerException e) {
							e.printStackTrace();
						}
					}
					else{
						if(D) Log.w(TAG, "File skipped.\n" +trackList.get(i).getFilepath()+ "\nOnly mp3 files are supported.");
					}

					if(D) Log.i(TAG, "##########################################################");
					if(D) Log.i(TAG, "DEBUG BPM calculated BPM: " + processor.getBPM() + " Track: " + trackList.get(i).getTitle());
					if(D) Log.i(TAG, "##########################################################");

					progress.setProgress(i);

					if(i == filesInList){
						progress.dismiss();
					}
				}

				// handler message
				detectBpmResultMsg.obj=filesInList;
				detectBpmPostHandler.sendMessage(detectBpmResultMsg);
			}
		};

		this.bpmDetectionThread.start();

		// start handler msg box after scanning
		detectBpmPostHandler = new Handler(new Handler.Callback() {

			@Override
			public boolean handleMessage(Message msg) {

				// Toast.makeText(context, msg.obj.toString() + " files scanned", Toast.LENGTH_LONG).show();
//				String toastMsg = context.getResources().getString(R.string.dialog_label_musicplayer_libraryscan_postDialog_desc);
				new CustomToast(context, "BPM Detection for " + msg.obj.toString() + "accomblished. ", R.drawable.ic_folderscan_blue_50, 1000);

				// refresh Playlist after scanning
//				PlaylistController.getInstance().onScannedMusicFilesChanged();
				return false;
			}
		});
	}


}