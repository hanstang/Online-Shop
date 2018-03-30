package pack.com.seventhsite.seventhsite;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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
import java.util.List;
import java.util.regex.Pattern;


public class RegisterActivity extends Activity {
    //baca kategori dari web
    InputStream is;
    JSONObject jObject;
    JSONArray jArray;


    String answer="";
    Spinner spcity;
    ArrayList<String> listcity=new ArrayList<String>();
    EditText etemail,etpassword,etrepassword,etname,etaddress,etpostalcode,etphone,etcellphone;
    Button btnregister;
    varGlobal vg=new varGlobal();
    SessionManagement session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        session = new SessionManagement(getApplicationContext());
        spcity=(Spinner)findViewById(R.id.spCity);
        new getCity().execute("http://"+vg.ipconnection+"/service_android/listcity.php");
        etemail=(EditText)findViewById(R.id.etEmail);
        etpassword=(EditText)findViewById(R.id.etPassword);
        etrepassword=(EditText)findViewById(R.id.etRePassword);
        etname=(EditText)findViewById(R.id.etName);
        etaddress=(EditText)findViewById(R.id.etAddress);
        etpostalcode=(EditText)findViewById(R.id.etPostalCode);
        etphone=(EditText)findViewById(R.id.etPhone);
        etcellphone=(EditText)findViewById(R.id.etCellPhone);
        btnregister=(Button)findViewById(R.id.btnRegister);
        btnregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertbox=new AlertDialog.Builder(RegisterActivity.this);
                alertbox.setMessage("Are you sure?");
                alertbox.setPositiveButton("Yes",new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface arg0,int arg1)
                    {
                        if(cekForm()==true)
                        {
                            new cekEmail().execute("http://"+vg.ipconnection+"/service_android/cekemail.php?email="+etemail.getText().toString());
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
            Toast.makeText(RegisterActivity.this,"Email Can't be Empty",Toast.LENGTH_LONG).show();
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
            Toast.makeText(RegisterActivity.this,"Your Email is Invalid",Toast.LENGTH_LONG).show();
            return false;
        }
        if(etpassword.getText().toString().equals(""))
        {
            Toast.makeText(RegisterActivity.this,"Password Can't be Empty",Toast.LENGTH_LONG).show();
            return false;
        }
        if(!etpassword.getText().toString().equals(etrepassword.getText().toString()))
        {
            Toast.makeText(RegisterActivity.this,"Passwords and Re Password Doesn't Match",Toast.LENGTH_LONG).show();
            return false;
        }
        if(etname.getText().toString().equals(""))
        {
            Toast.makeText(RegisterActivity.this,"Name Can't be Empty",Toast.LENGTH_LONG).show();
            return false;
        }
        if(etaddress.getText().toString().equals(""))
        {
            Toast.makeText(RegisterActivity.this,"Address Can't be Empty",Toast.LENGTH_LONG).show();
            return false;
        }
        if(etpostalcode.getText().toString().equals(""))
        {
            Toast.makeText(RegisterActivity.this,"Postal Code Can't be Empty",Toast.LENGTH_LONG).show();
            return false;
        }
        if(etphone.getText().toString().equals(""))
        {
            Toast.makeText(RegisterActivity.this,"Phone Can't be Empty",Toast.LENGTH_LONG).show();
            return false;
        }
        if(etcellphone.getText().toString().equals(""))
        {
            Toast.makeText(RegisterActivity.this,"Cell Phone Can't be Empty",Toast.LENGTH_LONG).show();
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
                    jObject = new JSONObject(result);
                    jArray = jObject.getJSONArray("city");
                    int defindex=0;
                    for (int i = 0; i < jArray.length(); i++) {
                        JSONObject oneObject = jArray.getJSONObject(i);
                        listcity.add(oneObject.getString("CityName"));
                        if(oneObject.getString("CityName").equals("Surabaya"))
                        {
                            defindex=i;
                        }
                    }
                    ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(RegisterActivity.this,android.R.layout.simple_spinner_item, listcity);
                    dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spcity.setAdapter(dataAdapter);
                    spcity.setSelection(defindex);
                }
                //dialog.dismiss();
            } catch (JSONException e) {
                e.printStackTrace();
                //dialog.dismiss();
                //Toast.makeText(Frag2.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    class sendRegiter extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... url) {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(url[0]);

            try {
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("CustName", etname.getText().toString()));
                nameValuePairs.add(new BasicNameValuePair("Address", etaddress.getText().toString()));
                nameValuePairs.add(new BasicNameValuePair("CityName",String.valueOf(spcity.getSelectedItem())));
                nameValuePairs.add(new BasicNameValuePair("PostalCode", etpostalcode.getText().toString()));
                nameValuePairs.add(new BasicNameValuePair("Phone", etphone.getText().toString()));
                nameValuePairs.add(new BasicNameValuePair("CellPhone", etcellphone.getText().toString()));
                nameValuePairs.add(new BasicNameValuePair("Email", etemail.getText().toString()));
                nameValuePairs.add(new BasicNameValuePair("Pass", etpassword.getText().toString()));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                HttpResponse response=httpclient.execute(httppost);

                //Toast.makeText(getBaseContext(),"Sent",Toast.LENGTH_SHORT).show();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
                Toast.makeText(RegisterActivity.this,e.toString(),Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(RegisterActivity.this,e.toString(),Toast.LENGTH_LONG).show();
            }
            catch (Exception e)
            {
                Toast.makeText(RegisterActivity.this,e.toString(),Toast.LENGTH_LONG).show();
            }
            return "a";
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

    class cekEmail extends AsyncTask<String,String,String>
    {
        public cekEmail()
        {

        }

        @Override
        protected String doInBackground(String... url) {
            String hasil="";
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
                hasil=sb.toString();
                //toast("ini hasil "+hasil);
            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return hasil;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            answer=result;
            //toast(answer);

            if (answer.equals("false"))
            {
                Toast.makeText(RegisterActivity.this,"This Email is Already Registered",Toast.LENGTH_LONG).show();
            }
            else
            {
                new sendRegiter().execute("http://" + vg.ipconnection + "/service_android/insertuser.php");
                Toast.makeText(RegisterActivity.this,"Your Registration Successful",Toast.LENGTH_LONG).show();
                //session.createLoginSession();
                finish();
            }
        }
    }
}
