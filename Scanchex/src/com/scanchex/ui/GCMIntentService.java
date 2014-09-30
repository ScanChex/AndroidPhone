package com.scanchex.ui;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.gcm.GCMBaseIntentService;
import com.scanchex.utils.Resources;

public class GCMIntentService extends GCMBaseIntentService {

	private static final String TAG = "GCMIntentService";
	public static final String senderId = "676840218679";// ScanChexTest

	private String deviceToken;

	public GCMIntentService() {
		super(senderId);
	}

	@Override
	protected void onRegistered(Context context, String registrationId) {
		Log.i("Registration ID From GS", "Device registered: regId = "
				+ registrationId);
		deviceToken = registrationId;
		Resources.getResources().setPushNotificationId(deviceToken);
	}

	@Override
	protected void onUnregistered(Context context, String registrationId) {
		Log.i(TAG, "Device unregistered");
	}

	@Override
	protected void onMessage(Context context, Intent intent) {
		Log.i(TAG,
				"GCM Received message"
						+ intent.getExtras().getString("message"));
		String message = intent.getExtras().getString("message");
		// notifies user
		generateNotification(context, message);
	}

	@Override
	protected void onDeletedMessages(Context context, int total) {
		Log.i(TAG, "Received deleted messages notification");
		String message = getString(R.string.gcm_deleted, total);
		// notifies user
		generateNotification(context, message);
	}

	@Override
	public void onError(Context context, String errorId) {
		Log.i(TAG, "Received error: " + errorId);
	}

	@Override
	protected boolean onRecoverableError(Context context, String errorId) {
		// log message
		Log.i(TAG, "Received recoverable error: " + errorId);
		return super.onRecoverableError(context, errorId);
	}

	/**
	 * Issues a notification to inform the user that server has sent a message.
	 */
	private static void generateNotification(Context context, String message) {

		Context currentContext = Resources.getResources().getCurrentContext();
		if (currentContext instanceof SCMainMenuScreen) {
			((SCMainMenuScreen) currentContext)
					.showPushNotificationAlert(message);
		} else if (currentContext instanceof SCTicketViewScreen) {
			((SCTicketViewScreen) currentContext)
					.showPushNotificationAlert(message);
		} else if (currentContext instanceof SCDetailsFragmentScreen) {
			((SCDetailsFragmentScreen) currentContext)
					.showPushNotificationAlert(message);
		}

		int icon = R.drawable.app_icon;
		long when = System.currentTimeMillis();
		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notification = new Notification(icon, message, when);
		String title = context.getString(R.string.app_name);
		Intent notificationIntent = new Intent(context,
				SCMessageViewScreen.class);
		// set intent so it does not start a new activity
		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
				| Intent.FLAG_ACTIVITY_SINGLE_TOP);
		PendingIntent intent = PendingIntent.getActivity(context, 0,
				notificationIntent, 0);
		notification.setLatestEventInfo(context, title, message, intent);
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		notification.defaults |= Notification.DEFAULT_SOUND;
		notification.defaults |= Notification.DEFAULT_VIBRATE;
		notificationManager.notify(0, notification);
	}
}
