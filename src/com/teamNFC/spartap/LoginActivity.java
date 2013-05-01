package com.teamNFC.spartap;

import com.teamNFC.spartap.R;
import com.google.gson.Gson;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class LoginActivity extends Activity {
	String token = "";
	String studentID = "";
	Button buttonGetInfo, buttonCheckIn;
	ImageButton buttonLogin;
	EditText user, pass, nfcID;
	String MAC;
	UserData uData;
	ProgressDialog progressDialog;
	private Context thisContext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		thisContext = this;
		
		//Creates a data point in sharedPreferences to store the token.
		 
		SharedPreferences pref = getSharedPreferences("MyPref", 0);
		String value = pref.getString("token",null);
		
		//Checks to see if there is a token stored on phone, if there is then go to InfoActivity
		
		if(value != null){
			Intent ourIntent = new Intent(LoginActivity.this, InfoActivity.class);
			startActivity(ourIntent);
			finish();
		}
		setContentView(R.layout.activity_login);
		final SpartapServerCommunicator comm = new SpartapServerCommunicator();
		buttonLogin = (ImageButton)findViewById(R.id.bLoginImg);
		user = (EditText)findViewById(R.id.etUsername);
		pass = (EditText)findViewById(R.id.etPassword);
		MAC = "21:32:aa:56:32";
		final Editor editor = pref.edit();
		
		//Sets up the login button
		
		buttonLogin.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//Makes the login button look pressed.
				buttonLogin.setColorFilter(Color.argb(150, 155, 155, 155));
				
				//Async task to login and retrieve login token.
				try {					
					class Login extends AsyncTask<String, Void, String>{
						@Override
					    protected void onPreExecute() {
					        // TODO Auto-generated method stub
							progressDialog = ProgressDialog.show(LoginActivity.this, "Logging In","Please Wait...", true);
					    }
						@Override
						protected String doInBackground(String... params) {
							uData = new Gson().fromJson(comm.login(params[0], params[1], params[2]), UserData.class);
							return null;
						}

						@Override
						protected void onPostExecute(String result) {
							//Set text to the token you get from server
							super.onPostExecute(result);
							if(uData == null){
								progressDialog.dismiss();
								buttonLogin.setColorFilter(Color.argb(0, 155, 155, 155));
								Toast.makeText(thisContext, "Connection interrupted. Please try again.",Toast.LENGTH_LONG).show();
							}
							else{
							progressDialog.dismiss();
							buttonLogin.setColorFilter(Color.argb(0, 155, 155, 155));
							if(uData.getError() == null)
							{
							editor.putString("token", uData.getToken());
							editor.putString("Student ID", studentID);
							editor.commit();							
							Intent ourIntent = new Intent(LoginActivity.this, InfoActivity.class);
							startActivity(ourIntent);
							finish();
							}
							else{
								AlertDialog.Builder builder = new AlertDialog.Builder(thisContext);
				                builder.setCancelable(true);
				                builder.setTitle("Error");
				                builder.setMessage(uData.getError());
				                builder.setInverseBackgroundForced(true);
				                builder.setNeutralButton("Cancel",
				                        new DialogInterface.OnClickListener() {
				                            @Override
				                            public void onClick(DialogInterface dialog,
				                                    int which) {
				                            	
				                                dialog.dismiss();
				                                
				                            }
				                        });
				                AlertDialog alert = builder.create();
				                alert.show();
				                user.setText("");
				                pass.setText("");
							}
							
						}
						}
						
						
					}
					
					//Checks to see if the user is online before executing the login.
					
					if (isOnline()){
					studentID = user.getText().toString();
					new Login().execute(user.getText().toString(), pass.getText().toString(),MAC);
					}
					else{
						Toast.makeText(thisContext, "Cannot connect to the internet. Please try again.",Toast.LENGTH_LONG).show();
					}
					
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
	}
	/*
	 * Returns whether the user is online or not.
	 */
	public boolean isOnline() {
	    ConnectivityManager cm =
	        (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo netInfo = cm.getActiveNetworkInfo();
	    if (netInfo != null && netInfo.isConnectedOrConnecting()) {
	        return true;
	    }
	    return false;
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

}
