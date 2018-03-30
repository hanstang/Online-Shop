package pack.com.seventhsite.seventhsite;

import android.app.Activity;
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
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.HashMap;


public class TransaksiActivity extends Activity {
    varGlobal vg=new varGlobal();
    SessionManagement session;
    //baca kategori dari web
    InputStream is;
    JSONObject jObject;
    JSONArray jArray;

    private EasyTracker easyTracker = null;

    String iduser;
    ListView listtransaksi;

    ArrayList<transaksi> arrTransaksi=new ArrayList<transaksi>();
    AdapterTransaksi tadapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaksi);
        easyTracker = EasyTracker.getInstance(TransaksiActivity.this);
        session = new SessionManagement(getApplicationContext());
        HashMap<String,String> detailuser=session.getUserDetails();

        iduser=detailuser.get(SessionManagement.KEY_ID);

        listtransaksi=(ListView)findViewById(R.id.ListTransaksi);

        new getTransaksi().execute("http://"+vg.ipconnection+"/service_android/listtransaksi.php?custid="+iduser);


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

    class AdapterTransaksi extends ArrayAdapter
    {
        Context c;
        public AdapterTransaksi(Context context) {
            super(context,R.layout.transrow,arrTransaksi);
            c = context;
        }
        //method dipanggil untuk setiap baris data pada ListView
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            //TODO Auto-generated method stub
            //create tampilan dari XML-nya
            LayoutInflater inflater = ((Activity)c).getLayoutInflater();
            final View row = inflater.inflate(R.layout.transrow,null);
            final int pos=position;
            TextView txtidtrans=(TextView)row.findViewById(R.id.idTrans);
            TextView txttanggal=(TextView)row.findViewById(R.id.tanggalTrans);
            TextView txtharga=(TextView)row.findViewById(R.id.hargaTrans);
            TextView txtstatus=(TextView)row.findViewById(R.id.statusTrans);

            DecimalFormat kursIndonesia = (DecimalFormat) DecimalFormat.getCurrencyInstance();
            DecimalFormatSymbols formatRp = new DecimalFormatSymbols();

            formatRp.setCurrencySymbol("Rp. ");
            formatRp.setMonetaryDecimalSeparator(',');
            formatRp.setGroupingSeparator('.');

            kursIndonesia.setDecimalFormatSymbols(formatRp);

            txtidtrans.setText("Transaction Id : "+arrTransaksi.get(position).getOrderId());
            txttanggal.setText(arrTransaksi.get(position).getOrderDate());
            txtharga.setText(kursIndonesia.format(Integer.parseInt(arrTransaksi.get(position).getOrderTotal())+arrTransaksi.get(position).getOngkir()));

            if(arrTransaksi.get(position).getStatusId()==0)
            {
                txtstatus.setText("Waiting Confirmation");
            }
            else if(arrTransaksi.get(position).getStatusId()==1)
            {
                txtstatus.setText("Accepted,Waiting Your Proof");
            }
            else if(arrTransaksi.get(position).getStatusId()==2)
            {
                txtstatus.setText("Item(s) Sent, Please Wait");
            }

            row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String idtrans=arrTransaksi.get(pos).getOrderId();
                    Intent myIntent = new Intent(TransaksiActivity.this, DetailTransaksiActivity.class);
                    myIntent.putExtra("idtrans", idtrans+"");
                    myIntent.putExtra("jumtrans", arrTransaksi.get(pos).getOrderTotal());
                    myIntent.putExtra("statustrans", arrTransaksi.get(pos).getStatusId()+"");
                    myIntent.putExtra("ongkir", arrTransaksi.get(pos).getOngkir()+"");
                    myIntent.putExtra("noresi", arrTransaksi.get(pos).getNoResi());
                    TransaksiActivity.this.startActivity(myIntent);
                }
            });
            return row;
        }
    }

    class getTransaksi extends AsyncTask<String, String, String> {
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
                    //Toast.makeText(TransaksiActivity.this, "tidak ada review", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    jObject = new JSONObject(result);
                    jArray = jObject.getJSONArray("transaksi");
                    for (int i = 0; i < jArray.length(); i++) {
                        JSONObject oneObject = jArray.getJSONObject(i);
                        //int totalbiaya=Integer.parseInt(oneObject.getString("OrderTotal"))+Integer.parseInt(oneObject.getString("Ongkir"));
                        arrTransaksi.add(new transaksi(oneObject.getString("OrderId"),oneObject.getString("OrderTotal")+"",oneObject.getString("OrderDate"),Integer.parseInt(oneObject.getString("StatusId")),Integer.parseInt(oneObject.getString("Ongkir")),oneObject.getString("NoResi")));
                    }
                    tadapter = new AdapterTransaksi(TransaksiActivity.this);
                    listtransaksi.setAdapter(tadapter);
                    tadapter.notifyDataSetChanged();

                }
                //dialog.dismiss();
            } catch (JSONException e) {
                e.printStackTrace();
                //dialog.dismiss();
                //Toast.makeText(ItemActivity.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
