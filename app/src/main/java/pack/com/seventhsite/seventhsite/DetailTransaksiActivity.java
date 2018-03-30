package pack.com.seventhsite.seventhsite;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
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
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;


public class DetailTransaksiActivity extends Activity {
    SessionManagement session;
    varGlobal vg=new varGlobal();

    /////////////////////////////////////////////
    Bitmap bmImg;
    //baca kategori dari web
    InputStream is;
    JSONObject jObject;
    JSONArray jArray;
    //////////////////////////
    private EasyTracker easyTracker = null;
    //private static final int SELECT_PICTURE = 1;
    private Bitmap bitmap;

    int idTrans,jumlah,statustrans,ongkir;
    TextView txtjumlah,txtongkir,txtresi;
    String pathnya,noresi;

    ArrayList<item> arrItemTrans=new ArrayList<item>();
    AdapterItem iadapter;

    Button btnadd,btnupload;
    ImageView imgproof;
    ListView listitem;

    ProgressDialog dialog = null;
    int serverResponseCode = 0;
    String upLoadServerUri = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_transaksi);
        easyTracker = EasyTracker.getInstance(DetailTransaksiActivity.this);

        Intent i=getIntent();
        idTrans=Integer.parseInt(i.getExtras().getString("idtrans").toString());
        jumlah=Integer.parseInt(i.getExtras().getString("jumtrans").toString());
        ongkir=Integer.parseInt(i.getExtras().getString("ongkir").toString()+"");
        statustrans=Integer.parseInt(i.getExtras().getString("statustrans").toString());
        noresi=i.getExtras().getString("noresi").toString();
        txtjumlah=(TextView)findViewById(R.id.txtJumlah);
        txtongkir=(TextView)findViewById(R.id.txtOngkir);
        txtresi=(TextView)findViewById(R.id.txtNoResi);
        listitem=(ListView)findViewById(R.id.listItem);
        DecimalFormat kursIndonesia = (DecimalFormat) DecimalFormat.getCurrencyInstance();
        DecimalFormatSymbols formatRp = new DecimalFormatSymbols();

        formatRp.setCurrencySymbol("Rp. ");
        formatRp.setMonetaryDecimalSeparator(',');
        formatRp.setGroupingSeparator('.');

        kursIndonesia.setDecimalFormatSymbols(formatRp);

        txtjumlah.setText("Total : "+kursIndonesia.format(jumlah));
        txtongkir.setText("Shipping Fee : "+kursIndonesia.format(ongkir));
        txtresi.setText("No Resi : "+noresi);
        btnadd=(Button)findViewById(R.id.btnAdd);
        btnupload=(Button)findViewById(R.id.btnUpload);
        imgproof=(ImageView)findViewById(R.id.imgProof);
        new DownloadImage(imgproof).execute("http://"+vg.ipconnection+"/service_android/transaksi/" + idTrans +".jpg");
        btnadd.setVisibility(View.INVISIBLE);
        btnupload.setVisibility(View.INVISIBLE);




        if(statustrans==1)
        {
            btnadd.setVisibility(View.VISIBLE);
            //btnupload.setVisibility(View.VISIBLE);
        }

        btnadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
                //Toast.makeText(DetailTransaksiActivity.this,pathnya,Toast.LENGTH_LONG).show();
            }
        });
        btnupload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(imgproof.getDrawable()==null)
                {
                    Toast.makeText(DetailTransaksiActivity.this,"Image Cann't be Null",Toast.LENGTH_LONG).show();
                }
                else
                {
                    //new ImageUploadTask().execute();
                    dialog = ProgressDialog.show(DetailTransaksiActivity.this, "", "Uploading file...", true);
                    upLoadServerUri = "http://"+vg.ipconnection+"/service_android/uploadbukti.php?transid="+idTrans;
                    new Thread(new Runnable() {
                        public void run() {
                            runOnUiThread(new Runnable() {
                                public void run() {}
                            });
                            uploadFile(pathnya);
                        }
                    }).start();
                }
            }
        });

        new getDetailTransaksi().execute("http://"+vg.ipconnection+"/service_android/detailtransaksi.php?transid="+idTrans);
    }

    private void selectImage() {

        final CharSequence[] options = { "Choose from Gallery","Cancel" };

        AlertDialog.Builder builder = new AlertDialog.Builder(DetailTransaksiActivity.this);
        builder.setTitle("Add Photo!");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Take Photo"))
                {

                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    File f = new File(android.os.Environment.getExternalStorageDirectory(), "temp.jpg");
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                    startActivityForResult(intent, 1);
                }
                else if (options[item].equals("Choose from Gallery"))
                {

                    Intent intent = new   Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, 2);

                }
                else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 2) {

                Uri selectedImage = data.getData();
                String[] filePath = { MediaStore.Images.Media.DATA };
                Cursor c = getContentResolver().query(selectedImage,filePath, null, null, null);
                c.moveToFirst();
                int columnIndex = c.getColumnIndex(filePath[0]);
                String picturePath = c.getString(columnIndex);
                c.close();
                //Bitmap thumbnail = (BitmapFactory.decodeFile(picturePath));
                bitmap=(BitmapFactory.decodeFile(picturePath));
                Log.w("path of image from gallery......******************.........", picturePath + "");
                imgproof.setImageBitmap(bitmap);


                pathnya=picturePath;
                btnupload.setVisibility(View.VISIBLE);
            }

        }
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

    class getDetailTransaksi extends AsyncTask<String, String, String> {
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
                        //string gambar di isi qty
                        arrItemTrans.add(new item(oneObject.getString("ProdName"),Integer.parseInt(oneObject.getString("Price")),oneObject.getString("Qty"),1,Double.parseDouble(oneObject.getString("Disc")),1));
                    }
                    iadapter = new AdapterItem(DetailTransaksiActivity.this);
                    listitem.setAdapter(iadapter);
                    iadapter.notifyDataSetChanged();
                    //listitem.setClickable(true);
                }
                //dialog.dismiss();
            } catch (JSONException e) {
                e.printStackTrace();
                //dialog.dismiss();
                //Toast.makeText(ItemActivity.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
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

    class AdapterItem extends ArrayAdapter {
        Context c;

        public AdapterItem(Context context) {
            super(context, R.layout.itemtransrow, arrItemTrans);
            c = context;
        }

        //method dipanggil untuk setiap baris data pada ListView
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            //TODO Auto-generated method stub

            LayoutInflater inflater = ((Activity) c).getLayoutInflater();
            final View row = inflater.inflate(R.layout.itemtransrow, null);

            TextView txtproduk = (TextView) row.findViewById(R.id.txtProduk);
            TextView txtjumlah = (TextView) row.findViewById(R.id.txtJumlah);
            TextView txtharga = (TextView) row.findViewById(R.id.txtHarga);

            txtproduk.setText(arrItemTrans.get(position).getNamaItem());
            //string gambar di isi qty
            txtjumlah.setText(arrItemTrans.get(position).getGambar()+" Item(s)");


            DecimalFormat kursIndonesia = (DecimalFormat) DecimalFormat.getCurrencyInstance();
            DecimalFormatSymbols formatRp = new DecimalFormatSymbols();

            formatRp.setCurrencySymbol("Rp. ");
            formatRp.setMonetaryDecimalSeparator(',');
            formatRp.setGroupingSeparator('.');

            kursIndonesia.setDecimalFormatSymbols(formatRp);


            txtharga.setText(kursIndonesia.format((int) (arrItemTrans.get(position).getHargaItem() * ((100 - arrItemTrans.get(position).getDiskon()) / 100))));


            return row;
        }
    }

    public int uploadFile(String sourceFileUri) {


        String fileName = sourceFileUri;

        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        File sourceFile = new File(sourceFileUri);

        if (!sourceFile.isFile()) {

            dialog.dismiss();

            /*Log.e("uploadFile", "Source File not exist :"
                    +uploadFilePath + "" + uploadFileName);*/

            runOnUiThread(new Runnable() {
                public void run() {
                    /*messageText.setText("Source File not exist :"
                            +uploadFilePath + "" + uploadFileName);*/
                }
            });

            return 0;

        }
        else
        {
            try {

                // open a URL connection to the Servlet
                FileInputStream fileInputStream = new FileInputStream(sourceFile);
                URL url = new URL(upLoadServerUri);

                // Open a HTTP  connection to  the URL
                conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true); // Allow Inputs
                conn.setDoOutput(true); // Allow Outputs
                conn.setUseCaches(false); // Don't use a Cached Copy
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                conn.setRequestProperty("uploaded_file", fileName);

                dos = new DataOutputStream(conn.getOutputStream());

                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename="
                        + fileName + "" + lineEnd);

                dos.writeBytes(lineEnd);

                // create a buffer of  maximum size
                bytesAvailable = fileInputStream.available();

                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];

                // read file and write it into form...
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                while (bytesRead > 0) {

                    dos.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                }

                // send multipart form data necesssary after file data...
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                // Responses from the server (code and message)
                serverResponseCode = conn.getResponseCode();
                String serverResponseMessage = conn.getResponseMessage();

                Log.i("uploadFile", "HTTP Response is : "
                        + serverResponseMessage + ": " + serverResponseCode);

                if(serverResponseCode == 200){

                    runOnUiThread(new Runnable() {
                        public void run() {

                            String msg = "File Upload Completed.\n\n See uploaded file here : \n\n"
                                    +" http://www.androidexample.com/media/uploads/"
                                    +"coba";

                            //messageText.setText(msg);
                            Toast.makeText(DetailTransaksiActivity.this, "File Upload Complete.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                //close the streams //
                fileInputStream.close();
                dos.flush();
                dos.close();

            } catch (MalformedURLException ex) {

                dialog.dismiss();
                ex.printStackTrace();

                runOnUiThread(new Runnable() {
                    public void run() {
                        /*messageText.setText("MalformedURLException Exception : check script url.");*/
                        Toast.makeText(DetailTransaksiActivity.this, "MalformedURLException",
                                Toast.LENGTH_SHORT).show();
                    }
                });

                Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
            } catch (Exception e) {

                dialog.dismiss();
                e.printStackTrace();

                runOnUiThread(new Runnable() {
                    public void run() {
                        /*messageText.setText("Got Exception : see logcat ");*/
                        Toast.makeText(DetailTransaksiActivity.this, "Got Exception : see logcat ",
                                Toast.LENGTH_SHORT).show();
                    }
                });
                Log.e("Upload file to server Exception", "Exception : "
                        + e.getMessage(), e);
            }
            dialog.dismiss();
            return serverResponseCode;

        } // End else block
    }
}

