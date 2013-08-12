package com.scanchex.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.scanchex.ui.R;

public class SCPreferences {

	private static SCPreferences userInfo;

	public static SCPreferences getPreferences() {
		if (userInfo == null) {
			userInfo = new SCPreferences();
		}
		return userInfo;
	}

	public String getUserName(Context context) {
		SharedPreferences manager = PreferenceManager.getDefaultSharedPreferences(context);
//		return manager.getString(context.getString(R.string.pref_user_name), "adnan");//admin
		return manager.getString(context.getString(R.string.pref_user_name), "julie");//employee
//		return manager.getString(context.getString(R.string.pref_user_name), "");
	}

	public void setUserName(Context context, String number) {
		PreferenceManager.getDefaultSharedPreferences(context).edit().putString(context.getString(R.string.pref_user_name), number).commit();
	}

	public String getUserMasterKey(Context context) {
		SharedPreferences manager = PreferenceManager.getDefaultSharedPreferences(context);
		return manager.getString(context.getString(R.string.pref_master_key),"");
	}

	public void setUserMasterKey(Context context, String number) {
		PreferenceManager.getDefaultSharedPreferences(context).edit().putString(context.getString(R.string.pref_master_key), number).commit();
	}
	
	
	public String getUserFullName(Context context) {
		SharedPreferences manager = PreferenceManager.getDefaultSharedPreferences(context);
		return manager.getString(context.getString(R.string.pref_userfull_name),"");
	}

	public void setUserFullName(Context context, String number) {
		PreferenceManager.getDefaultSharedPreferences(context).edit().putString(context.getString(R.string.pref_userfull_name), number).commit();
	}
	
	
	public String getCompanyId(Context context) {
		SharedPreferences manager = PreferenceManager.getDefaultSharedPreferences(context);
//		return manager.getString(context.getString(R.string.pref_company_id),"pk-007");//admin
		return manager.getString(context.getString(R.string.pref_company_id),"pk-007");//employee
//		return manager.getString(context.getString(R.string.pref_company_id),"");
	}

	public void setCompanyId(Context context, String cid) {
		PreferenceManager.getDefaultSharedPreferences(context).edit().putString(context.getString(R.string.pref_company_id), cid).commit();
	}
}
