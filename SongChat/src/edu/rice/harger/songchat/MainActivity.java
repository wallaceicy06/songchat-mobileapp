package edu.rice.harger.songchat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import edu.rice.harger.songchat.SongSliceActivity.FriendsDialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;

public class MainActivity extends Activity implements OnClickListener, OnItemClickListener{

	public static final String DEVELOPER_ID = "AIzaSyB2iYmJiz1VhQH9RqcMOrEy6H4DGgS7n54";
	
	private String userId;
	private ListView listView;
	private String[] friends;
	private ArrayAdapter<MessageInfo> adapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		listView = (ListView) findViewById(R.id.listView1);
	    adapter = new ArrayAdapter<MessageInfo>(this, android.R.layout.simple_list_item_1);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(this);
		
		DialogFragment fragment = LoginDialog.newInstance();
		fragment.show(getFragmentManager().beginTransaction(), "dialog");
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case R.id.new_message:
			pickSong();
			return true;
		case R.id.refresh:
			refreshMessageList();
			populateFriends();
			return true;
		case R.id.add_friend:
			AddFriendDialog dialog = AddFriendDialog.newInstance();
			dialog.show(getFragmentManager().beginTransaction(), "dialog");
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onClick(View v) {
	}
	
	public void sendNewMessage() {
		Intent newMessageIntent = new Intent(this, SongSliceActivity.class);
		newMessageIntent.putExtra("vidId", "Zuich5TChEM");
		newMessageIntent.putExtra("userId", userId);
		newMessageIntent.putExtra("friends", friends);
		startActivity(newMessageIntent);
	}

	public void addFriend(final String friendId) {
		Thread trd = new Thread(new Runnable() {
			@Override
			public void run() {
				HttpClient httpClient = new DefaultHttpClient();
				HttpGet httpGet = new HttpGet(
						"http://ec2-184-73-80-30.compute-1.amazonaws.com/add_friend.php?username="
								+ userId + "&friends=" + friendId);

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

					Log.d("New friend Response:", str.toString());
					populateFriends();

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
	
	public void populateFriends() {
		Thread trd = new Thread(new Runnable() {
			@Override
			public void run() {
				HttpClient httpClient = new DefaultHttpClient();
				HttpGet httpGet = new HttpGet(
						"http://ec2-184-73-80-30.compute-1.amazonaws.com/get_friends.php?username="
								+ userId);

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

					Log.i("FRIENDS", str.toString());
					
					try {
						final JSONObject json = new JSONObject(str.toString());
						String allFriends = json.getString("friends");
						friends = allFriends.split(",");
					} catch (JSONException e) {
						e.printStackTrace();
					}


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
	
	protected void loginWithCredentials(final String username,
			final String email) {
		Thread trd = new Thread(new Runnable() {
			@Override
			public void run() {
				HttpClient httpClient = new DefaultHttpClient();
				HttpGet httpGet = new HttpGet(
						"http://ec2-184-73-80-30.compute-1.amazonaws.com/new_user.php?email="
								+ email + "&username=" + username);

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

					userId = str.toString();
					Log.d("Http Response:", userId);
					
					populateFriends();
					refreshMessageList();

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
	
	public void refreshMessageList() {
		Thread trd = new Thread(new Runnable() {
			@Override
			public void run() {
				HttpClient httpClient = new DefaultHttpClient();
				HttpGet httpGet = new HttpGet(
						"http://ec2-184-73-80-30.compute-1.amazonaws.com/get_by_date.php?username=" + userId + "&number=10");

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
					
					try {
						final JSONObject json = new JSONObject(str.toString());
						
						final Iterator<String> jsonIterator = json.keys();
						runOnUiThread(new Runnable() {
						     @Override
						     public void run() {
						    	 adapter.clear();
						    	 int index = 0;
									while(jsonIterator.hasNext()) {
										//messages[index++] = json.getJSONObject(jsonIterator.next()).get("sender").toString();
										try {
											JSONObject msg = json.getJSONObject(jsonIterator.next());
											MessageInfo msgInfo = new MessageInfo(msg.getString("sender"),
																                  userId,
																                  msg.getString("msg"),
																                  msg.getString("url"),
																                  msg.getInt("start_time"),
																                  msg.getInt("length"));
											adapter.add(msgInfo);
										} catch (JSONException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
									}
									Log.i("ADAPTER", Integer.toString(adapter.getCount()));
									adapter.notifyDataSetChanged();
						     }
						});
						

					} catch (JSONException e) {
						e.printStackTrace();
					}
					
					
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
		//ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, new String[] {"apple", "orange", "banana"});
		//listView.setAdapter(adapter);
	}

	public static class LoginDialog extends DialogFragment {
		public static LoginDialog newInstance() {
			LoginDialog dialog = new LoginDialog();
			return dialog;
		}

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			// Get the layout inflater
			LayoutInflater inflater = getActivity().getLayoutInflater();
			View inflatedView = inflater.inflate(R.layout.dialog_signin, null);
			final EditText username = (EditText) inflatedView
					.findViewById(R.id.username);
			final EditText email = (EditText) inflatedView
					.findViewById(R.id.email);
			builder.setView(inflatedView);

			builder.setPositiveButton("Ok",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							if(username.getText().length() > 0 && email.getText().length() > 0) {
							((MainActivity) getActivity())
									.loginWithCredentials(username.getText()
											.toString(), email.getText()
											.toString());
							} else {
								((MainActivity) getActivity()).repeatLogin();
							}
						}
					});
			builder.setNegativeButton("Cancel",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							((MainActivity) getActivity()).repeatLogin();
						}
					});
			return builder.create();
		}
	}
	
	public void repeatLogin() {
		LoginDialog loginDialog = LoginDialog.newInstance();
		loginDialog.show(getFragmentManager(), "dialog");
	}
	
	public static class AddFriendDialog extends DialogFragment {
		public static AddFriendDialog newInstance() {
			AddFriendDialog dialog = new AddFriendDialog();
			return dialog;
		}

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			// Get the layout inflater
			LayoutInflater inflater = getActivity().getLayoutInflater();
			View inflatedView = inflater.inflate(R.layout.dialog_add_friend, null);
			final EditText friendId = (EditText) inflatedView
					.findViewById(R.id.friend_id);
			builder.setView(inflatedView);

			builder.setPositiveButton("Ok",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							((MainActivity) getActivity())
									.addFriend(friendId.getText().toString());
						}
					});
			builder.setNegativeButton("Cancel",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {

						}
					});
			return builder.create();
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Intent songPlayIntent = new Intent(this, SongPlayActivity.class);
		songPlayIntent.putExtra("vidId", adapter.getItem(position).getUrl());
		songPlayIntent.putExtra("startTime", adapter.getItem(position).getStartTime());
		songPlayIntent.putExtra("length", adapter.getItem(position).getLength());
		startActivity(songPlayIntent);
	}
	
	public void pickSong()
	{
		Intent intent = new Intent(this, PickSong.class);
		intent.putExtra("userId", userId);
		intent.putExtra("friends", friends);
		startActivity(intent);
		
	}
}
