package pack.com.seventhsite.seventhsite;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class ChangePasswordActivity extends ActionBarActivity {
    //baca kategori dari web
    InputStream is;
    JSONObject jObject;
    JSONArray jArray;

    EditText etoldpassword,etpassword,etrepassword;
    Button btnchange;
    SessionManagement session;
    varGlobal vg=new varGlobal();
    String idUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        etoldpassword=(EditText)findViewById(R.id.etOldPassword);
        etpassword=(EditText)findViewById(R.id.etPassword);
        etrepassword=(EditText)findViewById(R.id.etRePassword);
        btnchange=(Button)findViewById(R.id.btnChange);
        btnchange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertbox=new AlertDialog.Builder(ChangePasswordActivity.this);
                alertbox.setMessage("Are you sure?");
                alertbox.setPositiveButton("Yes",new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface arg0,int arg1)
                    {
                        session = new SessionManagement(getApplicationContext());
                        HashMap<String,String> detailuser=session.getUserDetails();
                        idUser=detailuser.get(SessionManagement.KEY_ID);
                        if(etoldpassword.getText().toString().equals(detailuser.get(SessionManagement.KEY_PASSWORD)))
                        {
                            if(etpassword.getText().toString().equals(etrepassword.getText().toString()))
                            {
                                new sendPassword().execute("http://" + vg.ipconnection + "/service_android/updatepassword.php");
                            }
                            else
                            {
                                Toast.makeText(ChangePasswordActivity.this,"Passwords and Re Password Doesn't Match",Toast.LENGTH_LONG).show();
                            }
                        }
                        else
                        {
                            Toast.makeText(ChangePasswordActivity.this,"Your Old Password is Invalid",Toast.LENGTH_LONG).show();
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

    class sendPassword extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... url) {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(url[0]);

            try {
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("pass", etpassword.getText().toString()));
                nameValuePairs.add(new BasicNameValuePair("custid", idUser));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                HttpResponse response=httpclient.execute(httppost);

                //Toast.makeText(getBaseContext(),"Sent",Toast.LENGTH_SHORT).show();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
                Toast.makeText(ChangePasswordActivity.this, e.toString(), Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(ChangePasswordActivity.this,e.toString(),Toast.LENGTH_LONG).show();
            }
            catch (Exception e)
            {
                Toast.makeText(ChangePasswordActivity.this,e.toString(),Toast.LENGTH_LONG).show();
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
            Toast.makeText(ChangePasswordActivity.this,"Your Password Changed, Please Login Again",Toast.LENGTH_LONG).show();
            session.logoutUser();
            //finish();
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
