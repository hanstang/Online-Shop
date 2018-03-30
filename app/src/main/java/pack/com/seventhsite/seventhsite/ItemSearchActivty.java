package pack.com.seventhsite.seventhsite;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
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
import com.google.analytics.tracking.android.MapBuilder;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;


public class ItemSearchActivty extends ActionBarActivity {
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
    //baca bitmap image
    Bitmap bmImg;
    ImageView img;
    ////////////////////////////////////////////////

    ListView listItem;
    ArrayList<item> arrItem=new ArrayList<item>();
    AdapterItem iadapter;
    int idkat;
    String namaproduk,hrgmax,hrgmin;
    TextView tv;
    varGlobal vg=new varGlobal();

    private EasyTracker easyTracker = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_search_activty);
        easyTracker=EasyTracker.getInstance(ItemSearchActivty.this);

        //menu////////////////////////////////////////////////////////////////////////////////
        listMenu=(ListView) findViewById(R.id.ListMenu);
        arrMenu.add(new menu("Cart"));
        arrMenu.add(new menu("Profile"));
        arrMenu.add(new menu("Transaction"));
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
        idkat=Integer.parseInt(i.getExtras().getString("idkat").toString());
        namaproduk=i.getExtras().getString("produk").toString();
        hrgmin=i.getExtras().getString("hrgmin").toString();
        hrgmax=i.getExtras().getString("hrgmax").toString();
        easyTracker.send(MapBuilder.createEvent("Lihat Item" + idkat, "onCreate", idkat + "", null).build());


        listItem=(ListView) findViewById(R.id.ListItem);
        new getItem().execute("http://"+vg.ipconnection+"/service_android/listitemsearch.php?katid="+idkat+"&min="+hrgmin+"&max="+hrgmax+"&produk="+namaproduk.toLowerCase());

        //Toast.makeText(ItemSearchActivty.this,hrgmin+hrgmax+namaproduk,Toast.LENGTH_LONG).show();
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
                        ItemSearchActivty.this.startActivity(myIntent);

                    }
                    else if(te.getText().toString().equals("Profile"))
                    {
                        //finish();
                        Intent myIntent = new Intent(getApplicationContext(), ProfileActivity.class);
                        ItemSearchActivty.this.startActivity(myIntent);
                    }
                    else if(te.getText().toString().equals("Logout"))
                    {
                        session = new SessionManagement(getApplicationContext());
                        session.logoutUser();
                    }
                    else if(te.getText().toString().equals("Transaction"))
                    {
                        Intent myIntent = new Intent(getApplicationContext(), TransaksiActivity.class);
                        ItemSearchActivty.this.startActivity(myIntent);
                    }
                    //Toast.makeText(getApplicationContext(), te.getText().toString(), Toast.LENGTH_LONG).show();
                }
            });
            return row;
        }
    }

    class AdapterItem extends ArrayAdapter
    {
        Context c;
        public AdapterItem(Context context) {
            super(context,R.layout.itemrow,arrItem);
            c = context;
        }
        //method dipanggil untuk setiap baris data pada ListView
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            //TODO Auto-generated method stub

            LayoutInflater inflater = ((Activity)c).getLayoutInflater();
            final View row = inflater.inflate(R.layout.itemrow, null);
            final int iditem=arrItem.get(position).getId();
            TextView t = (TextView)row.findViewById(R.id.txtItem);
            TextView hrgBelumDiskon=(TextView)row.findViewById(R.id.txtHargaAsli);
            t.setText(arrItem.get(position).getNamaItem().toString());
            TextView hrg=(TextView)row.findViewById(R.id.txtHarga);

            ImageView imgrate=(ImageView)row.findViewById(R.id.imgRate);

            if(arrItem.get(position).getRating()==0)
            {
                imgrate.setImageResource(R.drawable.rate0);
            }
            else if(arrItem.get(position).getRating()==1)
            {
                imgrate.setImageResource(R.drawable.rate1);
            }
            else if(arrItem.get(position).getRating()==2)
            {
                imgrate.setImageResource(R.drawable.rate2);
            }
            else if(arrItem.get(position).getRating()==3)
            {
                imgrate.setImageResource(R.drawable.rate3);
            }
            else if(arrItem.get(position).getRating()==4)
            {
                imgrate.setImageResource(R.drawable.rate4);
            }
            else if(arrItem.get(position).getRating()==5)
            {
                imgrate.setImageResource(R.drawable.rate5);
            }


            DecimalFormat kursIndonesia = (DecimalFormat) DecimalFormat.getCurrencyInstance();
            DecimalFormatSymbols formatRp = new DecimalFormatSymbols();

            formatRp.setCurrencySymbol("Rp. ");
            formatRp.setMonetaryDecimalSeparator(',');
            formatRp.setGroupingSeparator('.');

            kursIndonesia.setDecimalFormatSymbols(formatRp);

            hrgBelumDiskon.setText(kursIndonesia.format(arrItem.get(position).getHargaItem()));
            hrgBelumDiskon.setPaintFlags(hrgBelumDiskon.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            if(arrItem.get(position).getDiskon()==0)
            {
                hrgBelumDiskon.setText("");
            }
            hrg.setText(kursIndonesia.format((int)(arrItem.get(position).getHargaItem() * ((100 - arrItem.get(position).getDiskon()) / 100))));

            ImageView image=(ImageView)row.findViewById(R.id.ImgItem);


            if(arrItem.get(position).getGambar().equals(""))
            {
                //image.setImageResource(R.drawable.no_image);
            }
            else
            {
                new DownloadImage(image).execute("http://"+vg.ipconnection+"/service_android/sitefiles/image/products/" + arrItem.get(position).getGambar());
            }
            row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub

                    Intent myIntent = new Intent(ItemSearchActivty.this, DetailItemActivity.class);
                    myIntent.putExtra("iditem", iditem+"");
                    //myIntent.putExtra("gambar", arrItem.get(position).getGambar());
                    //myIntent.putExtra("namaKategori", "Kategori");
                    ItemSearchActivty.this.startActivity(myIntent);

                    //Toast.makeText(ItemActivity.this, iditem + "", Toast.LENGTH_SHORT).show();
                }
            });
            return row;
        }
    }

    ProgressDialog dialog;
    class getItem extends AsyncTask<String, String, String> {
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
            dialog= ProgressDialog.show(ItemSearchActivty.this, "Get User Data", "Loading...");
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            //GET USER DATA
            try {
                //Toast.makeText(ItemSearchActivty.this, result , Toast.LENGTH_SHORT).show();
                if(result.length()==11)
                {
                    //move to list item and parse id kategori
                    finish();
                    Toast.makeText(ItemSearchActivty.this, "Item Not Found", Toast.LENGTH_LONG).show();
                }
                else
                {
                    jObject = new JSONObject(result);
                    jArray = jObject.getJSONArray("item");
                    //Toast.makeText(ItemSearchActivty.this, jArray.length()+"", Toast.LENGTH_SHORT).show();
                    /*if(jArray.length()==0)
                    {
                        finish();
                        Toast.makeText(ItemSearchActivty.this, "Item Not Found", Toast.LENGTH_LONG).show();
                    }*/
                    for (int i = 0; i < jArray.length(); i++) {
                        try {
                            JSONObject oneObject = jArray.getJSONObject(i);
                            // Pulling items from the array
                            arrItem.add(new item(oneObject.getString("ProdName"),Integer.parseInt(oneObject.getString("Price")),oneObject.getString("ThumbnailFile"),Integer.parseInt(oneObject.getString("ProdId")),Double.parseDouble(oneObject.getString("Disc")),(int)Double.parseDouble(oneObject.getString("Rating"))));
                        } catch (JSONException e) {
                            //dialog.dismiss();
                            Toast.makeText(ItemSearchActivty.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                    iadapter = new AdapterItem(ItemSearchActivty.this);
                    listItem.setAdapter(iadapter);
                    iadapter.notifyDataSetChanged();
                    listItem.setClickable(true);
                }
                //dialog.dismiss();
            } catch (JSONException e) {
                e.printStackTrace();
                //Toast.makeText(ItemSearchActivty.this, "tidak ada item", Toast.LENGTH_SHORT).show();
                //dialog.dismiss();
                //Toast.makeText(ItemActivity.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        }
    }


    class DownloadImage extends AsyncTask<String,Void,Bitmap>
    {
        //ImageView myiv;
        //ProgressBar mypb;
        private final WeakReference<ImageView> imageViewReference;
        public DownloadImage(ImageView iv)
        {
            //myiv = iv;
            imageViewReference=new WeakReference<ImageView>(iv);
            //mypb = pb;
        }
        @Override
        protected Bitmap doInBackground(String...url) {
            // TODO Auto-generated method stub

            final AndroidHttpClient client = AndroidHttpClient.newInstance("Android");
            final HttpGet getRequest = new HttpGet(url[0]);
            try {
                HttpResponse response = client.execute(getRequest);
                final int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode != HttpStatus.SC_OK) {
                    Log.w("ImageDownloader", "Error " + statusCode
                            + " while retrieving bitmap from " + url);
                    return null;
                }

                final HttpEntity entity = response.getEntity();
                if (entity != null) {
                    InputStream inputStream = null;
                    try {
                        inputStream = entity.getContent();
                        final Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        return bitmap;
                    } finally {
                        if (inputStream != null) {
                            inputStream.close();
                        }
                        entity.consumeContent();
                    }
                }
            } catch (Exception e) {
                // Could provide a more explicit error message for IOException or
                // IllegalStateException
                getRequest.abort();
                Log.w("ImageDownloader", "Error while retrieving bitmap from " + url);
            } finally {
                if (client != null) {
                    client.close();
                }
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected void onPostExecute(Bitmap result) {
            // TODO Auto-generated method stub
            //super.onPostExecute(result);
            if (imageViewReference != null && result != null) {
                final ImageView imageView = imageViewReference.get();
                if (imageView != null) {
                    imageView.setImageBitmap(result);
                }
            }
            dialog.dismiss();
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
