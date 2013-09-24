/*
 * project		HouseBoss
 * 
 * package		com.lucyhutcheson.libs
 * 
 * @author		Lucy Hutcheson
 * 
 * date			Sep 19, 2013
 * 
 */
package com.lucyhutcheson.libs;

import java.util.HashMap;


public class ReminderSingleton {

	/**********************
	 * Private members
	 **********************/
	// Will hold single instance of this class
	private static ReminderSingleton _instance = null;
	// Will hold data for reminders
	private HashMap<String, String> _reminderHashMap = new HashMap<String, String>();
	private HashMap<String, HashMap<String, String>> _remindersHashMap = new HashMap<String, HashMap<String, String>>();
	
	
	/**
	 * ********************
	 * Private Methods
	 * ********************.
	 */
	// Used to hide constructor and prevent other classes from instantiating
	protected ReminderSingleton() {
	}
	
	/**
	 * ********************
	 * Public method
	 * ********************.
	 *
	 * @return single instance of ReminderSingleton Class
	 */
	public static ReminderSingleton getInstance() {
		// Create a new instantiation if there isn't one
		if(_instance == null){
			_instance = new ReminderSingleton();
		}
		// Otherwise, just return the one already created
		return _instance;
	}
	
	/**
	 * *************************
	 * Public setters / getters
	 * *************************.
	 *
	 */
	public HashMap<String, String> get_reminder(){
		return _reminderHashMap;
	}
	public HashMap<String, HashMap<String,String>> get_reminders(){
		return _remindersHashMap;
	}
	
	/**
	 * Set_movies.
	 *
	 * @param string the movie
	 */

	public void set_reminder(HashMap<String, String> hashMap) {
		_reminderHashMap.putAll(hashMap);
	}
	public void set_reminders(HashMap<String, HashMap<String,String>> hashMap) {
		_remindersHashMap.putAll(hashMap);
	}

}
