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
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.analytics.tracking.android.EasyTracker;

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
import java.util.HashMap;


public class DetailItemActivity extends ActionBarActivity {
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
    Bitmap bmImg;
    //baca kategori dari web
    InputStream is;
    JSONObject jObject;
    JSONArray jArray;
    //////////////////////////

    ViewPager imgview,recommendview;


    ArrayList<String> arrImage=new ArrayList<String>();
    ArrayList<String> arrRecommend=new ArrayList<String>();
    ArrayList<item> arrItemRecommend=new ArrayList<item>();
    AdapterRecommend recadapter;

    AdapterImage iadapter;
    int iditem,harga,idkat;
    double diskon,ratenya;
    TextView txtdeskripsi,txtnamabarang,txtjumrev;
    ImageButton addCart,addkoment,sharing,gorev;
    varGlobal vg=new varGlobal();
    String namagambar;
    ImageView ivRating;
    LinearLayout layrekomen;
    //ShoppingCartHelper myCart=new ShoppingCartHelper();
    ArrayList<review> arrReview=new ArrayList<review>();
    private EasyTracker easyTracker = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_item);
        easyTracker=EasyTracker.getInstance(DetailItemActivity.this);

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
        layrekomen=(LinearLayout)findViewById(R.id.layoutnyarekomendasi);
        iditem=Integer.parseInt(i.getExtras().getString("iditem").toString());
        txtdeskripsi=(TextView) findViewById(R.id.txtDespendek);
        txtnamabarang=(TextView) findViewById(R.id.txtNamabarang);
        ivRating= (ImageView) findViewById(R.id.imgRating);
        txtjumrev=(TextView)findViewById(R.id.txtJumRev);
        gorev=(ImageButton)findViewById(R.id.goRev);
        addCart=(ImageButton)findViewById(R.id.addtoCart);
        addkoment=(ImageButton)findViewById(R.id.addComment);
        sharing=(ImageButton)findViewById(R.id.addShare);
        imgview=(ViewPager) findViewById(R.id.ListImage);
        recommendview=(ViewPager)findViewById(R.id.ListRekomend);

        //1 ambil data list gambar
        new getImage().execute("http://"+vg.ipconnection+"/service_android/imagelist.php?prodid="+iditem);
        //ambil data deskripsi
        //new getDetail().execute("http://"+vg.ipconnection+"/service_android/detailitem.php?prodid="+iditem);
        new updateViewItem().execute("http://"+vg.ipconnection+"/service_android/updateviewitem.php?prodid="+iditem);


        addCart.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                ShoppingCartHelper.addBarang(iditem,txtnamabarang.getText().toString(),namagambar,harga,diskon);

                Toast.makeText(DetailItemActivity.this, "add 1 item toCart, you have " + ShoppingCartHelper.getjumlahbarang() + " item(s)", Toast.LENGTH_LONG).show();
            }
        });

        addkoment.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //cek sudah pernah koment ato tidak
                new getReview().execute("http://"+vg.ipconnection+"/service_android/reviewitem.php?prodid="+iditem);
                //Toast.makeText(DetailItemActivity.this,"koment",Toast.LENGTH_LONG).show();
            }
        });
        gorev.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(txtjumrev.getText().toString().equals("0 Review(s)"))
                {
                    Toast.makeText(DetailItemActivity.this,"Sorry, No Review Right now",Toast.LENGTH_LONG).show();
                }
                else
                {
                    Intent myIntent = new Intent(DetailItemActivity.this, ReviewActivity.class);
                    myIntent.putExtra("iditem", iditem + "");
                    myIntent.putExtra("namaItem", txtnamabarang.getText().toString());
                    myIntent.putExtra("rateItem", ratenya + "");
                    //myIntent.putExtra("gambar", arrItem.get(position).getGambar());
                    //myIntent.putExtra("namaKategori", "Kategori");
                    DetailItemActivity.this.startActivity(myIntent);
                }
                //Toast.makeText(DetailItemActivity.this,"koment",Toast.LENGTH_LONG).show();
            }
        });
        sharing.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SEND);

                intent.putExtra(Intent.EXTRA_TEXT, "http://demo.seventhsite.com/product.php?pid="+iditem);
                intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Check out this site!");
                intent.setType("text/plain");
                startActivity(Intent.createChooser(intent, "Share"));
            }
        });
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
        arrRecommend.clear();
        arrItemRecommend.clear();
        new getDetail().execute("http://"+vg.ipconnection+"/service_android/detailitem.php?prodid="+iditem);
        session = new SessionManagement(getApplicationContext());
        if(session.isLoggedIn()==false)
        {
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
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

    ////////////////////////////////////////////////////////////////////////

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
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
    }

    //////////////////////////////////////////////////////////////////
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
                        DetailItemActivity.this.startActivity(myIntent);

                    }
                    else if(te.getText().toString().equals("Profile"))
                    {
                        //finish();
                        Intent myIntent = new Intent(getApplicationContext(), ProfileActivity.class);
                        DetailItemActivity.this.startActivity(myIntent);
                    }
                    else if(te.getText().toString().equals("Logout"))
                    {
                        session = new SessionManagement(getApplicationContext());
                        session.logoutUser();
                    }
                    else if(te.getText().toString().equals("My Transaction"))
                    {
                        Intent myIntent = new Intent(getApplicationContext(), TransaksiActivity.class);
                        DetailItemActivity.this.startActivity(myIntent);
                    }
                    //Toast.makeText(getApplicationContext(), te.getText().toString(), Toast.LENGTH_LONG).show();
                }
            });
            return row;
        }
    }

    class AdapterImage extends PagerAdapter
    {
        Context c;
        String nama;
        LayoutInflater inflater;
        public AdapterImage(Context context) {
            //super(context);
            c = context;
            //this.nama=nama;
        }

        @Override
        public boolean isViewFromObject(View view, Object o) {
            return view == ((LinearLayout) o);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            ((ViewPager) container).removeView((LinearLayout) object);
        }

        @Override
        public int getCount() {
            return arrImage.size();
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ImageView iv;

            inflater = (LayoutInflater) c
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View itemView = inflater.inflate(R.layout.imageitemlist, container,false);
            iv=(ImageView) itemView.findViewById(R.id.imgItemrow);

            ImageView kanan=(ImageView)itemView.findViewById(R.id.arahkanan);
            ImageView kiri=(ImageView)itemView.findViewById(R.id.arahkiri);
            if(position==getCount()-1)
            {
                kanan.setVisibility(View.INVISIBLE);
            }
            if(position==0)
            {
                kiri.setVisibility(View.INVISIBLE);
            }
            //Toast.makeText(DetailItemActivity.this,arrImage.get(position).toString(),Toast.LENGTH_LONG).show();
            //iv.setImageResource(R.drawable.ic_launcher);
            new DownloadImage(iv).execute("http://"+vg.ipconnection+"/service_android/sitefiles/image/products/" + arrImage.get(position).toString());
            ((ViewPager) container).addView(itemView);
            /*
            final int pos=position;
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(DetailItemActivity.this,arrImage.get(pos).toString(),Toast.LENGTH_LONG).show();
                }
            });*/
            return itemView;
        }
    }

    class AdapterRecommend extends PagerAdapter
    {
        Context c;

        LayoutInflater inflater;
        public AdapterRecommend(Context context) {
            //super(context);
            c = context;
            //this.nama=nama;
        }

        @Override
        public boolean isViewFromObject(View view, Object o) {
            return view == ((LinearLayout) o);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            ((ViewPager) container).removeView((LinearLayout) object);
        }

        @Override
        public int getCount() {
            return arrItemRecommend.size();
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ImageView iv;

            inflater = (LayoutInflater) c
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View itemView = inflater.inflate(R.layout.itemrowslide, container,false);
            //set tampilan
            ImageView kanan=(ImageView)itemView.findViewById(R.id.arahkanan);
            ImageView kiri=(ImageView)itemView.findViewById(R.id.arahkiri);
            if(position==getCount()-1)
            {
                kanan.setVisibility(View.INVISIBLE);
            }
            if(position==0)
            {
                kiri.setVisibility(View.INVISIBLE);
            }
            ImageView ivrecommend=(ImageView)itemView.findViewById(R.id.ImgItem);
            TextView itemrecommend=(TextView)itemView.findViewById(R.id.txtItem);
            TextView hargaaslirecommend=(TextView)itemView.findViewById(R.id.txtHargaAsli);
            TextView hargafinalrecommend=(TextView)itemView.findViewById(R.id.txtHarga);

            ImageView imgrate=(ImageView)itemView.findViewById(R.id.imgRate);

            if(arrItemRecommend.get(position).getRating()==0)
            {
                imgrate.setImageResource(R.drawable.rate0);
            }
            else if(arrItemRecommend.get(position).getRating()==1)
            {
                imgrate.setImageResource(R.drawable.rate1);
            }
            else if(arrItemRecommend.get(position).getRating()==2)
            {
                imgrate.setImageResource(R.drawable.rate2);
            }
            else if(arrItemRecommend.get(position).getRating()==3)
            {
                imgrate.setImageResource(R.drawable.rate3);
            }
            else if(arrItemRecommend.get(position).getRating()==4)
            {
                imgrate.setImageResource(R.drawable.rate4);
            }
            else if(arrItemRecommend.get(position).getRating()==5)
            {
                imgrate.setImageResource(R.drawable.rate5);
            }

            new DownloadImage(ivrecommend).execute("http://"+vg.ipconnection+"/service_android/sitefiles/image/products/" + arrItemRecommend.get(position).getGambar());
            itemrecommend.setText(arrItemRecommend.get(position).getNamaItem());

            DecimalFormat kursIndonesia = (DecimalFormat) DecimalFormat.getCurrencyInstance();
            DecimalFormatSymbols formatRp = new DecimalFormatSymbols();

            formatRp.setCurrencySymbol("Rp. ");
            formatRp.setMonetaryDecimalSeparator(',');
            formatRp.setGroupingSeparator('.');

            kursIndonesia.setDecimalFormatSymbols(formatRp);

            hargaaslirecommend.setText(kursIndonesia.format(arrItemRecommend.get(position).getHargaItem()));
            hargaaslirecommend.setPaintFlags(hargaaslirecommend.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            if(arrItemRecommend.get(position).getDiskon()==0)
            {
                hargaaslirecommend.setText("");
            }
            hargafinalrecommend.setText(kursIndonesia.format((int)(arrItemRecommend.get(position).getHargaItem() * ((100 - arrItemRecommend.get(position).getDiskon()) / 100))));


            //buat add
            ((ViewPager) container).addView(itemView);
            //kasi event click
            final int pos=position;
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //ke detailitemactivity
                    //Toast.makeText(DetailItemActivity.this,arrItemRecommend.get(pos).getId()+"",Toast.LENGTH_LONG).show();
                    Intent myIntent = new Intent(DetailItemActivity.this, DetailItemActivity.class);
                    myIntent.putExtra("iditem", arrItemRecommend.get(pos).getId()+"");
                    //myIntent.putExtra("gambar", arrItem.get(position).getGambar());
                    //myIntent.putExtra("namaKategori", "Kategori");
                    DetailItemActivity.this.startActivity(myIntent);
                }
            });
            return itemView;
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

        }
    }

    class getImage extends AsyncTask<String, String, String> {
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
        /*
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog= ProgressDialog.show(MainActivity.this, "Get User Data", "Loading...");
        }*/

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            //GET USER DATA
            try {
                //Toast.makeText(KategoriActivity.this, result , Toast.LENGTH_SHORT).show();
                if(result.length()==2)
                {
                    //move to list item and parse id kategori
                    //Toast.makeText(DetailItemActivity.this, "tidak ada gambar", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    jObject = new JSONObject(result);
                    jArray = jObject.getJSONArray("imglist");
                    for (int i = 0; i < jArray.length(); i++) {
                        try {
                            JSONObject oneObject = jArray.getJSONObject(i);
                            // Pulling items from the array
                            arrImage.add(oneObject.getString("ImageFile"));
                            //Toast.makeText(DetailItemActivity.this, oneObject.getString("ImageFile"), Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            //dialog.dismiss();
                            Toast.makeText(DetailItemActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    iadapter = new AdapterImage(DetailItemActivity.this);
                    imgview.setAdapter(iadapter);
                    iadapter.notifyDataSetChanged();
                    //imgview.setClickable(true);
                }
                //dialog.dismiss();
            } catch (JSONException e) {
                e.printStackTrace();
                //dialog.dismiss();
                //Toast.makeText(DetailItemActivity.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        }
    }
    ProgressDialog dialog;
    class getDetail extends AsyncTask<String, String, String> {
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
            dialog= ProgressDialog.show(DetailItemActivity.this, "Get Data", "Loading...");
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            //dialog.dismiss();
            //GET USER DATA
            try {
                //Toast.makeText(KategoriActivity.this, result , Toast.LENGTH_SHORT).show();
                if(result.length()==2)
                {
                    //move to list item and parse id kategori
                    //Toast.makeText(DetailItemActivity.this, "tidak ada data", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
                else
                {
                    jObject = new JSONObject(result);
                    jArray = jObject.getJSONArray("item");
                    for (int i = 0; i < jArray.length(); i++) {
                        try {
                            JSONObject oneObject = jArray.getJSONObject(i);
                            txtdeskripsi.setText(oneObject.getString("DetailDesc"));
                            namagambar=oneObject.getString("ThumbnailFile");
                            txtnamabarang.setText(oneObject.getString("ProdName"));
                            harga=Integer.parseInt(oneObject.getString("Price"));
                            diskon=Double.parseDouble(oneObject.getString("Disc"));
                            ratenya=Double.parseDouble(oneObject.getString("Rating"));
                            setRate(ratenya);
                            txtjumrev.setText(oneObject.getString("TotalReview")+" Review(s)");
                            idkat=Integer.parseInt(oneObject.getString("CatId"));
                        } catch (JSONException e) {
                            //dialog.dismiss();
                            //Toast.makeText(DetailItemActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                    //ambil data recommend
                    new getRecommend().execute("http://"+vg.ipconnection+"/service_android/recommend_test.php?prodid="+iditem+"&katid="+idkat);

                }
                //dialog.dismiss();
            } catch (JSONException e) {
                e.printStackTrace();
                //dialog.dismiss();
                //Toast.makeText(DetailItemActivity.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        }
    }

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
            //dialog= ProgressDialog.show(MainActivity.this, "Get User Data", "Loading...");
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            //GET USER DATA
            try {
                //Toast.makeText(KategoriActivity.this, result , Toast.LENGTH_SHORT).show();
                if(result.length()==2)
                {
                    //move to list item and parse id kategori
                    //Toast.makeText(ReviewActivity.this, "tidak ada review", Toast.LENGTH_SHORT).show();
                    Intent myIntent = new Intent(DetailItemActivity.this, AddReviewActivity.class);
                    myIntent.putExtra("iditem", iditem+"");
                    DetailItemActivity.this.startActivity(myIntent);
                }
                else
                {
                    boolean pernah=false;
                    HashMap<String,String> detailuser=session.getUserDetails();
                    jObject = new JSONObject(result);
                    jArray = jObject.getJSONArray("review");
                    for (int i = 0; i < jArray.length(); i++) {
                        JSONObject oneObject = jArray.getJSONObject(i);
                        if(oneObject.getString("CustName").equals(detailuser.get(SessionManagement.KEY_NAME)))
                        {
                            pernah=true;
                        }
                    }
                    if(pernah==false)
                    {
                        Intent myIntent = new Intent(DetailItemActivity.this, AddReviewActivity.class);
                        myIntent.putExtra("iditem", iditem+"");
                        DetailItemActivity.this.startActivity(myIntent);
                    }
                    else
                    {
                        Toast.makeText(DetailItemActivity.this,"Sorry ,You Already Give A Review",Toast.LENGTH_LONG).show();
                    }
                }
                //dialog.dismiss();
            } catch (JSONException e) {
                e.printStackTrace();
                //dialog.dismiss();
                //Toast.makeText(ItemActivity.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    class getRecommend extends AsyncTask<String, String, String> {
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
            //dialog= ProgressDialog.show(MainActivity.this, "Get User Data", "Loading...");
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            //GET USER DATA
            try {
                //Toast.makeText(DetailItemActivity.this, result , Toast.LENGTH_SHORT).show();
                if(result.length()==2)
                {
                    dialog.dismiss();
                    layrekomen.setVisibility(View.GONE);
                }
                else
                {
                    jObject = new JSONObject(result);
                    jArray = jObject.getJSONArray("recommend");
                    for (int i = 0; i < jArray.length(); i++) {
                        JSONObject oneObject = jArray.getJSONObject(i);
                        arrRecommend.add(oneObject.getString("barang").substring(4));
                        new getDetailforRewview().execute("http://"+vg.ipconnection+"/service_android/detailitem.php?prodid="+oneObject.getString("barang").substring(4));

                    }
                    //Toast.makeText(DetailItemActivity.this,arrRecommend.size()+"",Toast.LENGTH_LONG).show();
                }
                //dialog.dismiss();
            } catch (JSONException e) {
                e.printStackTrace();
                //dialog.dismiss();
                //Toast.makeText(DetailItemActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    class getDetailforRewview extends AsyncTask<String, String, String> {
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
        /*
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog= ProgressDialog.show(MainActivity.this, "Get User Data", "Loading...");
        }*/

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            dialog.dismiss();
            //GET USER DATA
            //Toast.makeText(DetailItemActivity.this, result , Toast.LENGTH_SHORT).show();
            try {

                if(result.length()==2)
                {
                    //move to list item and parse id kategori
                    //Toast.makeText(DetailItemActivity.this, "tidak ada data", Toast.LENGTH_SHORT).show();

                }
                else
                {
                    jObject = new JSONObject(result);
                    jArray = jObject.getJSONArray("item");
                    for (int i = 0; i < jArray.length(); i++) {
                        try {

                            JSONObject oneObject = jArray.getJSONObject(i);
                            arrItemRecommend.add(new item(oneObject.getString("ProdName"),Integer.parseInt(oneObject.getString("Price")),oneObject.getString("ThumbnailFile"),Integer.parseInt(oneObject.getString("ProdId")),Double.parseDouble(oneObject.getString("Disc")),(int)Double.parseDouble(oneObject.getString("Rating"))));
                        } catch (JSONException e) {
                            //dialog.dismiss();
                            //Toast.makeText(DetailItemActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    recadapter = new AdapterRecommend(DetailItemActivity.this);
                    recommendview.setAdapter(recadapter);
                    recadapter.notifyDataSetChanged();
                    //Toast.makeText(DetailItemActivity.this,arrItemRecommend.size()+"",Toast.LENGTH_LONG).show();
                }
                //dialog.dismiss();
            } catch (JSONException e) {
                e.printStackTrace();
                //dialog.dismiss();
                //Toast.makeText(DetailItemActivity.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        }

    }

    class updateViewItem extends AsyncTask<String, String, String> {
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
        /*
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog= ProgressDialog.show(MainActivity.this, "Get User Data", "Loading...");
        }*/

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            //GET USER DATA

        }
    }
}
