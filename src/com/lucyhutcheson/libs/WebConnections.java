package com.lucyhutcheson.libs;

import java.io.BufferedInputStream;
import java.net.URL;
import java.net.URLConnection;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class WebConnections {

	/**
	 * Checks the network connection. If available, pass in the url parameter
	 * and open the connection. After the data has been received, close the connection
	 * return the data response 
	 */
	
	static Boolean _conn = false;
	static String _connectionType = "Unavailable";
	
	// Get the connection type
	public static String getConnectionType(Context context){
		netInfo(context);
		return _connectionType;
	}
	
	// Find the connection status and return true or false
	public static Boolean getcConnectionStatus(Context context){
		netInfo(context);
		return _conn;
	}
	
	// Start connectivity manager and get the active network information to return
	private static void netInfo(Context context){
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE); 
		NetworkInfo ni = cm.getActiveNetworkInfo();
		if (ni != null){
			if(ni.isConnected()){
				_connectionType = ni.getTypeName();
				_conn = true;
			}
		}
	}
	
	// Open the URL connection and execute using the URL parameter passed in
	public static String getURLStringResponse(URL url){
		String response = "";
        //Log.i("WEBCONNECTIONS", "GETURLSTRINGRESPONSE: " + url.toString());
		
		try {
			// Open connection
			URLConnection conn = url.openConnection();  // open a connection first
			
			BufferedInputStream bin = new BufferedInputStream(conn.getInputStream());
			
			// Execute request
			byte[] contentBytes = new byte[1024];
			int bytesRead = 0;
			StringBuffer responseBuffer = new StringBuffer(); // holds the data coming in
			while((bytesRead = bin.read(contentBytes)) != -1){
				response = new String(contentBytes, 0, bytesRead);
				responseBuffer.append(response);
			} 
			//Log.i("BUFFER STRING", responseBuffer.toString());
			// Return response buffer that has all our data
			return responseBuffer.toString(); 
			
		} catch (Exception e) {
			// Log if we have any errors
			Log.e("URL RESPONSE ERROR", "getURLStringResponse: " + e.getLocalizedMessage());
		}
		
		return response;
	}

}
