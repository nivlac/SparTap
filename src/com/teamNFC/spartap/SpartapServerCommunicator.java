package com.teamNFC.spartap;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import android.util.Log;

/**
 * 
 * The Spartap Server Communicator for easy communication with the DeltaNFC server. Be sure to run
 * all functions using network access in a sub-class asynctask. "import android.os.AsyncTask;"
 *
 */

public class SpartapServerCommunicator {

	/**
	 * 
	 * @param The
	 *            username of the person.
	 * @param The
	 *            password of the person
	 * @param The
	 *            MAC Address of the device
	 * Logs the user into the server and sets the token instance variable to the users unique number.
	 * 
	 */
	public String login(String username, String password, String MACAddress) {
		String r = "";
		try{
		HttpParams httpParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParams, 5000);
		HttpConnectionParams.setSoTimeout(httpParams, 5000);
		HttpClient client = new DefaultHttpClient(httpParams);
		HttpPost request = new HttpPost("http://www.deltaNFC.com:80");
		String json = null;
			json = "{\"request\":\"login\",\"udid\":\"" + MACAddress +
					"\",\"username\":\"" + username + "\",\"password\":\"" + password +"\"}";

		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
        nameValuePairs.add(new BasicNameValuePair("access-type", "android"));
        nameValuePairs.add(new BasicNameValuePair("data", json));
		request.setEntity(new UrlEncodedFormEntity(nameValuePairs));

		HttpResponse response = client.execute(request);
		HttpEntity entity = response.getEntity();
	    r = EntityUtils.toString(entity);
		Log.d("response",r);
		return r;
		}
		catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	/**
	 * @param The token of the user
	 * Receives info about the user from the server. Sets the first name, last name, and points variables.
	 */
	public String receiveUserInfo(String t){
		String r = "";
		try{
		HttpParams httpParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParams, 10000);
		HttpConnectionParams.setSoTimeout(httpParams, 10000);
		HttpClient client = new DefaultHttpClient(httpParams);
		HttpPost request = new HttpPost("http://www.deltaNFC.com:80");
		String json = null;
			json = "{\"request\":\"identifyToken\",\"token\":\"" + t +"\"}";

		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
        nameValuePairs.add(new BasicNameValuePair("access-type", "android"));
        nameValuePairs.add(new BasicNameValuePair("data", json));
		request.setEntity(new UrlEncodedFormEntity(nameValuePairs));

		HttpResponse response = client.execute(request);
		HttpEntity entity = response.getEntity();
		r = EntityUtils.toString(entity);
		return r;
		}
		catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	/**
	 * 
	 * @param The nfc ID of the tag
	 * @param The token of the user
	 * Gets the event info
	 */
	public String getEventInfo(String ID, String t){
		String r = "";
		try{
		HttpParams httpParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParams, 10000);
		HttpConnectionParams.setSoTimeout(httpParams, 10000);
		HttpClient client = new DefaultHttpClient(httpParams);
		HttpPost request = new HttpPost("http://www.deltaNFC.com:80");
		String json = null;
			json = "{\"request\":\"getEventInfo\",\"token\":\"" + t +
					"\",\"nfc-id\":\"" + ID + "\"}";

		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
        nameValuePairs.add(new BasicNameValuePair("access-type", "android"));
        nameValuePairs.add(new BasicNameValuePair("data", json));
		request.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		HttpResponse response = client.execute(request);
		HttpEntity entity = response.getEntity();
		r = EntityUtils.toString(entity);
		return r;
		}
		catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 
	 * @param The nfc ID of the tag
	 * @param The token of the user.
	 * Checks user into event.
	 * 
	 */
	public String checkIn(String ID, String t) throws IOException{
		String r = "";
		try{
		HttpParams httpParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParams, 10000);
		HttpConnectionParams.setSoTimeout(httpParams, 10000);
		HttpClient client = new DefaultHttpClient(httpParams);
		HttpPost request = new HttpPost("http://www.deltaNFC.com:80");
		String json = null;
			json = "{\"request\":\"eventCheckInBeta\",\"token\":\"" + t +
					"\",\"nfc-id\":\"" + ID + "\"}";

		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
        nameValuePairs.add(new BasicNameValuePair("access-type", "android"));
        nameValuePairs.add(new BasicNameValuePair("data", json));
		request.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		Log.d("Sending",json);
		HttpResponse response = client.execute(request);
		HttpEntity entity = response.getEntity();
	    r = EntityUtils.toString(entity);
		return r;
		}
		catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}

}
