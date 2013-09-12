/*
 * project		HouseBoss
 * 
 * package		com.lucyhutcheson.libs
 * 
 * @author		Lucy Hutcheson
 * 
 * date			Sep 11, 2013
 * 
 */
package com.lucyhutcheson.libs;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

// TODO: Auto-generated Javadoc
/**
 * The Class AppPreferences.
 */
public class AppPreferences {
	public static final String TAG = "AppPreferences";
	public static final String KEY_PREFS_ZIP = "zip";
	public static final String KEY_PREFS_SETUP = "setup";
	private static final String APP_SHARED_PREFS = AppPreferences.class.getSimpleName();
	private SharedPreferences _sharedPrefs;
	private Editor _prefsEditor;
	
	/**
	 * Instantiates a new app preferences.
	 *
	 * @param context the context
	 */
	public AppPreferences(Context context) {
		this._sharedPrefs = context.getSharedPreferences(APP_SHARED_PREFS, Activity.MODE_PRIVATE);
		this._prefsEditor = _sharedPrefs.edit();
	}
	
	/**
	 * Gets the zip code saved by the user.
	 *
	 * @return the zip
	 */
	public String getZip() {
		Log.i(TAG, "Was saved: " + _sharedPrefs.getString(KEY_PREFS_ZIP, ""));
		return _sharedPrefs.getString(KEY_PREFS_ZIP, "");
	}
	
	/**
	 * Save zip code entered in by user.
	 *
	 * @param text the text
	 */
	public void saveZip(String text) {
		Log.i(TAG, "Saving: " + text);
		_prefsEditor.putString(KEY_PREFS_ZIP, text);
		_prefsEditor.commit();
	}
	
	/**
	 * Checks whether this is the first time the app was opened. It will be empty.
	 *
	 * @return the setup
	 */
	public String getSetup() {
		Log.i(TAG, "Was saved: " + _sharedPrefs.getString(KEY_PREFS_ZIP, ""));
		return _sharedPrefs.getString(KEY_PREFS_SETUP, "");
	}

	/**
	 * Saves the first time the app was opened.
	 *
	 * @param text the text
	 */
	public void saveSetup(String text) {
		Log.i(TAG, "Saving: " + text);
		_prefsEditor.putString(KEY_PREFS_SETUP, text);
		_prefsEditor.commit();
	}

}
