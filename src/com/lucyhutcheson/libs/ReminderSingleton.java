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

import java.util.ArrayList;
import java.util.HashMap;


public class ReminderSingleton {

	/**********************
	 * Private members
	 **********************/
	// Will hold single instance of this class
	private static ReminderSingleton _instance = null;
	// Will hold data for reminders
	private ArrayList<HashMap<String, String>> _reminderArrayList = new ArrayList<HashMap<String, String>>();
	
	
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
	public ArrayList<HashMap<String, String>> get_reminder(){
		return _reminderArrayList;
	}
	
	/**
	 * Set_movies.
	 *
	 * @param string the movie
	 */

	public void set_reminder(HashMap<String, String> hashMap) {
		_reminderArrayList.add(hashMap);
	}
}
