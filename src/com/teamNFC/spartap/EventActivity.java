package com.teamNFC.spartap;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.teamNFC.spartap.R;
import com.google.gson.Gson;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class EventActivity extends Activity {
	TextView title, description, startTime, endTime, location;
	Button buttonBack;
	private String token;
	private String nfc_id;
	EventData eData;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_event);
		title = (TextView)findViewById(R.id.tTitle);
		description = (TextView)findViewById(R.id.tDescription);
		startTime = (TextView)findViewById(R.id.tStartTime);
		endTime = (TextView)findViewById(R.id.tEndTime);
		location = (TextView)findViewById(R.id.tLocation);
		buttonBack = (Button)findViewById(R.id.bGoBack);
		Intent myIntent= getIntent();
		nfc_id = myIntent.getStringExtra("nfc_id");
		nfc_id = nfc_id.replaceAll("[^0-9]+", "");
		final SpartapServerCommunicator comm = new SpartapServerCommunicator();
		SharedPreferences prefs = this.getSharedPreferences("MyPref", 0);
		token = prefs.getString("token", "");
		class EventInfo extends AsyncTask<String, Void, String> {

			@Override
			protected String doInBackground(String... params) {
					eData = new Gson().fromJson(
							comm.getEventInfo(params[0], params[1]),
							EventData.class);
				return null;
			}

			@Override
			protected void onPostExecute(String result) {
			
				super.onPostExecute(result);
				if(eData == null){
					Toast.makeText(EventActivity.this, "Connection Interrupted. Please try again.",Toast.LENGTH_LONG).show();
					Intent ourIntent = new Intent(EventActivity.this, InfoActivity.class);
					startActivity(ourIntent);
					finish();
				}
				else{
					
					int st = Integer.parseInt(eData.getStarttime());
					int en = Integer.parseInt(eData.getEndtime());
					long start = Long.valueOf(st)*1000;
					long end = Long.valueOf(en)*1000;
					Date dS = new java.util.Date(start);
					Date dE = new java.util.Date(end);
					String startDate = new SimpleDateFormat("'Start Time: 'MM/dd/yyyy hh:mma").format(dS);
					String endDate = new SimpleDateFormat("'End Time: 'MM/dd/yyyy hh:mma").format(dE);
				title.setText(eData.getTitle());
				description.setText(eData.getDescription());
				startTime.setText(startDate);
				endTime.setText(endDate);
				location.setText("Location:\n" + eData.getLocation());
				}
				
			}

		}
		if (isOnline()){
		new EventInfo().execute(nfc_id, token);
		}
		else{
			Toast.makeText(EventActivity.this, "Cannot connect to the internet. Please try again.",Toast.LENGTH_LONG).show();
		}
		
		
		buttonBack.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent ourIntent = new Intent(EventActivity.this, InfoActivity.class);
				startActivity(ourIntent);
				finish();
			}
	} );
	}
	public boolean isOnline() {
	    ConnectivityManager cm =
	        (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo netInfo = cm.getActiveNetworkInfo();
	    if (netInfo != null && netInfo.isConnectedOrConnecting()) {
	        return true;
	    }
	    return false;
	}
	
}
	
