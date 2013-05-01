package com.teamNFC.spartap;

import com.teamNFC.spartap.R;
import com.google.gson.Gson;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class InfoActivity extends Activity {
	String token = null;
	String studentID = "";
	AutoResizeTextView name;
	TextView points, textCheckIn, ID;
	Button QRCheckIn;
	UserData uData;
	EventData eData;
	String nfc_id;
	protected NfcAdapter nfcAdapter;
    protected PendingIntent nfcPendingIntent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_info);
		
		//Sets up the NFC reader in phone
		
		nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        nfcPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, this.getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        QRCheckIn = (Button) findViewById(R.id.bQRCheckIn);
		name = (AutoResizeTextView) findViewById(R.id.tName);
		points = (TextView) findViewById(R.id.tPoints);
		ID = (TextView) findViewById(R.id.ID);
		
		//Sets up the shared preferences for token and student ID
		
		SharedPreferences prefs = this.getSharedPreferences("MyPref", 0);
		token = prefs.getString("token", "");
		studentID = prefs.getString("Student ID", "");
		final SpartapServerCommunicator comm = new SpartapServerCommunicator();
		
		//Async task to retrieve user info and store in instance variables of uData object
		
		
		try {
			class UserInfo extends AsyncTask<String, Void, String> {

				@Override
				protected String doInBackground(String... params) {
					uData = new Gson().fromJson(comm.receiveUserInfo(token),
							UserData.class);
					return null;
				}

				@Override
				protected void onPostExecute(String result) {
					super.onPostExecute(result);
					if(uData == null){
						Toast.makeText(InfoActivity.this, "Cannot connect to the internet. Please try again.",Toast.LENGTH_LONG).show();
						name.setText("");
						points.setText("0");
						ID.setText(studentID);
					}
					else{
					name.setText(uData.getFirstName() + " "
							+ uData.getLastName());
					points.setText(uData.getPoints());
					ID.setText(studentID);
					}
				}

			}
			new UserInfo().execute();

		} catch (Exception e) {
			
			e.printStackTrace();
		}
		
		QRCheckIn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(InfoActivity.this, QRCheckin.class);
				startActivity(intent);
			}
		});

		
	}
	
	//Looks for NFC Tags when one is scanned and asks the user if the would like to check in.
	
	@Override
	protected void onResume() {
		super.onResume();
		
		enableForegroundMode();
			 if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
			        Parcelable[] messages = getIntent().getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);

            if (messages != null) {
                NdefMessage[] ndefMessages = new NdefMessage[messages.length];
                for (int i = 0; i < messages.length; i++) {
                    ndefMessages[i] = (NdefMessage) messages[i];
                }
            NdefRecord record = ndefMessages[0].getRecords()[0];

            byte[] payload = record.getPayload();
            nfc_id = new String(payload);
            AlertDialog.Builder builder = new AlertDialog.Builder(InfoActivity.this);
            builder.setCancelable(true);
            builder.setTitle("NFC Tag Found");
            builder.setMessage("Would you like to check in?");
            builder.setInverseBackgroundForced(true);
            builder.setPositiveButton("Yes",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog,
                                int which) {
                        	Intent myIntent = new Intent(InfoActivity.this, CheckIn.class);
                        	myIntent.putExtra("nfc_id",nfc_id);
                        	startActivity(myIntent);
                        	finish();
                            dialog.dismiss();
                            
                            
                        }
                    });
            builder.setNegativeButton("No",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog,
                                int which) {
                        	nfc_id = "";
                            dialog.dismiss();
                            
                            
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
	    }
     }
	}

	//Looks for NFC Tags when one is scanned and asks the user if the would like to check in.
	
	@Override
	public void onNewIntent(Intent intent) {
		
		   
		    if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {
		    	Parcelable[] messages = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);

	            if (messages != null) {
	                NdefMessage[] ndefMessages = new NdefMessage[messages.length];
	                for (int i = 0; i < messages.length; i++) {
	                    ndefMessages[i] = (NdefMessage) messages[i];
	                }
	            NdefRecord record = ndefMessages[0].getRecords()[0];

	            byte[] payload = record.getPayload();
	            nfc_id = new String(payload);
	            AlertDialog.Builder builder = new AlertDialog.Builder(InfoActivity.this);
                builder.setCancelable(true);
                builder.setTitle("NFC Tag Found");
                builder.setMessage("Would you like to check in?");
                builder.setInverseBackgroundForced(true);
                builder.setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                    int which) {
                            	Intent myIntent = new Intent(InfoActivity.this, CheckIn.class);
                            	myIntent.putExtra("nfc_id",nfc_id);
                            	startActivity(myIntent);
                            	finish();
                                dialog.dismiss();
                                
                                
                            }
                        });
                builder.setNegativeButton("No",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                    int which) {
                            	nfc_id = "";
                                dialog.dismiss();
                                
                                
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
		    }
         }
	}
public void enableForegroundMode() {

        // foreground mode gives the current active application priority for reading scanned tags
        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        IntentFilter[] writeTagFilters = new IntentFilter[] {tagDetected};
        nfcAdapter.enableForegroundDispatch(this, nfcPendingIntent, writeTagFilters, null);
}

//Creates the options menu

public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.menu, menu);
    return true;
}

public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
    case R.id.signout:
    	
    //Starts activity to sign out user.
    	
    startActivity(new Intent(this, SignOut.class));
    finish();
    return true;
    case R.id.refresh:
    	
    	//Refreshes info on info page.
    	
    	final SpartapServerCommunicator comm = new SpartapServerCommunicator();
    	try {
			class UserInfo extends AsyncTask<String, Void, String> {

				@Override
				protected String doInBackground(String... params) {
					
					uData = new Gson().fromJson(comm.receiveUserInfo(token),
							UserData.class);
					return null;
				}

				@Override
				protected void onPostExecute(String result) {
					super.onPostExecute(result);
					if(uData == null){
						Toast.makeText(InfoActivity.this, "Cannot connect to the internet. Please try again.",Toast.LENGTH_LONG).show();
						name.setText("");
						points.setText("0");
						ID.setText(studentID);
					}
					else{
					name.setText(uData.getFirstName() + " "
							+ uData.getLastName());
					points.setText(uData.getPoints());
					ID.setText(studentID);
					}
				}

			}
			new UserInfo().execute();

		} catch (Exception e) {
			
			e.printStackTrace();
		}
    return true;
    default:
    return super.onOptionsItemSelected(item);
    }
}
}
