package pack.com.seventhsite.seventhsite;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import java.util.HashMap;

/**
 * Created by Samsung_PC on 8/1/2014.
 */
public class SessionManagement {
    // Shared Preferences
    SharedPreferences pref;

    // Editor for Shared preferences
    SharedPreferences.Editor editor;

    // Context
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Sharedpref file name
    private static final String PREF_NAME = "AndroidHivePref";

    // All Shared Preferences Keys
    private static final String IS_LOGIN = "IsLoggedIn";

    public static final String KEY_ID = "id";

    // User name (make variable public to access from outside)
    public static final String KEY_NAME = "name";

    public static final String KEY_EMAIL = "email";

    // Email address (make variable public to access from outside)
    public static final String KEY_PASSWORD = "password";

    public static final String KEY_ADDRESS = "address";

    public static final String KEY_CITY = "city";

    public static final String KEY_POSTAL = "postal";

    public static final String KEY_PHONE = "phone";

    public static final String KEY_CELL = "cell";

    public static final String KEY_STATUS = "status";


    // Constructor
    public SessionManagement(Context context){
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }
    public void createLoginSession(String id ,String name, String email, String password ,String address,String city,String postal,String phone,String cell, String status){
        // Storing login value as TRUE
        editor.putBoolean(IS_LOGIN, true);

        editor.putString(KEY_ID, id);

        // Storing name in pref
        editor.putString(KEY_NAME, name);

        // Storing email in pref
        editor.putString(KEY_EMAIL, email);

        editor.putString(KEY_PASSWORD, password);
        editor.putString(KEY_ADDRESS, address);
        editor.putString(KEY_CITY, city);
        editor.putString(KEY_POSTAL, postal);
        editor.putString(KEY_PHONE, phone);
        editor.putString(KEY_CELL, cell);
        editor.putString(KEY_STATUS,status);
        // commit changes
        editor.commit();
    }

    public HashMap<String, String> getUserDetails(){
        HashMap<String, String> user = new HashMap<String, String>();

        user.put(KEY_ID, pref.getString(KEY_ID, null));

        // user name
        user.put(KEY_NAME, pref.getString(KEY_NAME, null));

        // user email id
        user.put(KEY_EMAIL, pref.getString(KEY_EMAIL, null));

        user.put(KEY_PASSWORD, pref.getString(KEY_PASSWORD, null));

        user.put(KEY_ADDRESS, pref.getString(KEY_ADDRESS, null));

        user.put(KEY_CITY, pref.getString(KEY_CITY, null));

        user.put(KEY_POSTAL, pref.getString(KEY_POSTAL, null));

        user.put(KEY_PHONE, pref.getString(KEY_PHONE, null));

        user.put(KEY_CELL, pref.getString(KEY_CELL, null));

        user.put(KEY_STATUS, pref.getString(KEY_STATUS, null));
        // return user
        return user;
    }

    public void checkLogin(){
        // Check login status
        if(!this.isLoggedIn()){
            // user is not logged in redirect him to Login Activity
            Intent i = new Intent(_context, LoginActivity.class);
            // Closing all the Activities
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            // Add new Flag to start new Activity
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            // Staring Login Activity
            _context.startActivity(i);
        }

    }

    public void logoutTemp()
    {
        editor.clear();
        editor.commit();
    }

    public void logoutUser(){
        // Clearing all data from Shared Preferences
        editor.clear();
        editor.commit();

        // After logout redirect user to Loing Activity
        Intent i = new Intent(_context, LoginActivity.class);
        // Closing all the Activities
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // Add new Flag to start new Activity
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // Staring Login Activity
        _context.startActivity(i);
    }

    public boolean isLoggedIn(){
        return pref.getBoolean(IS_LOGIN, false);
    }
}
