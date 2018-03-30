package pack.com.seventhsite.seventhsite;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;


public class CheckoutStepActivity extends ActionBarActivity {

    SessionManagement session;
    varGlobal vg=new varGlobal();
    //baca kategori dari web
    InputStream is;
    JSONObject jObject;
    JSONArray jArray;

    ArrayList<Product> arrproductcart=new ArrayList<Product>();
    int jumlahBiayaVariable=0;

    Spinner spcity;
    ArrayList<String> listcity=new ArrayList<String>();
    ArrayList<String> listongkir=new ArrayList<String>();

    EditText etname,etaddress,etpostalcode,etphone,etcellphone;
    TextView etemail,txtket;
    Button btnfinish;
    int ongkirnya;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout_step);
        arrproductcart=ShoppingCartHelper.getCart();
        session = new SessionManagement(getApplicationContext());

        if(arrproductcart.size()!=0)
        {
            for(int i=0;i<arrproductcart.size();i++)
            {
                int tempSubtotal=0;
                tempSubtotal=(int)((arrproductcart.get(i).getHarga()*arrproductcart.get(i).getJumlah())*((100-arrproductcart.get(i).getDiskon())/100));
                jumlahBiayaVariable+=tempSubtotal;
            }
        }

        spcity=(Spinner)findViewById(R.id.spCity);
        new getCity().execute("http://"+vg.ipconnection+"/service_android/listcity.php");
        etemail=(TextView)findViewById(R.id.etEmail);
        txtket=(TextView)findViewById(R.id.txtKet);
        etname=(EditText)findViewById(R.id.etName);
        etaddress=(EditText)findViewById(R.id.etAddress);
        etpostalcode=(EditText)findViewById(R.id.etPostalCode);
        etphone=(EditText)findViewById(R.id.etPhone);
        etcellphone=(EditText)findViewById(R.id.etCellPhone);
        btnfinish=(Button)findViewById(R.id.btnFinish);

        HashMap<String,String> detailuser=session.getUserDetails();
        etemail.setText(detailuser.get(SessionManagement.KEY_EMAIL));
        etname.setText(detailuser.get(SessionManagement.KEY_NAME));
        etaddress.setText(detailuser.get(SessionManagement.KEY_ADDRESS));
        etpostalcode.setText(detailuser.get(SessionManagement.KEY_POSTAL));
        etphone.setText(detailuser.get(SessionManagement.KEY_PHONE));
        etcellphone.setText(detailuser.get(SessionManagement.KEY_CELL));

        if(detailuser.get(SessionManagement.KEY_STATUS).toString().equals("member"))
        {
            txtket.setText("");
        }

        btnfinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertbox=new AlertDialog.Builder(CheckoutStepActivity.this);
                alertbox.setMessage("Are you sure?");
                alertbox.setPositiveButton("Yes",new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface arg0,int arg1)
                    {
                        if(cekForm()==true)
                        {
                            new sendTransaksi().execute("http://" + vg.ipconnection + "/service_android/inserttransaksi.php");
                        }
                    }
                });
                alertbox.setNegativeButton("No",new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface arg0,int arg1)
                    {
                        //new sendReview().execute("http://" + vg.ipconnection + "/service_android/insertreview.php");
                    }
                });
                alertbox.show();
            }
        });
    }


    public boolean cekForm()
    {
        if(etemail.getText().toString().equals(""))
        {
            Toast.makeText(CheckoutStepActivity.this, "Email Can't be Empty", Toast.LENGTH_LONG).show();
            return false;
        }
        Pattern EMAIL_ADDRESS_PATTERN = Pattern.compile(
                "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                        "\\@" +
                        "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                        "(" +
                        "\\." +
                        "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                        ")+"
        );
        if(EMAIL_ADDRESS_PATTERN.matcher(etemail.getText().toString()).matches()==false)
        {
            Toast.makeText(CheckoutStepActivity.this,"Your Email is Invalid",Toast.LENGTH_LONG).show();
            return false;
        }

        if(etname.getText().toString().equals(""))
        {
            Toast.makeText(CheckoutStepActivity.this,"Name Can't be Empty",Toast.LENGTH_LONG).show();
            return false;
        }
        if(etaddress.getText().toString().equals(""))
        {
            Toast.makeText(CheckoutStepActivity.this,"Address Can't be Empty",Toast.LENGTH_LONG).show();
            return false;
        }
        if(etpostalcode.getText().toString().equals(""))
        {
            Toast.makeText(CheckoutStepActivity.this,"Postal Code Can't be Empty",Toast.LENGTH_LONG).show();
            return false;
        }
        if(etphone.getText().toString().equals(""))
        {
            Toast.makeText(CheckoutStepActivity.this,"Phone Can't be Empty",Toast.LENGTH_LONG).show();
            return false;
        }
        if(etcellphone.getText().toString().equals(""))
        {
            Toast.makeText(CheckoutStepActivity.this,"Cell Phone Can't be Empty",Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    class getCity extends AsyncTask<String, String, String> {
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

                }
                else
                {
                    HashMap<String,String> detailuser=session.getUserDetails();
                    int posisiCity=0;
                    jObject = new JSONObject(result);
                    jArray = jObject.getJSONArray("city");
                    for (int i = 0; i < jArray.length(); i++) {
                        JSONObject oneObject = jArray.getJSONObject(i);
                        listcity.add(oneObject.getString("CityName"));
                        listongkir.add(oneObject.getString("Ongkir"));
                        if(detailuser.get(SessionManagement.KEY_CITY).equals(oneObject.getString("CityName")))
                        {
                            posisiCity=i;
                        }
                    }
                    ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(CheckoutStepActivity.this,android.R.layout.simple_spinner_item, listcity);
                    dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spcity.setAdapter(dataAdapter);
                    spcity.setSelection(posisiCity);

                }
                //dialog.dismiss();
            } catch (JSONException e) {
                e.printStackTrace();
                //dialog.dismiss();
                //Toast.makeText(Frag2.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    class sendTransaksi extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... url) {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(url[0]);
            String hasil="";
            try {
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

                if(arrproductcart.size()!=0)
                {
                    for(int i=0;i<arrproductcart.size();i++)
                    {
                        nameValuePairs.add(new BasicNameValuePair("ProdId[]",arrproductcart.get(i).getIdbarang()+""));
                        nameValuePairs.add(new BasicNameValuePair("Qty[]",arrproductcart.get(i).getJumlah()+""));
                        nameValuePairs.add(new BasicNameValuePair("Disc[]",arrproductcart.get(i).getDiskon()+""));
                        nameValuePairs.add(new BasicNameValuePair("Price[]",arrproductcart.get(i).getHarga()+""));
                    }
                }
                HashMap<String,String> detailuser=session.getUserDetails();
                nameValuePairs.add(new BasicNameValuePair("CustId",detailuser.get(SessionManagement.KEY_ID)));
                nameValuePairs.add(new BasicNameValuePair("SendToName",etname.getText().toString()));
                nameValuePairs.add(new BasicNameValuePair("SendToAddress",etaddress.getText().toString()));
                nameValuePairs.add(new BasicNameValuePair("SendToCity",String.valueOf(spcity.getSelectedItem())));
                nameValuePairs.add(new BasicNameValuePair("SendToPostalCode",etpostalcode.getText().toString()));
                nameValuePairs.add(new BasicNameValuePair("SendToPhone",etphone.getText().toString()));
                nameValuePairs.add(new BasicNameValuePair("SendToCellPhone",etcellphone.getText().toString()));
                nameValuePairs.add(new BasicNameValuePair("OrderTotal",jumlahBiayaVariable+""));
                if(detailuser.get(SessionManagement.KEY_STATUS).toString().equals("reseller"))
                {
                    nameValuePairs.add(new BasicNameValuePair("Ongkir","0"));
                }
                else
                {
                    nameValuePairs.add(new BasicNameValuePair("Ongkir",listongkir.get(spcity.getSelectedItemPosition()).toString()+""));
                }


                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                HttpResponse response=httpclient.execute(httppost);
                HttpEntity entity = response.getEntity();
                is = entity.getContent();

                BufferedReader reader = new BufferedReader(new InputStreamReader(is,"iso-8859-1"),8);
                StringBuilder sb = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                is.close();
                hasil=sb.toString();
                //Toast.makeText(getBaseContext(),"Sent",Toast.LENGTH_SHORT).show();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
                Toast.makeText(CheckoutStepActivity.this, e.toString(), Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(CheckoutStepActivity.this,e.toString(),Toast.LENGTH_LONG).show();
            }
            catch (Exception e)
            {
                Toast.makeText(CheckoutStepActivity.this,e.toString(),Toast.LENGTH_LONG).show();
            }
            return hasil;
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
            //entar baca sukses atau tidak
            String idtrans=result;

            Intent myIntent = new Intent(CheckoutStepActivity.this, CheckoutFinishActivity.class);
            myIntent.putExtra("idtrans", idtrans+"");
            myIntent.putExtra("totaltrans", jumlahBiayaVariable+"");
            myIntent.putExtra("ongkir", listongkir.get(spcity.getSelectedItemPosition()).toString()+"");
            CheckoutStepActivity.this.startActivity(myIntent);
            finish();
            ShoppingCartHelper.clearCart();
            Toast.makeText(CheckoutStepActivity.this, "Your Transaction Successful", Toast.LENGTH_LONG).show();
            try {

                jObject = new JSONObject(result);
                //jArray = jObject.getJSONArray("item");
                String code=jObject.getString("code");
                if(code.equals("1"))
                {
                    Toast.makeText(getBaseContext(), "Inserted Successfully",
                            Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(getBaseContext(), "Sorry, Try Again",
                            Toast.LENGTH_LONG).show();
                }



            } catch (JSONException e) {
                e.printStackTrace();
                //dialog.dismiss();
                //Toast.makeText(DetailItemActivity.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
