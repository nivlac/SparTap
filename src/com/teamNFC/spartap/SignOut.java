package com.teamNFC.spartap;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;

public class SignOut extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		SharedPreferences prefs = this.getSharedPreferences("MyPref", 0);
		Editor editor = prefs.edit();
		editor.clear();
		editor.commit();
		Intent ourIntent = new Intent(SignOut.this, LoginActivity.class);
		startActivity(ourIntent);
		finish();
	}
	
	
}
