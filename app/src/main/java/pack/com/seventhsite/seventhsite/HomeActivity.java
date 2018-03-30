package pack.com.seventhsite.seventhsite;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.android.gcm.GCMRegistrar;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import static pack.com.seventhsite.seventhsite.CommonUtilities.DISPLAY_MESSAGE_ACTION;
import static pack.com.seventhsite.seventhsite.CommonUtilities.EXTRA_MESSAGE;
import static pack.com.seventhsite.seventhsite.CommonUtilities.SENDER_ID;
import static pack.com.seventhsite.seventhsite.CommonUtilities.SERVER_URL;

public class HomeActivity extends ActionBarActivity {

    InputStream is;
    // Asyntask
    AsyncTask<Void, Void, Void> mRegisterTask;

    // Alert dialog manager
    AlertDialogManager alert = new AlertDialogManager();

    // Connection detector
    ConnectionDetector cd;
    //golongan pack.com.seventhsite.seventhsite.menu//////////////////
    ListView listMenu;
    ArrayList<menu> arrMenu=new ArrayList<menu>();
    AdapterMenu madapter;
    SessionManagement session;
    ///////////////////////////////////////
    TabHost tabs;
    //drawer/////////////////////////////
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    /////////////////////////////////////////////

    private EasyTracker easyTracker = null;
    HashMap<String,String> detailuser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        easyTracker = EasyTracker.getInstance(HomeActivity.this);

        session = new SessionManagement(getApplicationContext());
        detailuser=session.getUserDetails();
        regGCM();
        //pack.com.seventhsite.seventhsite.menu////////////////////////////////////////////////////////////////////////////////
        listMenu=(ListView) findViewById(R.id.ListMenu);
        arrMenu.add(new menu("Cart"));
        arrMenu.add(new menu("Profile"));
        arrMenu.add(new menu("My Transaction"));
        arrMenu.add(new menu("Logout"));
        madapter = new AdapterMenu(this);
        listMenu.setAdapter(madapter);
        madapter.notifyDataSetChanged();
        listMenu.setClickable(true);
        ////////////////////////////////////////////////////////////////////////////////////////

        //drawer/////////////////////////////////////////////////////////////////////////////
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,R.drawable.ic_navigation_drawer, R.string.drawer_open, R.string.drawer_close);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        //////////////////////////////////////////////////////////////////////////////////////
        /*
        getSupportFragmentManager().beginTransaction()
                .add(R.id.Frametabhost, new tabhostfragment())
                .commit();
        */
        getSupportFragmentManager().beginTransaction()
                .add(R.id.tab1, new Frag1())
                .commit();

        getSupportFragmentManager().beginTransaction()
                .add(R.id.tab2, Frag2.newInstance(0))
                .commit();

        getSupportFragmentManager().beginTransaction()
                .add(R.id.tab3, new Frag3())
                .commit();


        tabs=(TabHost)findViewById(R.id.tabhost);

        tabs.setup();

        TabHost.TabSpec spec=tabs.newTabSpec("tag1");
        spec.setContent(R.id.tab1);
        spec.setIndicator("Home");
        tabs.addTab(spec);

        spec=tabs.newTabSpec("tag2");
        spec.setContent(R.id.tab2);
        spec.setIndicator("Category");
        tabs.addTab(spec);

        spec=tabs.newTabSpec("tag3");
        spec.setContent(R.id.tab3);
        spec.setIndicator("Search");
        tabs.addTab(spec);

        for(int i=0;i<tabs.getTabWidget().getChildCount();i++)
        {
            TextView tv = (TextView) tabs.getTabWidget().getChildAt(i).findViewById(android.R.id.title);
            tv.setTextColor(Color.parseColor("#5d5b46"));
        }
        setSelectedTabColor();
        tabs.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                setSelectedTabColor();
            }
        });
    }
    private void setSelectedTabColor() {
        for(int i=0;i<tabs.getTabWidget().getChildCount();i++)
        {
            tabs.getTabWidget().getChildAt(i)
                    .setBackgroundResource(R.drawable.temp);
        }
        tabs.getTabWidget().getChildAt(tabs.getCurrentTab())
                .setBackgroundResource(R.drawable.temp1);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        //session = new SessionManagement(getApplicationContext());
        if(session.isLoggedIn()==false)
        {
            finish();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        EasyTracker.getInstance(this).activityStart(this);  // Add this method.
    }

    @Override
    public void onStop() {
        super.onStop();
        EasyTracker.getInstance(this).activityStop(this);  // Add this method.
    }

    public void gotoPage(int param, String page){
        getSupportFragmentManager().beginTransaction()
                .add(R.id.tab2, Frag2.newInstance(param))
                .addToBackStack(page)
                .commit();
    }
    public void removePage(String page)
    {
        getSupportFragmentManager().popBackStack();

    }

    class AdapterMenu extends ArrayAdapter
    {
        Context c;
        public AdapterMenu(Context context) {
            super(context,R.layout.menurow,arrMenu);
            c = context;
        }
        //method dipanggil untuk setiap baris data pada ListView
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            //TODO Auto-generated method stub
            //create tampilan dari XML-nya
            LayoutInflater inflater = ((Activity)c).getLayoutInflater();
            final View row = inflater.inflate(R.layout.menurow, null);
            //tulisan nama pack.com.seventhsite.seventhsite.menu
            TextView t = (TextView)row.findViewById(R.id.textMenu);
            ImageView ivicon=(ImageView)row.findViewById(R.id.iconMenu);
            t.setText(arrMenu.get(position).getNamaMenu().toString());
            if(t.getText().toString().equals("Cart"))
            {
                ivicon.setImageResource(R.drawable.shoping_cart);
            }
            else if(t.getText().toString().equals("Profile"))
            {
                ivicon.setImageResource(R.drawable.online_support);
            }
            else if(t.getText().toString().equals("Logout"))
            {
                ivicon.setImageResource(R.drawable.logout);
            }
            else if(t.getText().toString().equals("My Transaction"))
            {
                ivicon.setImageResource(R.drawable.money_bag);
            }
            row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    TextView te;
                    te=(TextView)row.findViewById(R.id.textMenu);
                    if(te.getText().toString().equals("Cart"))
                    {
                        //finish();
                        Intent myIntent = new Intent(getApplicationContext(), CartActivity.class);
                        HomeActivity.this.startActivity(myIntent);

                    }
                    else if(te.getText().toString().equals("Profile"))
                    {
                        //finish();
                        Intent myIntent = new Intent(getApplicationContext(), ProfileActivity.class);
                        HomeActivity.this.startActivity(myIntent);
                    }
                    else if(te.getText().toString().equals("Logout"))
                    {
                        //session = new SessionManagement(getApplicationContext());
                        //finish();
                        session.logoutUser();
                    }
                    else if(te.getText().toString().equals("My Transaction"))
                    {
                        Intent myIntent = new Intent(getApplicationContext(), TransaksiActivity.class);
                        HomeActivity.this.startActivity(myIntent);
                    }
                    //Toast.makeText(getApplicationContext(), te.getText().toString(), Toast.LENGTH_LONG).show();
                }
            });
            return row;
        }
    }


    //yang berubah //////////////////////////////////////////////////////////////
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
        //Toast.makeText(getApplicationContext(), "menu klik", Toast.LENGTH_LONG).show();
    }
    /////////////////////////////////////////////////////////////////////////

    public void regGCM(){
        cd = new ConnectionDetector(getApplicationContext());
        if (!cd.isConnectingToInternet()) {
            alert.showAlertDialog(HomeActivity.this,
                    "Internet Connection Error",
                    "Please connect to working Internet connection", false);
            return;
        }

        if (SERVER_URL == null || SENDER_ID == null || SERVER_URL.length() == 0
                || SENDER_ID.length() == 0) {
            alert.showAlertDialog(HomeActivity.this, "Configuration Error!",
                    "Please set your Server URL and GCM Sender ID", false);
            return;
        }
        //GET DEVICE ID
        final TelephonyManager tm = (TelephonyManager) getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);
        final String tmDevice, tmSerial, androidId;
        tmDevice = "" + tm.getDeviceId();
        tmSerial = "" + tm.getSimSerialNumber();
        androidId = "" + android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);

        UUID deviceUuid = new UUID(androidId.hashCode(), ((long)tmDevice.hashCode() << 32) | tmSerial.hashCode());
        final String deviceId = deviceUuid.toString();
        //Toast.makeText(MainActivity.this, deviceId, Toast.LENGTH_SHORT).show();

        GCMRegistrar.checkDevice(this);
        GCMRegistrar.checkManifest(this);

        registerReceiver(mHandleMessageReceiver, new IntentFilter(
                DISPLAY_MESSAGE_ACTION));

        final String regId = GCMRegistrar.getRegistrationId(this);
        // Check if regid already presents
        if (regId.equals("")) {
            // Registration is not present, register now with GCM
            GCMRegistrar.register(this, SENDER_ID);
        } else {
            // Device is already registered on GCM
            if (GCMRegistrar.isRegisteredOnServer(this)) {
                // Skips registration.
                Log.d("req id :", "sudah ada");
            } else {
                final Context context = this;
                mRegisterTask = new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... params) {
                        // Register on server
                        new registerGCMtoServer().execute("http://"+varGlobal.ipconnection +"/service_android/gcm/register.php?regId="+regId+"&useremail="+detailuser.get(SessionManagement.KEY_EMAIL));
                        //Log.d("req id :", "belum ada");
                        Log.d("req id :",regId);
                        //headers.setText(regId);
                        //Toast.makeText(HomeActivity.this,regId,Toast.LENGTH_LONG).show();
                        //ServerUtilities.register(context,"");
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void result) {
                        mRegisterTask = null;
                    }

                };
                mRegisterTask.execute(null, null, null);
            }
        }
    }
    /**
     * Receiving push messages
     * */
    private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String newMessage = intent.getExtras().getString(EXTRA_MESSAGE);
            // Waking up mobile if it is sleeping
            WakeLocker.acquire(getApplicationContext());
            Toast.makeText(getApplicationContext(), "New Message: " + newMessage, Toast.LENGTH_LONG).show();
            WakeLocker.release();
        }
    };
    @Override
    protected void onDestroy() {
        if (mRegisterTask != null) {
            mRegisterTask.cancel(true);
        }
        try {
            unregisterReceiver(mHandleMessageReceiver);
            GCMRegistrar.onDestroy(this);
        } catch (Exception e) {
            Log.e("UnRegister Receiver Error", "> " + e.getMessage());
        }
        super.onDestroy();
    }

    class registerGCMtoServer extends AsyncTask<String, String, String>{
        @Override
        protected String doInBackground(String... url) {
            String temp="";
            try {
                Log.e("regtoserver", "masuk");
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(url[0]);
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity entity = response.getEntity();
                is = entity.getContent();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch(IllegalArgumentException e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
        }
    }
}
