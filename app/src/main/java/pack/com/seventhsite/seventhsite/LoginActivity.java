package pack.com.seventhsite.seventhsite;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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


public class LoginActivity extends Activity {

    EditText email,password;
    TextView txtregister;
    Button login;
    SessionManagement session;


    //asynctask
    String answer="";


    //baca Item dari web
    InputStream is;
    JSONObject jObject;
    JSONArray jArray;

    varGlobal vg=new varGlobal();

    private EasyTracker easyTracker = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        easyTracker = EasyTracker.getInstance(LoginActivity.this);

        session = new SessionManagement(getApplicationContext());
        if(session.isLoggedIn()==true)
        {
            finish();
            Intent myIntent = new Intent(LoginActivity.this, HomeActivity.class);
            //myIntent.putExtra("key", value); //Optional parameters
            LoginActivity.this.startActivity(myIntent);
            //Toast.makeText(getApplicationContext(), "Auto Login", Toast.LENGTH_LONG).show();
        }
        email=(EditText)findViewById(R.id.txtEmail);
        password=(EditText)findViewById(R.id.txtPassword);
        login=(Button)findViewById(R.id.btnLogin);
        login.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //event click
                //cek login
                //toast("test");
                new cekLogin().execute("http://"+vg.ipconnection+"/service_android/ceklogin.php?email="+email.getText().toString()+"&password="+password.getText().toString());

            }
        });
        txtregister=(TextView)findViewById(R.id.txtRegister);
        txtregister.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent myIntent = new Intent(LoginActivity.this, RegisterActivity.class);
                LoginActivity.this.startActivity(myIntent);
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

    private void toast(String text){
        Toast.makeText(this, text, Toast.LENGTH_LONG).show();
    }
    ProgressDialog dialog;
    class cekLogin extends AsyncTask<String,String,String>
    {
        public cekLogin()
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
        protected void onPreExecute() {
            super.onPreExecute();
            dialog= ProgressDialog.show(LoginActivity.this, "Get Data", "Loading...");
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            answer=result;
            //toast(answer);

            if (answer.equals("false"))
            {
                dialog.dismiss();
                toast("Your Email or Password is Invalid");
            }
            else
            {

                //isi session
                new getUser().execute("http://"+vg.ipconnection+"/service_android/datalogin.php?email="+email.getText().toString());
                ////////////////////////////////////////////////////////////

            }
        }
    }

    class getUser extends AsyncTask<String, String, String> {
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
            dialog.dismiss();
            //GET USER DATA
            try {
                //Toast.makeText(KategoriActivity.this, result , Toast.LENGTH_SHORT).show();

                jObject = new JSONObject(result);
                jArray = jObject.getJSONArray("user");
                for (int i = 0; i < jArray.length(); i++) {

                    JSONObject oneObject = jArray.getJSONObject(i);
                    // Pulling items from the array
                    session.createLoginSession(oneObject.getString("CustId"),oneObject.getString("CustName"),email.getText().toString(), password.getText().toString(),oneObject.getString("Address"),oneObject.getString("CityName"),oneObject.getString("PostalCode"),oneObject.getString("Phone"),oneObject.getString("CellPhone"),oneObject.getString("Status"));

                }
                toast("Login Success");
                finish();
                Intent myIntent = new Intent(LoginActivity.this, HomeActivity.class);
                LoginActivity.this.startActivity(myIntent);
                //dialog.dismiss();
            } catch (JSONException e) {
                e.printStackTrace();
                toast(e.toString());
                //dialog.dismiss();
                //Toast.makeText(ItemActivity.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(0,1,0,"change ip");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==1)
        {
            Intent myIntent = new Intent(LoginActivity.this, SettingActivity.class);
            LoginActivity.this.startActivity(myIntent);
        }
        return true;
    }
}
