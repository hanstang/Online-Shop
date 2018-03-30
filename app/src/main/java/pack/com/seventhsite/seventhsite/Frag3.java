package pack.com.seventhsite.seventhsite;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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


public class Frag3 extends android.support.v4.app.Fragment {
    public Frag3() {
        // Required empty public constructor
    }
    //baca kategori dari web
    InputStream is;
    JSONObject jObject;
    JSONArray jArray;

    EditText etproduk,etpricemin,etpricemax;
    Spinner sppricemin,sppricemax,spkategori;

    ArrayList<String> listkategori = new ArrayList<String>();
    ArrayList<String> listidkategori=new ArrayList<String>();
    ArrayList<String> listpricemin = new ArrayList<String>();
    ArrayList<String> listpriceminvalue = new ArrayList<String>();
    ArrayList<String> listpricemax = new ArrayList<String>();
    ArrayList<String> listpricemaxvalue = new ArrayList<String>();

    Button btnsearch;
    varGlobal vg=new varGlobal();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_frag3, container,false);
        etproduk=(EditText)view.findViewById(R.id.etProduk);
        /*
        sppricemin=(Spinner)view.findViewById(R.id.spPriceMin);
        sppricemax=(Spinner)view.findViewById(R.id.spPriceMax);
        */
        etpricemin=(EditText)view.findViewById(R.id.etHaragMin);
        etpricemax=(EditText)view.findViewById(R.id.etHargaMax);

        spkategori=(Spinner)view.findViewById(R.id.spKategori);
        btnsearch=(Button)view.findViewById(R.id.btnSearch);
        listkategori.add("All");
        listidkategori.add("0");

        //priceMinimum();
        //priceMaximum();


        new getKategori().execute("http://"+vg.ipconnection+"/service_android/listkategori.php?parentid=0");
        btnsearch.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(etpricemin.getText().toString().equals("")||etpricemax.getText().toString().equals(""))
                {
                    Toast.makeText(getActivity(),"Minimum Price and Maximum Price Cannot be null",Toast.LENGTH_LONG).show();
                }
                else
                {
                    if(Integer.parseInt(etpricemin.getText().toString())>Integer.parseInt(etpricemax.getText().toString()))
                    {
                        Toast.makeText(getActivity(),"Minimum Price Cannot be Greater than Maximum Price",Toast.LENGTH_LONG).show();
                    }
                    else {
                        Intent myIntent = new Intent(((HomeActivity) getActivity()), ItemSearchActivty.class);
                        myIntent.putExtra("produk", etproduk.getText().toString());
                        myIntent.putExtra("idkat", String.valueOf(listidkategori.get((int) spkategori.getSelectedItemId()).toString()));
                        myIntent.putExtra("hrgmin", etpricemin.getText().toString());
                        myIntent.putExtra("hrgmax", etpricemax.getText().toString());
                        //myIntent.putExtra("hrgmin", String.valueOf(listpriceminvalue.get((int)sppricemin.getSelectedItemId()).toString()));
                        //myIntent.putExtra("hrgmax", String.valueOf(listpricemaxvalue.get((int)sppricemax.getSelectedItemId()).toString()));
                        ((HomeActivity) getActivity()).startActivity(myIntent);
                        //Toast.makeText(getActivity(),String.valueOf(listidkategori.get((int)spkategori.getSelectedItemId()).toString()) , Toast.LENGTH_LONG).show();
                    }
                }

            }
        });
        return view;
    }
    /*
    public void priceMinimum()
    {
        listpricemin.add("Min");
        listpriceminvalue.add("0");
        listpricemin.add("Rp.200.000,00");
        listpriceminvalue.add("200000");
        listpricemin.add("Rp.400.000,00");
        listpriceminvalue.add("400000");
        listpricemin.add("Rp.600.000,00");
        listpriceminvalue.add("600000");
        listpricemin.add("Rp.800.000,00");
        listpriceminvalue.add("800000");
        listpricemin.add("Rp.1.000.000,00");
        listpriceminvalue.add("1000000");

        ArrayAdapter<String> dataAdapterMin =new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item, listpricemin);
        dataAdapterMin.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sppricemin.setAdapter(dataAdapterMin);
    }

    public void priceMaximum()
    {
        listpricemax.add("Max");
        listpricemaxvalue.add("0");
        listpricemax.add("Rp1.500.000,00");
        listpricemaxvalue.add("1500000");
        listpricemax.add("Rp.2.000.000,00");
        listpricemaxvalue.add("2000000");
        listpricemax.add("Rp.2.500.000,00");
        listpricemaxvalue.add("2500000");
        listpricemax.add("Rp.3.000.000,00");
        listpricemaxvalue.add("3000000");

        ArrayAdapter<String> dataAdapterMax =new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item, listpricemax);
        dataAdapterMax.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sppricemax.setAdapter(dataAdapterMax);
        //sppricemax.setSelection(listpricemax.size());
    }
    */

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
            dialog= ProgressDialog.show(getActivity(), "Get User Data", "Loading...");
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

                }
                else
                {
                    jObject = new JSONObject(result);
                    jArray = jObject.getJSONArray("kategori");
                    for (int i = 0; i < jArray.length(); i++) {
                        JSONObject oneObject = jArray.getJSONObject(i);
                        listkategori.add(oneObject.getString("CatName"));
                        listidkategori.add(oneObject.getString("CatId"));
                    }
                    ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item, listkategori);
                    dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spkategori.setAdapter(dataAdapter);

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
