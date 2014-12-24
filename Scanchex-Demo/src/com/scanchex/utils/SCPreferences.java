package com.scanchex.utils;

import android.app.Activity;
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
		SharedPreferences manager = PreferenceManager
				.getDefaultSharedPreferences(context);
		return manager.getString(context.getString(R.string.pref_user_name), "");// employee
	}

	public void setUserName(Context context, String number) {
		PreferenceManager.getDefaultSharedPreferences(context).edit()
				.putString(context.getString(R.string.pref_user_name), number)
				.commit();
	}

	public String getUserMasterKey(Context context) {
		SharedPreferences manager = PreferenceManager
				.getDefaultSharedPreferences(context);
		return manager.getString(context.getString(R.string.pref_master_key),
				"");
	}

	public void setUserMasterKey(Context context, String number) {
		PreferenceManager.getDefaultSharedPreferences(context).edit()
				.putString(context.getString(R.string.pref_master_key), number)
				.commit();
	}
	
	public String getClientLogo(Context context) {
		SharedPreferences manager = PreferenceManager
				.getDefaultSharedPreferences(context);
		return manager.getString("logo","");	
	}

	public void setClientLogo(Context context, String cid) {
		PreferenceManager.getDefaultSharedPreferences(context).edit()
				.putString("logo", cid)
				.commit();
	}

	public String getUserFullName(Context context) {
		SharedPreferences manager = PreferenceManager
				.getDefaultSharedPreferences(context);
		return manager.getString(
				context.getString(R.string.pref_userfull_name), "");
	}

	public void setUserFullName(Context context, String number) {
		PreferenceManager
				.getDefaultSharedPreferences(context).edit().putString(context.getString(R.string.pref_userfull_name),
						number).commit();
	}

	public String getCompanyId(Context context) {
		SharedPreferences manager = PreferenceManager
				.getDefaultSharedPreferences(context);
		return manager.getString(context.getString(R.string.pref_company_id),"");	
	}

	public void setCompanyId(Context context, String cid) {
		PreferenceManager.getDefaultSharedPreferences(context).edit()
				.putString(context.getString(R.string.pref_company_id), cid)
				.commit();
	}
	
	
	

	public int getUserType(Context context) {
		SharedPreferences manager = PreferenceManager
				.getDefaultSharedPreferences(context);
		return manager.getInt(context.getString(R.string.user_type), 0);// employee
	}

	public void setUserType(Context context, int userType) {
		PreferenceManager.getDefaultSharedPreferences(context).edit()
				.putInt(context.getString(R.string.user_type), userType)
				.commit();
	}

	public static void setComapnyUserName(Activity mActivity, String companyUser) {
		SharedPreferences sharedPreferences = mActivity.getSharedPreferences(
				"pref", Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putString("company", companyUser);
		editor.commit();
	}

	public static String getCompanyUserName(Activity mActivity) {
		SharedPreferences sharedPreferences = mActivity.getSharedPreferences(
				"pref", Activity.MODE_PRIVATE);

		String company = sharedPreferences.getString("company", "");

		return company;

	}
	
	public static void setColor(Activity mActivity, int color) {
		SharedPreferences sharedPreferences = mActivity.getSharedPreferences(
				"pref", Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putInt("color", color);
		editor.commit();
	}

	public static int getColor(Activity mActivity) {
		SharedPreferences sharedPreferences = mActivity.getSharedPreferences(
				"pref", Activity.MODE_PRIVATE);

		int color = sharedPreferences.getInt("color", 0);

		return color;

	}
	
	
	public static void setEmployeeCard(Activity mActivity, String companyUser) {
		SharedPreferences sharedPreferences = mActivity.getSharedPreferences(
				"pref", Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putString("employeeCard", companyUser);
		editor.commit();
	}

	public static String getEmployeeCard(Activity mActivity) {
		SharedPreferences sharedPreferences = mActivity.getSharedPreferences(
				"pref", Activity.MODE_PRIVATE);

		String employee = sharedPreferences.getString("employeeCard", "");

		return employee;

	}
}
