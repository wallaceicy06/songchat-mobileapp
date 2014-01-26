package edu.rice.harger.songchat;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayer.ErrorReason;
import com.google.android.youtube.player.YouTubePlayer.OnInitializedListener;
import com.google.android.youtube.player.YouTubePlayer.PlaybackEventListener;
import com.google.android.youtube.player.YouTubePlayer.PlayerStateChangeListener;
import com.google.android.youtube.player.YouTubePlayer.Provider;
import com.google.android.youtube.player.YouTubePlayerView;

import android.os.Bundle;
import android.os.Handler;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.Toast;

public class SongPlayActivity extends YouTubeBaseActivity implements
		OnInitializedListener, PlayerStateChangeListener, PlaybackEventListener, OnClickListener {

	private YouTubePlayerView youtubeView;
	private YouTubePlayer youtubePlayer;
	private String videoId;
	private ImageButton playButton;
	private int startTime = 30000;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_song_play);
		
		Intent intent = getIntent();
		videoId = intent.getStringExtra("vidId");

		playButton = (ImageButton) findViewById(R.id.song_play_button);
		playButton.setOnClickListener(this);
		
		youtubeView = (YouTubePlayerView) findViewById(R.id.youtubeplayer);
		youtubeView.initialize(MainActivity.DEVELOPER_ID, this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.song_play, menu);
		return true;
	}

	@Override
	public void onInitializationFailure(Provider arg0,
			YouTubeInitializationResult arg1) {
		Toast.makeText(this, "Initialization Fail", Toast.LENGTH_LONG).show();
	}

	@Override
	public void onInitializationSuccess(Provider provider, YouTubePlayer player,
			boolean wasRestored) {
		youtubePlayer = player;
		youtubePlayer.setPlayerStateChangeListener(this);
		youtubePlayer.setPlaybackEventListener(this);
		Toast.makeText(this, "Initialization  Success", Toast.LENGTH_LONG)
				.show();
		youtubePlayer.cueVideo(videoId, startTime);
	}

	@Override
	public void onBuffering(boolean arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPaused() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPlaying() {
		Log.i("SongPlayActivity", "On playing started");
		final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
          @Override
          public void run() {
        	  Log.i("SongPlayActivity", "Pause");
              youtubePlayer.pause();
          }
        }, 5000);  		
	}

	@Override
	public void onSeekTo(int arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStopped() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onAdStarted() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onError(ErrorReason reason) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onLoaded(String arg0) {
  	  	Log.i("SongPlayActivity", "Loaded");
	}

	@Override
	public void onLoading() {
	}

	@Override
	public void onVideoEnded() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onVideoStarted() {
  	  	Log.i("SongPlayActivity", "Video started");
		
	}

	@Override
	public void onClick(View view) {
		if(view == playButton) {
			youtubePlayer.play();
		}
	}

}
