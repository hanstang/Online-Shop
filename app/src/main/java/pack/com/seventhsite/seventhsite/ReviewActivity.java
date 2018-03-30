package pack.com.seventhsite.seventhsite;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.analytics.tracking.android.EasyTracker;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.util.ArrayList;


public class ReviewActivity extends ActionBarActivity {
    //golongan menu//////////////////
    ListView listMenu;
    ArrayList<menu> arrMenu=new ArrayList<menu>();
    AdapterMenu madapter;
    SessionManagement session;
    ///////////////////////////////////////

    //drawer/////////////////////////////
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    /////////////////////////////////////////////

    //baca Item dari web
    InputStream is;
    JSONObject jObject;
    JSONArray jArray;

    int iditem;
    String namaItem;

    double ratenya;

    TextView txtnamabarang;
    ImageView ivRating;
    ListView listreview;

    ArrayList<review> arrReview=new ArrayList<review>();
    AdapterReview radapter;
    varGlobal vg=new varGlobal();
    private EasyTracker easyTracker = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);
        easyTracker = EasyTracker.getInstance(ReviewActivity.this);
        //menu////////////////////////////////////////////////////////////////////////////////
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
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.drawable.ic_navigation_drawer, R.string.drawer_open, R.string.drawer_close);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        //////////////////////////////////////////////////////////////////////////////////////
        Intent i=getIntent();
        iditem=Integer.parseInt(i.getExtras().getString("iditem").toString());
        namaItem=i.getExtras().getString("namaItem").toString();
        ratenya=Double.parseDouble(i.getExtras().getString("rateItem").toString());

        txtnamabarang=(TextView)findViewById(R.id.TxtNamabrng);
        txtnamabarang.setText(namaItem);

        ivRating=(ImageView)findViewById(R.id.imageRate);
        setRate(ratenya);

        listreview=(ListView)findViewById(R.id.ListReview);
        new getReview().execute("http://"+vg.ipconnection+"/service_android/reviewitem.php?prodid="+iditem);
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

    @Override
    protected void onPostResume() {
        super.onPostResume();
        session = new SessionManagement(getApplicationContext());
        if(session.isLoggedIn()==false)
        {
            finish();
        }
    }

    public void setRate(Double ratingnya)
    {
        int pembulatan = (int) Math.round(ratingnya);
        if (pembulatan==0)
        {
            ivRating.setImageResource(R.drawable.rate0);
        }
        else if(pembulatan==1)
        {
            ivRating.setImageResource(R.drawable.rate1);
        }
        else if(pembulatan==2)
        {
            ivRating.setImageResource(R.drawable.rate2);
        }
        else if(pembulatan==3)
        {
            ivRating.setImageResource(R.drawable.rate3);
        }
        else if(pembulatan==4)
        {
            ivRating.setImageResource(R.drawable.rate4);
        }
        else if(pembulatan==5)
        {
            ivRating.setImageResource(R.drawable.rate5);
        }
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
            //tulisan nama menu
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
                        ReviewActivity.this.startActivity(myIntent);

                    }
                    else if(te.getText().toString().equals("Profile"))
                    {
                        //finish();
                        Intent myIntent = new Intent(getApplicationContext(), ProfileActivity.class);
                        ReviewActivity.this.startActivity(myIntent);
                    }
                    else if(te.getText().toString().equals("Logout"))
                    {
                        session = new SessionManagement(getApplicationContext());
                        session.logoutUser();
                    }
                    else if(te.getText().toString().equals("My Transaction"))
                    {
                        Intent myIntent = new Intent(getApplicationContext(), TransaksiActivity.class);
                        ReviewActivity.this.startActivity(myIntent);
                    }
                    //Toast.makeText(getApplicationContext(), te.getText().toString(), Toast.LENGTH_LONG).show();
                }
            });
            return row;
        }
    }

    class AdapterReview extends ArrayAdapter
    {
        Context c;
        public AdapterReview(Context context) {
            super(context,R.layout.reviewrow,arrReview);
            c = context;
        }
        //method dipanggil untuk setiap baris data pada ListView
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            //TODO Auto-generated method stub
            //create tampilan dari XML-nya
            LayoutInflater inflater = ((Activity)c).getLayoutInflater();
            final View row = inflater.inflate(R.layout.reviewrow, null);

            TextView headernya=(TextView)row.findViewById(R.id.txtHeader);
            TextView isinya=(TextView)row.findViewById(R.id.txtIsi);
            TextView usernya=(TextView)row.findViewById(R.id.txtUser);

            headernya.setText(arrReview.get(position).getHeader());
            isinya.setText(arrReview.get(position).getIsi());
            usernya.setText("by "+arrReview.get(position).getUser());

            return row;
        }
    }
    ProgressDialog dialog;
    class getReview extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... url) {
            String temp="";
            try {
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(url[0]);
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity entity = response.getEntity();
                is = entity.getContent();

                BufferedReader reader = new BufferedReader(new InputStreamReader(is,"iso-8859-1"),8);
                StringBuilder sb = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                is.close();
                temp=sb.toString();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return temp;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog= ProgressDialog.show(ReviewActivity.this, "Get Data", "Loading...");
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            dialog.dismiss();
            //GET USER DATA
            try {
                //Toast.makeText(KategoriActivity.this, result , Toast.LENGTH_SHORT).show();
                if(result.length()==2)
                {
                    //move to list item and parse id kategori
                    Toast.makeText(ReviewActivity.this, "tidak ada review", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    jObject = new JSONObject(result);
                    jArray = jObject.getJSONArray("review");
                    for (int i = 0; i < jArray.length(); i++) {
                        JSONObject oneObject = jArray.getJSONObject(i);
                        arrReview.add(new review(oneObject.getString("ReviewHeader"),oneObject.getString("Review"),oneObject.getString("CustName")));
                    }
                    radapter = new AdapterReview(ReviewActivity.this);
                    listreview.setAdapter(radapter);
                    radapter.notifyDataSetChanged();

                }
                //dialog.dismiss();
            } catch (JSONException e) {
                e.printStackTrace();
                //dialog.dismiss();
                //Toast.makeText(ItemActivity.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
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
}
