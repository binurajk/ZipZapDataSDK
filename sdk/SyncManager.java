package com.zipzap.sdk;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

public class SyncManager 
		implements LoaderManager.LoaderCallbacks<Cursor>, ISyncManager {
	
	private static SyncManager _instance;
	
	private FragmentActivity activity;
	private CursorLoader cursorLoader;
	private String backupDir;
	private String backupFile;
	
	private SyncManager(FragmentActivity activity, String backupDir, String backupFile) {
		this.activity = activity;
		this.backupDir = backupDir;
		this.backupFile = backupFile;
	}
	
	public void getZipZapData() {
		activity.getSupportLoaderManager().initLoader(1, null, this);
	}
	
	public void putZipZapData() {
		ContextWrapper cw = new ContextWrapper(activity);
		File directory = cw.getDir(this.backupDir, Context.MODE_PRIVATE);
		File mypath = new File(directory, this.backupFile);
		byte[] fileData = null;

		try {
			FileInputStream in = new FileInputStream(mypath);
			int size = (int) mypath.length();
			fileData = new byte[size];

			in.read(fileData, 0, size);
			in.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
		
		ContentValues values = new ContentValues();
		values.put("name", fileData);
		activity.getContentResolver().update(Uri.parse("content://com.zipzapsync.MyProvider/cte"), values, null, null);
		
	}

	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		cursorLoader = new CursorLoader(activity, Uri.parse("content://com.zipzapsync.MyProvider/cte"), null, null, null, null);
		return cursorLoader;
	}
	
	@Override
	public void onLoadFinished(android.support.v4.content.Loader<Cursor> arg0,
			Cursor cursor) {
		cursor.moveToFirst();
		byte[] blob = null;
		while (!cursor.isAfterLast()) {
			blob = cursor.getBlob(cursor.getColumnIndex("name"));
			cursor.moveToNext();
		}

		if (blob != null) {
			Bitmap bitmap = BitmapFactory.decodeByteArray(blob, 0, blob.length);

	        ContextWrapper cw = new ContextWrapper(activity);
			File directory = cw.getDir(this.backupDir, Context.MODE_PRIVATE);
			File mypath = new File(directory, this.backupFile);
			FileOutputStream out = null;
			
	        try {
	        	
	        	out = new FileOutputStream(mypath);
	        	bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
	            
	        } catch (FileNotFoundException e) {
	        	e.printStackTrace();
	        }			
		}
		else {
			//nothing there on content provider
		}
	}

	@Override
	public void onLoaderReset(android.support.v4.content.Loader<Cursor> arg0) {
		
	}

	public static SyncManager getInstance(FragmentActivity activity, String backupDir, String backupFile) {
		
		if (_instance == null) {
			_instance = new SyncManager(activity, backupDir, backupFile);
		}
		return _instance;
	}
	
}
