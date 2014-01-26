package edu.rice.harger.songchat;

import java.util.ArrayList;

import edu.rice.harger.songchat.YouTube.YouTubeListener;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class PickSong extends Activity implements OnClickListener, YouTubeListener, OnItemClickListener {
	
	private ListView searchResults;
	private Button searchButton;
	private TextView searchText;
	private ArrayAdapter<YT_Wrapper> adapter;
	private String userId;
	private String[] friends;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pick_song);
		
		Intent intent = getIntent();
		userId = intent.getStringExtra("userId");
		friends = intent.getStringArrayExtra("friends");
		
		searchResults = (ListView) findViewById(R.id.video_results);
		adapter = new ArrayAdapter<YT_Wrapper>(this, android.R.layout.simple_list_item_1);
		searchResults.setAdapter(adapter);
		searchResults.setOnItemClickListener(this);
		searchButton = (Button) findViewById(R.id.search_button);
		searchButton.setOnClickListener(this);
		searchText = (TextView) findViewById(R.id.searchText);	
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.pick_song, menu);
		return true;
	}

	@Override
	public void onClick(View view) {
		if(view == searchButton) {
			YouTube myTask = new YouTube(this, adapter);
			myTask.execute(searchText.getText().toString());
		}
	}

	@Override
	public void onResultsObtained(ArrayList<YT_Wrapper> results) {
		adapter.clear();
		
		for(YT_Wrapper w: results) {
			adapter.add(w);
		}
		Log.i(STORAGE_SERVICE, Integer.toString(adapter.getCount()));
		adapter.notifyDataSetChanged();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int pos,
			long id) {
		Log.i("what","fdsfasd");
		Intent intent = new Intent(this, SongSliceActivity.class);
		String message = adapter.getItem(pos).getURLs().toString();
		intent.putExtra("vidTitle", adapter.getItem(pos).getTitles().toString());
		intent.putExtra("vidUrl", message);
		intent.putExtra("userId", userId);
		intent.putExtra("friends", friends);
		startActivity(intent);
		
	}

	
}