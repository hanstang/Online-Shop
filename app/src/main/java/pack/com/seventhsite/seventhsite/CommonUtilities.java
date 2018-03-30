package pack.com.seventhsite.seventhsite;

import android.content.Context;
import android.content.Intent;

/**
 * Created by Samsung_PC on 9/17/2014.
 */
public class CommonUtilities {
    // give your server registration url here
    static final String SERVER_URL = "http://"+varGlobal.ipconnection+"/service_android/gcm/register.php";

    // Google project id
    static final String SENDER_ID = "531999984776";

    /**
     * Tag used on log messages.
     */
    static final String TAG = "Seventhsite";

    static final String DISPLAY_MESSAGE_ACTION =
            "pack.com.seventhsite.seventhsite.DISPLAY_MESSAGE";

    static final String EXTRA_MESSAGE = "message";

    /**
     * Notifies UI to display a message.
     * <p>
     * This method is defined in the common helper because it's used both by
     * the UI and the background service.
     *
     * @param context application's context.
     * @param message message to be displayed.
     */
    static void displayMessage(Context context, String message) {
        Intent intent = new Intent(DISPLAY_MESSAGE_ACTION);
        intent.putExtra(EXTRA_MESSAGE, message);
        context.sendBroadcast(intent);
    }
}
