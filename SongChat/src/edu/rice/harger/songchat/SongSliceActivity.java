package edu.rice.harger.songchat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayer.ErrorReason;
import com.google.android.youtube.player.YouTubePlayer.OnInitializedListener;
import com.google.android.youtube.player.YouTubePlayer.PlaybackEventListener;
import com.google.android.youtube.player.YouTubePlayer.PlayerStateChangeListener;
import com.google.android.youtube.player.YouTubePlayer.Provider;
import com.google.android.youtube.player.YouTubePlayerView;

import edu.rice.harger.songchat.MainActivity.LoginDialog;

import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

public class SongSliceActivity extends YouTubeBaseActivity implements
		OnInitializedListener, PlayerStateChangeListener,
		PlaybackEventListener, OnClickListener, OnEditorActionListener {

	private String userId;
	private String[] friends;
	private YouTubePlayerView youtubeView;
	private YouTubePlayer youtubePlayer;
	private Button toggleEndsButton;
	private Button reviewButton;
	private EditText videoUrl;
	private boolean isStart;
	private boolean endSelected;
	private boolean isReviewing;
	private int startTimeMillis;
	private int endTimeMillis;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_song_slice);
		
		loadFriends();

		endSelected = false;
		isReviewing = false;

		Intent intent = getIntent(); 
		userId = intent.getStringExtra("userId");
		friends = intent.getStringArrayExtra("friends");
		String vidUrl = intent.getStringExtra("vidUrl");		
		String vidTitle = intent.getStringExtra("vidTitle");
		
		toggleEndsButton = (Button) findViewById(R.id.toggle_button);
		toggleEndsButton.setOnClickListener(this);

		reviewButton = (Button) findViewById(R.id.review_button);
		reviewButton.setOnClickListener(this);
		
		videoUrl = (EditText) findViewById(R.id.video_url);
		videoUrl.setOnEditorActionListener(this);
		
		String videoId= vidUrl.split("v=")[1];
		videoId= videoId.split("&")[0];
		Log.i("WHAT",videoId +" "+ vidUrl);
		
//		Pattern pattern = Pattern.compile("(\?v=|\/\d\/|\/embed\/|\/v\/|\.be\/)([a-zA-Z0-9\-\_]+)/");
//		Matcher matcher = pattern.matcher(vidUrl);
//		String videoId = matcher.group();
		
		videoUrl.setText(videoId);
		
		youtubeView = (YouTubePlayerView) findViewById(R.id.slice_player);
		youtubeView.initialize(MainActivity.DEVELOPER_ID, this);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.song_slice, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case R.id.send:
			FriendsDialog dialog = FriendsDialog.newInstance();
			dialog.show(getFragmentManager().beginTransaction(), "dialog");
			return true;
		default:
			return super.onOptionsItemSelected(item);		
		}
	}
	
	public void sendMessage(final int userIndex) {
		Thread trd = new Thread(new Runnable() {
			@Override
			public void run() {
				HttpClient httpClient = new DefaultHttpClient();
				HttpGet httpGet = new HttpGet(
						"http://ec2-184-73-80-30.compute-1.amazonaws.com/app2server.php?tx_username="
								+ userId + "&rx_username=" + friends[userIndex] + "&msg=_" + "&start_time=" + startTimeMillis + "&length=" + (endTimeMillis - startTimeMillis) + "&url=" + videoUrl.getText().toString());;

				// Making HTTP Request
				try {
					HttpResponse response = httpClient.execute(httpGet);

					// writing response to log
					InputStream in = response.getEntity().getContent();
					BufferedReader reader = new BufferedReader(
							new InputStreamReader(in));
					StringBuilder str = new StringBuilder();
					String line = null;
					while ((line = reader.readLine()) != null) {
						str.append(line);
					}
					in.close();

					Log.d("Http Response:", str.toString());

				} catch (ClientProtocolException e) {
					// writing exception to log
					e.printStackTrace();

				} catch (IOException e) {
					// writing exception to log
					e.printStackTrace();
				}
			}
		});
		trd.start();
	}

	@Override
	public void onInitializationFailure(Provider provider,
			YouTubeInitializationResult result) {
	}

	@Override
	public void onInitializationSuccess(Provider provider,
			YouTubePlayer player, boolean wasRestored) {
		youtubePlayer = player;
		youtubePlayer.setPlayerStateChangeListener(this);
		youtubePlayer.setPlaybackEventListener(this);
		youtubePlayer.cueVideo(videoUrl.getText().toString());

	}

	@Override
	public void onClick(View view) {
		if (view == toggleEndsButton) {
			if (isStart) {
				startTimeMillis = youtubePlayer.getCurrentTimeMillis();
				isStart = false;
				toggleEndsButton.setText("End");
			} else {
				endTimeMillis = youtubePlayer.getCurrentTimeMillis();
				isStart = true;
				toggleEndsButton.setText("Start");
			}
			Log.i("Selected Time", startTimeMillis + ":" + endTimeMillis);
		} else if (view == reviewButton) {
			if (startTimeMillis < endTimeMillis) {
				isReviewing = true;
				youtubePlayer.cueVideo(videoUrl.getText().toString(), startTimeMillis);
			}
		}
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
		if (isReviewing) {
			final Handler handler = new Handler();
			handler.postDelayed(new Runnable() {
				@Override
				public void run() {
					Log.i("SongSliceActivity", "Pause");
					youtubePlayer.pause();
					isReviewing = false;
				}
			}, endTimeMillis - startTimeMillis);
		}
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
	public void onError(ErrorReason arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onLoaded(String arg0) {
		youtubePlayer.play();
	}

	@Override
	public void onLoading() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onVideoEnded() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onVideoStarted() {
		// TODO Auto-generated method stub

	}
	
	private void loadFriends() {
		Thread trd = new Thread(new Runnable() {
			@Override
			public void run() {
				HttpClient httpClient = new DefaultHttpClient();
				HttpGet httpGet = new HttpGet(
						"http://ec2-184-73-80-30.compute-1.amazonaws.com/get_friends.php?username=" + userId);

				// Making HTTP Request
				try {
					HttpResponse response = httpClient.execute(httpGet);

					// writing response to log
					InputStream in = response.getEntity().getContent();
					BufferedReader reader = new BufferedReader(
							new InputStreamReader(in));
					StringBuilder str = new StringBuilder();
					String line = null;
					while ((line = reader.readLine()) != null) {
						str.append(line);
					}
					in.close();
					
//					JSONObject json = new JSONObject(str);
//					JSONArray articles = json.getJSONArray(name)

					Log.d("Friends Response:", str.toString());

				} catch (ClientProtocolException e) {
					// writing exception to log
					e.printStackTrace();

				} catch (IOException e) {
					// writing exception to log
					e.printStackTrace();
				}
			}
		});
		trd.start();
		//ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, new String[] {"apple", "orange", "banana"});
		//listView.setAdapter(adapter);
	}
	
	public String[] getFriends() {
		return friends;
	}
	
	public static class FriendsDialog extends DialogFragment {
		public static FriendsDialog newInstance() {
			FriendsDialog dialog = new FriendsDialog();
			return dialog;
		}

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
		    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		    builder.setTitle("Pick friend")
		           .setItems(((SongSliceActivity) getActivity()).getFriends(), new DialogInterface.OnClickListener() {
		               public void onClick(DialogInterface dialog, int which) {
		            	   ((SongSliceActivity) getActivity()).sendMessage(which);
		           }
		    });
		    return builder.create();
		}
	}


	@Override
	public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {
		 if (actionId == EditorInfo.IME_ACTION_GO) {
	            if (youtubePlayer != null) {
	                youtubePlayer.cueVideo(videoUrl.getText().toString());
	            }
	        }
		 return false;
	}

}
