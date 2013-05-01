package com.teamNFC.spartap;

import java.io.IOException;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class CheckIn extends Activity {
	String nfc_id;
	EventData eData;
	private String token;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		Intent myIntent= getIntent();
		final SpartapServerCommunicator comm = new SpartapServerCommunicator();
		nfc_id = myIntent.getStringExtra("nfc_id");
		nfc_id = nfc_id.replaceAll("[^0-9]+", "");
		Log.d("Does it have Garbage", nfc_id);
		SharedPreferences prefs = this.getSharedPreferences("MyPref", 0);
		token = prefs.getString("token", "");
		class EventCheckIn extends AsyncTask<String, Void, String> {

			@Override
			protected String doInBackground(String... params) {
					
					try {
						eData = new Gson().fromJson(
								comm.checkIn(params[0], params[1]),
								EventData.class);
					} 
					catch (Exception e) {
						
						e.printStackTrace();
					}
				return null;
			}

			@Override
			protected void onPostExecute(String result) {
				// Checks all possible results from check in and starts a particular activity
				super.onPostExecute(result);
				if(eData == null){
					Toast.makeText(CheckIn.this, "Connection Interrupted. Please try again.",Toast.LENGTH_LONG).show();
					Intent ourIntent = new Intent(CheckIn.this, InfoActivity.class);
					startActivity(ourIntent);
					finish();
				}
				if(eData.getError() == null){
					Toast.makeText(CheckIn.this, "Check In Successful!",Toast.LENGTH_LONG).show();
					Intent myIntent = new Intent(CheckIn.this, EventActivity.class);
                	myIntent.putExtra("nfc_id",nfc_id);
                	startActivity(myIntent);
                	finish();
				}
				else{
					Toast.makeText(CheckIn.this, eData.getError(),Toast.LENGTH_LONG).show();
					Intent ourIntent = new Intent(CheckIn.this, InfoActivity.class);
					startActivity(ourIntent);
					finish();
				}
				
			}

		}
		// Create new instance of class with URI and token as parameter
		new EventCheckIn().execute(nfc_id, token);

	} 
	
}
