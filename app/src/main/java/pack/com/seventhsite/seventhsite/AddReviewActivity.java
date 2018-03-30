package pack.com.seventhsite.seventhsite;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.analytics.tracking.android.EasyTracker;

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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class AddReviewActivity extends Activity {
    SessionManagement session;
    varGlobal vg=new varGlobal();

    TextView judul,isi;
    RatingBar ratingnya;
    String iduser,iditem;
    Button submitnya;

    JSONObject jObject;
    JSONArray jArray;
    private EasyTracker easyTracker = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_review);
        easyTracker=EasyTracker.getInstance(AddReviewActivity.this);
        session = new SessionManagement(getApplicationContext());
        HashMap<String,String> detailuser=session.getUserDetails();
        Intent i=getIntent();

        judul=(TextView)findViewById(R.id.etJudul);
        isi=(TextView)findViewById(R.id.fieldReview);
        ratingnya=(RatingBar)findViewById(R.id.ratingBar);
        iduser=detailuser.get(SessionManagement.KEY_ID);
        iditem=i.getExtras().getString("iditem").toString();
        submitnya=(Button)findViewById(R.id.btnSubmitRev);




        submitnya.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (cekForm()==true) {
                    AlertDialog.Builder alertbox = new AlertDialog.Builder(AddReviewActivity.this);
                    alertbox.setMessage("Are you sure?");
                    alertbox.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface arg0, int arg1) {
                            finish();
                            new sendReview().execute("http://" + vg.ipconnection + "/service_android/insertreview.php");
                            Toast.makeText(AddReviewActivity.this, "Thank's for Your Review", Toast.LENGTH_LONG).show();
                        }
                    });
                    alertbox.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface arg0, int arg1) {
                            //new sendReview().execute("http://" + vg.ipconnection + "/service_android/insertreview.php");
                        }
                    });
                    alertbox.show();
                }
            }
        });
    }

    public boolean cekForm()
    {
        if(judul.getText().toString().equals(""))
        {
            Toast.makeText(AddReviewActivity.this, "Review Title Can't be Empty", Toast.LENGTH_LONG).show();
            return false;

        }
        else if(isi.getText().toString().equals(""))
        {
            Toast.makeText(AddReviewActivity.this, "Review Can't be Empty", Toast.LENGTH_LONG).show();
            return false;
        }
        else if(ratingnya.getRating()==0)
        {
            Toast.makeText(AddReviewActivity.this, "Rating Can't be Empty", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
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

    class sendReview extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... url) {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(url[0]);

            try {
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("CustId", iduser));
                nameValuePairs.add(new BasicNameValuePair("ProdId", iditem));
                nameValuePairs.add(new BasicNameValuePair("ReviewHeader", judul.getText().toString()));
                nameValuePairs.add(new BasicNameValuePair("Review", isi.getText().toString()));
                nameValuePairs.add(new BasicNameValuePair("Rating", ratingnya.getRating()+""));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                HttpResponse response=httpclient.execute(httppost);

                //Toast.makeText(getBaseContext(),"Sent",Toast.LENGTH_SHORT).show();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
                Toast.makeText(AddReviewActivity.this,e.toString(),Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(AddReviewActivity.this,e.toString(),Toast.LENGTH_LONG).show();
            }
            catch (Exception e)
            {
                Toast.makeText(AddReviewActivity.this,e.toString(),Toast.LENGTH_LONG).show();
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
}
