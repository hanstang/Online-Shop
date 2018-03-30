package pack.com.seventhsite.seventhsite;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.gcm.GCMBaseIntentService;

import static pack.com.seventhsite.seventhsite.CommonUtilities.SENDER_ID;
import static pack.com.seventhsite.seventhsite.CommonUtilities.displayMessage;

/**
 * Created by Samsung_PC on 9/17/2014.
 */
public class GCMIntentService extends GCMBaseIntentService {
    public static final int NOTIFICATION_ID = 1;
    private static final String TAG = "GCMBaseIntentService";
    NotificationManager mNotificationManager;

    public GCMIntentService() {
        super(SENDER_ID);
    }

    /**
     * Method called on device registered
     **/
    @Override
    protected void onRegistered(Context context, String registrationId) {
        Log.i(TAG, "Device registered: regId = " + registrationId);
        displayMessage(context, "Your device registred with GCM");
        //Log.d("NAME", HomeActivity.name);
        //ServerUtilities.register(context, HomeActivity.name, HomeActivity.email, registrationId);
    }

    /**
     * Method called on device un registred
     * */
    @Override
    protected void onUnregistered(Context context, String registrationId) {
        Log.i(TAG, "Device unregistered");
        displayMessage(context, getString(R.string.gcm_unregistered));
        //ServerUtilities.unregister(context, registrationId);
    }

    /**
     * Method called on Receiving a new message
     * */
    @Override
    protected void onMessage(Context context, Intent intent) {
        Log.i(TAG, "Received message");
        try {
            String message = intent.getExtras().getString("price");
            Log.d("req id :", message);
            displayMessage(context, message);
            // notifies user
            generateNotification(context, message);

        } catch (Exception e) {
            e.printStackTrace();
        }
        /*
        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, HomeActivity.class), 0);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.atas)
                        .setContentTitle("GCM Notification")
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText("woi"))
                        .setContentText("hallo");

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
        */
    }

    /**
     * Method called on receiving a deleted message
     * */
    @Override
    protected void onDeletedMessages(Context context, int total) {
        Log.i(TAG, "Received deleted messages notification");
        String message = getString(R.string.gcm_deleted, total);
        displayMessage(context, message);
        //notifies user
        generateNotification(context, message);
    }

    /**
     * Method called on Error
     * */
    @Override
    public void onError(Context context, String errorId) {
        Log.i(TAG, "Received error: " + errorId);
        //displayMessage(context, getString(R.string.gcm_error, errorId));
    }

    @Override
    protected boolean onRecoverableError(Context context, String errorId) {
        // log message
        Log.i(TAG, "Received recoverable error: " + errorId);
        displayMessage(context, getString(R.string.gcm_recoverable_error,
                errorId));
        return super.onRecoverableError(context, errorId);
    }

    /**
     * Issues a notification to inform the user that server has sent a message.
     */
    private static void generateNotification(Context context, String message) {

        int icon = R.drawable.atas;
        long when = System.currentTimeMillis();
        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion < android.os.Build.VERSION_CODES.HONEYCOMB){

            Notification notification = new Notification(icon, message, when);

            String title = context.getString(R.string.app_name);

            Intent notificationIntent = new Intent(context, HomeActivity.class);
            // set intent so it does not start a new activity
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            PendingIntent intent =
                    PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);
            notification.setLatestEventInfo(context, title, message, intent);
            notification.flags |= Notification.FLAG_AUTO_CANCEL;

            // Play default notification sound
            notification.defaults |= Notification.DEFAULT_SOUND;

            // Vibrate if vibrate is enabled
            notification.defaults |= Notification.DEFAULT_VIBRATE;
            notificationManager.notify(0, notification);
        }
        else
        {

            //String title = context.getString(R.string.app_name);

            Intent notificationIntent = new Intent(context, LoginActivity.class);
            // set intent so it does not start a new activity
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            PendingIntent intent =
                    PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);
            //notification.setLatestEventInfo(context, title, message, intent);
            Notification notification = new Notification.Builder(context)
                    .setContentTitle("Seventhsite")
                    .setContentText(message)
                    .setSmallIcon(icon)
                    .setWhen(when)
                    .setContentIntent(intent)
                    .build();
            notification.flags |= Notification.FLAG_AUTO_CANCEL;

            // Play default notification sound
            notification.defaults |= Notification.DEFAULT_SOUND;

            // Vibrate if vibrate is enabled
            notification.defaults |= Notification.DEFAULT_VIBRATE;
            notificationManager.notify(7, notification);
        }
    }
}
