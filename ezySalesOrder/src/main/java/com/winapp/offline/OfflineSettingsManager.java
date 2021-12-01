package com.winapp.offline;

import com.winapp.helper.Constants;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class OfflineSettingsManager implements Constants{
	private SharedPreferences preferences;
	private Context context;

	public OfflineSettingsManager(Context context) {
		this.context = context;
		preferences = this.context.getSharedPreferences(PREF_OFFLINE,
				Context.MODE_PRIVATE);
	}

	public void setCompanyType(String applicationtype) {
		Editor editor = preferences.edit();
		editor.putString(PREF_ALLOW_COMPANYTYPE, applicationtype);
		editor.commit();
	}

	public String getCompanyType() {
		return preferences.getString(PREF_ALLOW_COMPANYTYPE, null);
	}
	
	public void setOverdue(String overdue) {
		  Editor editor = preferences.edit();
		  editor.putString(PREF_ALLOW_OVERDUE, overdue);
		  editor.commit();
		 }

		 public String getOverdue() {
		  return preferences.getString(PREF_ALLOW_OVERDUE, null);
		 }
}
