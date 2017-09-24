package com.zipzap.sdk;

import android.support.v4.app.FragmentActivity;

public class SyncFactory {
	public static ISyncManager getSyncManger(FragmentActivity activity,
			String backupDir, String backupFile) {

		return (ISyncManager) SyncManager.getInstance(activity, backupDir, backupFile);
	}
}
