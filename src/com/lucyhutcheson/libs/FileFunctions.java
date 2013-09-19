package com.lucyhutcheson.libs;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import android.content.Context;
import android.util.Log;

public class FileFunctions {
	// FUNCTION TO SAVE STRINGS
	public static Boolean storeStringFile(Context context, String filename, String content, Boolean external) {
		try {
			File file;
			FileOutputStream fos;
			
			// CHECK IF WE'RE WRITING TO THE SD CARD
			if (external){
				file = new File(context.getExternalFilesDir(null), filename);
				fos = new FileOutputStream(file);
			// ELSE WE WRITE TO INTERNAL STORAGE	
			} else {
				fos = context.openFileOutput(filename, Context.MODE_PRIVATE);
			}
			// WRITE/SAVE OUR DATA AND THEN CLOSE OUT THE STREAM
			fos.write(content.getBytes());
			fos.close();
		} catch (IOException e){
			Log.e("WRITE ERROR", filename);
		}
		return true;
		
	}
	// FUNCTION TO SAVE OBJECTS 
	public static Boolean storeObjectFile(Context _context, String filename, Object content, Boolean external) {
		try {
			File file;
			FileOutputStream fos;
			ObjectOutputStream oos;
			
			// CHECK IF WE'RE WRITING TO THE SD CARD
			if (external){
				file = new File(_context.getExternalFilesDir(null), filename);
				fos = new FileOutputStream(file);
			// ELSE WE WRITE TO INTERNAL STORAGE					
			} else {
				fos = _context.openFileOutput(filename, Context.MODE_PRIVATE);
			}
			// WRITE/SAVE OUR DATA OBJECT AND THEN CLOSE OUT THE STREAM
			oos = new ObjectOutputStream(fos);
			oos.writeObject(content);
			oos.close();
			fos.close();
		} catch (FileNotFoundException e) {
			Log.e("FILEFUNCTIONS", "File Not Found");
			e.printStackTrace();
		} catch (IOException e){
			Log.e("WRITE ERROR", filename);
		}
		return true;
	}
	
	public static String readStringFile(Context context, String filename, Boolean external) {
		String content = "";
		try {
			File file;
			FileInputStream fis;
			if (external) {
				file = new File(context.getExternalFilesDir(null), filename);
				fis = new FileInputStream(file);
			} else {
				file = new File(filename);
				fis = context.openFileInput(filename);
			}
			BufferedInputStream bis = new BufferedInputStream(fis);
			byte[] contentBytes = new byte[1024];
			int bytesRead = 0;
			StringBuffer contentBuffer = new StringBuffer();
			
			// READ OUR DATA
			while((bytesRead = bis.read(contentBytes)) != -1){
				content = new String(contentBytes, 0, bytesRead);
				contentBuffer.append(content);
			}
			content = contentBuffer.toString();
			// CLOSEOUT OUR STREAMS
			fis.close();
		} catch (FileNotFoundException e) {
			Log.e("READ ERROR", "FILE NOT FOUND " + filename);
		} catch (IOException e){
			Log.e("READ ERROR", "I/O ERROR");
		}
		// RETURN STRING
		return content;
	}
	
	public static Object readObjectFile(Context context, String filename, Boolean external) {
		Object content = new Object();
		try {
			File file;
			FileInputStream fis;
			if (external) {
				file = new File(context.getExternalFilesDir(null), filename);
				fis = new FileInputStream(file);
			} else {
				file = new File(filename);
				fis = context.openFileInput(filename);
			}
			
			ObjectInputStream ois = new ObjectInputStream(fis);
			try{
				content = (Object) ois.readObject();
			} catch (ClassNotFoundException e){
				Log.e("READ ERROR", "INVALID JAVA OBJECT FILE");
			}
			
			// CLOSEOUT OUR STREAMS
			ois.close();
			fis.close();
			
		} catch (FileNotFoundException e) {
			Log.e("READ ERROR", "FILE NOT FOUND " + filename);
			return null;
		} catch (IOException e){
			Log.e("READ ERROR", "I/O ERROR");
			return null;
		}
		// RETURN OBJECT
		return content;
	}

}
