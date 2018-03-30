package pack.com.seventhsite.seventhsite;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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



public class Frag1 extends android.support.v4.app.Fragment {

    SessionManagement session;

    InputStream is;
    JSONObject jObject;
    JSONArray jArray;

    ViewPager listrecommend,listnew,listhot;
    LinearLayout layrekomen;
    varGlobal vg=new varGlobal();
    ArrayList<String> arrRecommend=new ArrayList<String>();
    ArrayList<item> arrItemRecommend=new ArrayList<item>();
    AdapterRecommend recadapter;

    ArrayList<item> arrNewItem=new ArrayList<item>();
    AdapterNewItem nadapter;

    ArrayList<item> arrHotItem=new ArrayList<item>();
    AdapterHotItem hadapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_frag1, container,false);
        listrecommend=(ViewPager)view.findViewById(R.id.ListRekomend);
        listnew=(ViewPager)view.findViewById(R.id.ListNew);
        listhot=(ViewPager)view.findViewById(R.id.ListHot);
        layrekomen=(LinearLayout)view.findViewById(R.id.layoutnyarekomendasi);
        session=new SessionManagement(((HomeActivity) getActivity()));
        HashMap<String,String> detailuser=session.getUserDetails();

        new getRecommend().execute("http://"+vg.ipconnection+"/service_android/recommend_user.php?katid=0&prodid=0&custid="+detailuser.get(SessionManagement.KEY_ID));
        //Toast.makeText(getActivity(),detailuser.get(SessionManagement.KEY_ID)+"",Toast.LENGTH_LONG).show();
        new getNewItem().execute("http://"+vg.ipconnection+"/service_android/listnewitem.php");

        new getHotItem().execute("http://"+vg.ipconnection+"/service_android/listhotitem.php");
        return view;
    }


    public Frag1() {
        // Required empty public constructor
    }
    ProgressDialog dialog;
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
            dialog= ProgressDialog.show(getActivity(), "Get Data", "Loading...");
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            //GET USER DATA
            try {
                //Toast.makeText(KategoriActivity.this, result , Toast.LENGTH_SHORT).show();
                if(result.length()==2)
                {
                    layrekomen.setVisibility(View.GONE);
                    dialog.dismiss();
                }
                else
                {
                    jObject = new JSONObject(result);
                    jArray = jObject.getJSONArray("recommend");
                    for (int i = 0; i < jArray.length(); i++) {
                        JSONObject oneObject = jArray.getJSONObject(i);
                        arrRecommend.add(oneObject.getString("barang").substring(4));
                        new getDetailforRewview().execute("http://"+vg.ipconnection+"/service_android/detailitem.php?prodid="+oneObject.getString("barang").substring(4));
                        //Toast.makeText(getActivity(),arrRecommend.size()+"",Toast.LENGTH_LONG).show();
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

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //dialog= ProgressDialog.show(getActivity(), "Get Data", "Loading...");
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

                    recadapter = new AdapterRecommend(getActivity());
                    listrecommend.setAdapter(recadapter);
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
                    Intent myIntent = new Intent(((HomeActivity) getActivity()), DetailItemActivity.class);
                    myIntent.putExtra("iditem", arrItemRecommend.get(pos).getId()+"");
                    ((HomeActivity) getActivity()).startActivity(myIntent);
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
            //dialog= ProgressDialog.show(getActivity(), "Get Data", "Loading...");
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            //dialog.dismiss();
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

    class getHotItem extends AsyncTask<String, String, String> {
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


                }
                else
                {
                    jObject = new JSONObject(result);
                    jArray = jObject.getJSONArray("item");
                    for (int i = 0; i < jArray.length(); i++) {
                        JSONObject oneObject = jArray.getJSONObject(i);
                        // Pulling items from the array
                        if(i<5) {
                            arrHotItem.add(new item(oneObject.getString("ProdName"), Integer.parseInt(oneObject.getString("Price")), oneObject.getString("ThumbnailFile"), Integer.parseInt(oneObject.getString("ProdId")), Double.parseDouble(oneObject.getString("Disc")),(int)Double.parseDouble(oneObject.getString("Rating"))));
                            //Toast.makeText(getActivity(),arrRecommend.size()+"",Toast.LENGTH_LONG).show();
                        }
                    }
                    //Toast.makeText(DetailItemActivity.this,arrRecommend.size()+"",Toast.LENGTH_LONG).show();
                    hadapter = new AdapterHotItem(getActivity());
                    listhot.setAdapter(hadapter);
                    hadapter.notifyDataSetChanged();
                }
                //dialog.dismiss();
            } catch (JSONException e) {
                e.printStackTrace();
                //dialog.dismiss();
                //Toast.makeText(DetailItemActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    class AdapterHotItem extends PagerAdapter
    {
        Context c;

        LayoutInflater inflater;
        public AdapterHotItem(Context context) {
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
            return arrHotItem.size();
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

            if(arrHotItem.get(position).getRating()==0)
            {
                imgrate.setImageResource(R.drawable.rate0);
            }
            else if(arrHotItem.get(position).getRating()==1)
            {
                imgrate.setImageResource(R.drawable.rate1);
            }
            else if(arrHotItem.get(position).getRating()==2)
            {
                imgrate.setImageResource(R.drawable.rate2);
            }
            else if(arrHotItem.get(position).getRating()==3)
            {
                imgrate.setImageResource(R.drawable.rate3);
            }
            else if(arrHotItem.get(position).getRating()==4)
            {
                imgrate.setImageResource(R.drawable.rate4);
            }
            else if(arrHotItem.get(position).getRating()==5)
            {
                imgrate.setImageResource(R.drawable.rate5);
            }

            new DownloadImage(ivrecommend).execute("http://"+vg.ipconnection+"/service_android/sitefiles/image/products/" + arrHotItem.get(position).getGambar());
            itemrecommend.setText(arrHotItem.get(position).getNamaItem());

            DecimalFormat kursIndonesia = (DecimalFormat) DecimalFormat.getCurrencyInstance();
            DecimalFormatSymbols formatRp = new DecimalFormatSymbols();

            formatRp.setCurrencySymbol("Rp. ");
            formatRp.setMonetaryDecimalSeparator(',');
            formatRp.setGroupingSeparator('.');

            kursIndonesia.setDecimalFormatSymbols(formatRp);

            hargaaslirecommend.setText(kursIndonesia.format(arrHotItem.get(position).getHargaItem()));
            hargaaslirecommend.setPaintFlags(hargaaslirecommend.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            if(arrHotItem.get(position).getDiskon()==0)
            {
                hargaaslirecommend.setText("");
            }
            hargafinalrecommend.setText(kursIndonesia.format((int)(arrNewItem.get(position).getHargaItem() * ((100 - arrHotItem.get(position).getDiskon()) / 100))));


            //buat add
            ((ViewPager) container).addView(itemView);
            //kasi event click
            final int pos=position;
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //ke detailitemactivity
                    Intent myIntent = new Intent(((HomeActivity) getActivity()), DetailItemActivity.class);
                    myIntent.putExtra("iditem", arrHotItem.get(pos).getId()+"");
                    ((HomeActivity) getActivity()).startActivity(myIntent);
                }
            });
            return itemView;
        }
    }

    class getNewItem extends AsyncTask<String, String, String> {
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


                }
                else
                {
                    jObject = new JSONObject(result);
                    jArray = jObject.getJSONArray("item");
                    for (int i = 0; i < jArray.length(); i++) {
                        JSONObject oneObject = jArray.getJSONObject(i);
                        // Pulling items from the array
                        if(i<5) {
                            arrNewItem.add(new item(oneObject.getString("ProdName"), Integer.parseInt(oneObject.getString("Price")), oneObject.getString("ThumbnailFile"), Integer.parseInt(oneObject.getString("ProdId")), Double.parseDouble(oneObject.getString("Disc")),(int)Double.parseDouble(oneObject.getString("Rating"))));
                            //Toast.makeText(getActivity(),arrRecommend.size()+"",Toast.LENGTH_LONG).show();
                        }
                    }
                    //Toast.makeText(DetailItemActivity.this,arrRecommend.size()+"",Toast.LENGTH_LONG).show();
                    nadapter = new AdapterNewItem(getActivity());
                    listnew.setAdapter(nadapter);
                    nadapter.notifyDataSetChanged();
                }
                //dialog.dismiss();
            } catch (JSONException e) {
                e.printStackTrace();
                //dialog.dismiss();
                //Toast.makeText(DetailItemActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    class AdapterNewItem extends PagerAdapter
    {
        Context c;

        LayoutInflater inflater;
        public AdapterNewItem(Context context) {
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
            return arrNewItem.size();
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

            if(arrNewItem.get(position).getRating()==0)
            {
                imgrate.setImageResource(R.drawable.rate0);
            }
            else if(arrNewItem.get(position).getRating()==1)
            {
                imgrate.setImageResource(R.drawable.rate1);
            }
            else if(arrNewItem.get(position).getRating()==2)
            {
                imgrate.setImageResource(R.drawable.rate2);
            }
            else if(arrNewItem.get(position).getRating()==3)
            {
                imgrate.setImageResource(R.drawable.rate3);
            }
            else if(arrNewItem.get(position).getRating()==4)
            {
                imgrate.setImageResource(R.drawable.rate4);
            }
            else if(arrNewItem.get(position).getRating()==5)
            {
                imgrate.setImageResource(R.drawable.rate5);
            }

            new DownloadImage(ivrecommend).execute("http://"+vg.ipconnection+"/service_android/sitefiles/image/products/" + arrNewItem.get(position).getGambar());
            itemrecommend.setText(arrNewItem.get(position).getNamaItem());

            DecimalFormat kursIndonesia = (DecimalFormat) DecimalFormat.getCurrencyInstance();
            DecimalFormatSymbols formatRp = new DecimalFormatSymbols();

            formatRp.setCurrencySymbol("Rp. ");
            formatRp.setMonetaryDecimalSeparator(',');
            formatRp.setGroupingSeparator('.');

            kursIndonesia.setDecimalFormatSymbols(formatRp);

            hargaaslirecommend.setText(kursIndonesia.format(arrNewItem.get(position).getHargaItem()));
            hargaaslirecommend.setPaintFlags(hargaaslirecommend.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            if(arrNewItem.get(position).getDiskon()==0)
            {
                hargaaslirecommend.setText("");
            }
            hargafinalrecommend.setText(kursIndonesia.format((int)(arrNewItem.get(position).getHargaItem() * ((100 - arrNewItem.get(position).getDiskon()) / 100))));


            //buat add
            ((ViewPager) container).addView(itemView);
            //kasi event click
            final int pos=position;
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //ke detailitemactivity
                    Intent myIntent = new Intent(((HomeActivity) getActivity()), DetailItemActivity.class);
                    myIntent.putExtra("iditem", arrNewItem.get(pos).getId()+"");
                    ((HomeActivity) getActivity()).startActivity(myIntent);
                }
            });
            return itemView;
        }
    }
}
