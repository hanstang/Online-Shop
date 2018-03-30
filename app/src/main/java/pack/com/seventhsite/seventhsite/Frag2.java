package pack.com.seventhsite.seventhsite;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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



public class Frag2 extends android.support.v4.app.Fragment {
    //baca kategori dari web
    InputStream is;
    JSONObject jObject;
    JSONArray jArray;
    ////////////////////////////////////////////////

    ListView listKat;
    ArrayList<kategori> arrKat=new ArrayList<kategori>();
    AdapterKat kadapter;
    TextView tv;
    int idparent=0;
    varGlobal vg=new varGlobal();

    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        idparent=getArguments().getInt("idnyaparent");
        View view = inflater.inflate(R.layout.fragment_frag2, container,false);
        listKat=(ListView)view.findViewById(R.id.ListKategori);
        new getKategori().execute("http://"+vg.ipconnection+"/service_android/listkategori.php?parentid="+idparent);
        return view;
    }
    public Frag2()
    {
        //idparent=0;
    }

    public static final Frag2 newInstance (int idparent) {
        //this.idparent = idparent;
        Frag2 fragment = new Frag2();
        Bundle bundle = new Bundle(1);
        bundle.putInt("idnyaparent", idparent);
        fragment.setArguments(bundle);
        return fragment ;
    }
    class AdapterKat extends ArrayAdapter
    {
        Context c;
        public AdapterKat(Context context) {
            super(context,R.layout.katrow,arrKat);
            c = context;
        }
        //method dipanggil untuk setiap baris data pada ListView
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            //TODO Auto-generated method stub
            //create tampilan dari XML-nya
            LayoutInflater inflater = ((Activity)c).getLayoutInflater();

            final View row = inflater.inflate(R.layout.katrow, null);
            //tulisan nama kategori
            TextView t = (TextView)row.findViewById(R.id.textKat);
            t.setText(arrKat.get(position).getNamaKategori().toString());
            final int idnya=arrKat.get(position).getCatId();
            row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    //TextView te;
                    //te=(TextView)row.findViewById(R.id.textKat);
                    ((HomeActivity) getActivity()).gotoPage(idnya,"page"+idnya);
                    //Toast.makeText(getActivity(), te.getText().toString(), Toast.LENGTH_LONG).show();
                }
            });
            return row;
        }
    }
    ProgressDialog dialog;
    class getKategori extends AsyncTask<String, String, String> {
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
                //Toast.makeText(getActivity(),e.toString(),Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                e.printStackTrace();
                //Toast.makeText(getActivity(),e.toString(),Toast.LENGTH_LONG).show();
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
            dialog.dismiss();
            //GET USER DATA
            try {
                //Toast.makeText(KategoriActivity.this, result , Toast.LENGTH_SHORT).show();
                if(result.length()==2)
                {
                    //move to list item and parse id kategori
                    //finish();
                    ((HomeActivity) getActivity()).removePage("coba");

                    Intent myIntent = new Intent(((HomeActivity) getActivity()), ItemActivity.class);
                    myIntent.putExtra("idkat", idparent+"");
                    ////myIntent.putExtra("namaKategori", tv.getText().toString());
                    ((HomeActivity) getActivity()).startActivity(myIntent);
                    //Toast.makeText(KategoriActivity.this, "pindah ke halaman list item dengan idkat "+idparent, Toast.LENGTH_SHORT).show();
                }
                else
                {
                    jObject = new JSONObject(result);
                    jArray = jObject.getJSONArray("kategori");
                    for (int i = 0; i < jArray.length(); i++) {
                        try {
                            JSONObject oneObject = jArray.getJSONObject(i);
                            // Pulling items from the array
                            arrKat.add(new kategori(Integer.parseInt(oneObject.getString("CatId")), oneObject.getString("CatName")));

                        } catch (JSONException e) {
                            //dialog.dismiss();
                            Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                    kadapter = new AdapterKat(getActivity());
                    listKat.setAdapter(kadapter);
                    kadapter.notifyDataSetChanged();
                    listKat.setClickable(true);
                }
                //dialog.dismiss();
            } catch (JSONException e) {
                e.printStackTrace();
                //dialog.dismiss();
                //Toast.makeText(Frag2.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        }
    }


}
