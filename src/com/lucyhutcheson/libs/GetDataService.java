package com.lucyhutcheson.libs;

import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONObject;

import android.app.Activity;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

public class GetDataService extends IntentService  {

	public static final String SEARCH_KEY = "search";
	public static final String MESSENGER_KEY = "messenger";
	Messenger messenger;
	Message message;
	String response;
	String noResponseError;

	/**
	 * Instantiates a new download service.
	 *           
	 */
	public GetDataService() {
		super("GetDataService");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.IntentService#onHandleIntent(android.content.Intent)
	 */
	@Override
	protected void onHandleIntent(Intent intent) {
		Log.i("GETDATA SERVICE", "GETDATA SERVICE STARTED");
		
        String localUrlString = intent.getDataString();
		Bundle extras = intent.getExtras();
		messenger = (Messenger) extras.get(MESSENGER_KEY);
		message = Message.obtain();

		// SETUP OUR URL TO QUERY
		URL localURL = null;
        try {
			localURL = new URL(localUrlString);
		} catch (MalformedURLException e1) {
			Log.e("ERROR", e1.toString());
			e1.printStackTrace();
		}
        
        // CHECK OUR NETWORK CONNECTION
		ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

		// QUERY URL IF NETWORK IS AVAILABLE
		if (networkInfo != null && networkInfo.isConnected()) {
			try {
				response = WebConnections.getURLStringResponse(localURL);

				try {
					// GET OUR JSON RESPONSE
					JSONObject jsonResult = new JSONObject(response);
					//Log.i("JSON OBJECT", jsonResult.toString());
					
					
					// NO LOCATION RECEIVED
					if (jsonResult.isNull("current_observation")) {
						Log.i("JSON RESPONSE", "NO RESPONSE RECEIVED");
						Toast toast = Toast.makeText(getApplicationContext(), "Location Not Found",
								Toast.LENGTH_SHORT);
						toast.show();
						message.arg1 = Activity.RESULT_CANCELED;
						message.obj = "Location Not Found";
						try {
							messenger.send(message);
						} catch (RemoteException e) {
							response = null;
							message.arg1 = Activity.RESULT_CANCELED;
							message.obj = "Location Not Found";
							Log.i("MESSENGER", "ERROR SENDING MESSAGE");
							e.printStackTrace();
						}

					} else {
						
						//Log.i("JSON RESPONSE DATA", jsonResult.getString("current_observation"));
						
						// GET OUR DATA FROM THE JSON ARRAY
						JSONObject arrayResults = jsonResult.getJSONObject("current_observation");
												
						// SAVE THE DATA TO OUR TEMP FILE FOR INCLUSION IN FAVORITES IF SELECTED BY USER
						FileFunctions.storeStringFile(getApplicationContext(), "temp", arrayResults.toString(), true);
						
						// SETUP OUR MESSAGE AND SEND
						message.arg1 = Activity.RESULT_OK;
						message.obj = arrayResults;
						
						try {
							messenger.send(message);
						} catch (RemoteException e) {
							response = null;
							message.arg1 = Activity.RESULT_CANCELED;
							message.obj = "Location not found";
							Log.i("MESSENGER", "ERROR SENDING MESSAGE");
							e.printStackTrace();
						}
					}
				} catch (Exception e) {
					message.arg1 = Activity.RESULT_CANCELED;
					message.obj = "Service completed";
					Log.e("ERROR", e.toString());
					e.printStackTrace();
				}
			} catch (Exception e) {
				response = null;
				message.arg1 = Activity.RESULT_CANCELED;
				Log.e("ERROR", e.toString());
				e.printStackTrace();
				localURL = null;
			}

		} 
		// No network connection available
		else {
			message.arg1 = Activity.RESULT_CANCELED;
			message.obj = "Service completed";
			Toast toast = Toast.makeText(getApplicationContext(), "No network detected.",
					Toast.LENGTH_SHORT);
			toast.show();
		}

	}
}
