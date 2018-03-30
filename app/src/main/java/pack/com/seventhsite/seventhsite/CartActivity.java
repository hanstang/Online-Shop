package pack.com.seventhsite.seventhsite;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.Bundle;
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

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;


public class CartActivity extends Activity {

    SessionManagement session;
    //baca kategori dari web
    InputStream is;
    JSONObject jObject;
    JSONArray jArray;

    ArrayList<Product> arrproductcart=new ArrayList<Product>();
    AdapterCart cadapter;
    ListView listCart;

    TextView jumlahBiaya;
    Button btncheckout;
    int jumlahBiayaVariable=0;
    varGlobal vg=new varGlobal();
    String cart="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        session = new SessionManagement(getApplicationContext());


        arrproductcart=ShoppingCartHelper.getCart();
        listCart=(ListView) findViewById(R.id.ListCart);
        cadapter = new AdapterCart(this);
        listCart.setAdapter(cadapter);
        cadapter.notifyDataSetChanged();

        jumlahBiaya=(TextView)findViewById(R.id.TotalHarga);

        changeJumlah();
        btncheckout=(Button)findViewById(R.id.btnCheckout);
        btncheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(arrproductcart.size()==0)
                {
                    Toast.makeText(CartActivity.this,"Sorry Your Cart is Empty",Toast.LENGTH_LONG).show();
                }
                else {
                    AlertDialog.Builder alertbox=new AlertDialog.Builder(CartActivity.this);
                    alertbox.setMessage("Are you sure?");
                    alertbox.setPositiveButton("Yes",new DialogInterface.OnClickListener(){
                        public void onClick(DialogInterface arg0,int arg1)
                        {

                            //new sendTransaksi().execute("http://" + vg.ipconnection + "/service_android/inserttransaksi.php");
                            finish();
                            Intent myIntent = new Intent(CartActivity.this, CheckoutStepActivity.class);
                            CartActivity.this.startActivity(myIntent);
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
            }
        });
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        session = new SessionManagement(getApplicationContext());
        if(session.isLoggedIn()==false)
        {
            finish();
        }
    }


    //procedure buat jumlah
    public void changeJumlah()
    {
        DecimalFormat kursIndonesia = (DecimalFormat) DecimalFormat.getCurrencyInstance();
        DecimalFormatSymbols formatRp = new DecimalFormatSymbols();

        formatRp.setCurrencySymbol("Rp. ");
        formatRp.setMonetaryDecimalSeparator(',');
        formatRp.setGroupingSeparator('.');

        kursIndonesia.setDecimalFormatSymbols(formatRp);

        jumlahBiayaVariable=0;
        //itung total
        if(arrproductcart.size()!=0)
        {
            for(int i=0;i<arrproductcart.size();i++)
            {
                int tempSubtotal=0;
                tempSubtotal=(int)((arrproductcart.get(i).getHarga()*arrproductcart.get(i).getJumlah())*((100-arrproductcart.get(i).getDiskon())/100));
                jumlahBiayaVariable+=tempSubtotal;
            }
        }
        else
        {
            jumlahBiayaVariable=0;
        }
        jumlahBiaya.setText(kursIndonesia.format(jumlahBiayaVariable)+"");
    }


    class AdapterCart extends ArrayAdapter
    {
        Context c;
        public AdapterCart(Context context) {
            super(context,R.layout.cartrow,arrproductcart);
            c = context;
        }
        //method dipanggil untuk setiap baris data pada ListView
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            //TODO Auto-generated method stub
            //create tampilan dari XML-nya
            LayoutInflater inflater = ((Activity)c).getLayoutInflater();
            final View row = inflater.inflate(R.layout.cartrow, null);
            final int nomorow=position;
            //tulisan nama menu
            TextView namaproduk = (TextView)row.findViewById(R.id.txtNamaProdukCart);
            TextView hargaawal=(TextView)row.findViewById(R.id.txtHargaAwal);
            TextView hargafinal=(TextView)row.findViewById(R.id.txtHargaFinal);
            TextView jumlahbrng=(TextView)row.findViewById(R.id.txtJumlah);
            final TextView subtotal=(TextView)row.findViewById(R.id.txtSubtotal);
            ImageView gambarprocart=(ImageView)row.findViewById(R.id.imgProdukCart);
            Button tombolRemove=(Button)row.findViewById(R.id.btnRemove);
            Button tombolPlus=(Button)row.findViewById(R.id.buttonPlus);
            Button tombolMinus=(Button)row.findViewById(R.id.buttonMinus);

            new DownloadImage(gambarprocart).execute("http://"+vg.ipconnection+"/service_android/sitefiles/image/products/" + arrproductcart.get(position).getGambar());
            namaproduk.setText(arrproductcart.get(position).getNamaBarang());
            jumlahbrng.setText(arrproductcart.get(position).getJumlah()+"");
            //Toast.makeText(CartActivity.this,hargaawal.getText().toString(),Toast.LENGTH_LONG).show();

            final DecimalFormat kursIndonesia = (DecimalFormat) DecimalFormat.getCurrencyInstance();
            DecimalFormatSymbols formatRp = new DecimalFormatSymbols();

            formatRp.setCurrencySymbol("Rp. ");
            formatRp.setMonetaryDecimalSeparator(',');
            formatRp.setGroupingSeparator('.');

            kursIndonesia.setDecimalFormatSymbols(formatRp);

            hargaawal.setText(kursIndonesia.format(arrproductcart.get(position).getHarga()));
            hargaawal.setPaintFlags(hargaawal.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            if(arrproductcart.get(position).getDiskon()==0)
            {
                hargaawal.setText("");
            }
            hargafinal.setText(kursIndonesia.format((int)(arrproductcart.get(position).getHarga() * ((100 - arrproductcart.get(position).getDiskon()) / 100))));
            int hargasubtotal=(int)(arrproductcart.get(position).getHarga() * ((100 - arrproductcart.get(position).getDiskon()) / 100));
            hargasubtotal=arrproductcart.get(position).getJumlah()*hargasubtotal;
            subtotal.setText("SubTotal : "+kursIndonesia.format(hargasubtotal));
            tombolRemove.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    arrproductcart.remove(nomorow);
                    cadapter.notifyDataSetChanged();
                    changeJumlah();
                }
            });

            tombolPlus.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    arrproductcart.get(nomorow).setJumlah(arrproductcart.get(nomorow).getJumlah()+1);
                    cadapter.notifyDataSetChanged();
                    changeJumlah();
                    int hargasubtotal=(int)(arrproductcart.get(nomorow).getHarga() * ((100 - arrproductcart.get(nomorow).getDiskon()) / 100));
                    hargasubtotal=arrproductcart.get(nomorow).getJumlah()*hargasubtotal;
                    subtotal.setText("SubTotal : "+kursIndonesia.format(hargasubtotal));
                }
            });

            tombolMinus.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (arrproductcart.get(nomorow).getJumlah()==1)
                    {
                        //arrproductcart.remove(nomorow);
                        Toast.makeText(CartActivity.this,"Quantity Can not be Zero",Toast.LENGTH_LONG).show();
                    }
                    else
                    {
                        arrproductcart.get(nomorow).setJumlah(arrproductcart.get(nomorow).getJumlah()-1);
                    }
                    cadapter.notifyDataSetChanged();
                    changeJumlah();
                    int hargasubtotal=(int)(arrproductcart.get(nomorow).getHarga() * ((100 - arrproductcart.get(nomorow).getDiskon()) / 100));
                    hargasubtotal=arrproductcart.get(nomorow).getJumlah()*hargasubtotal;
                    subtotal.setText("SubTotal : "+kursIndonesia.format(hargasubtotal));
                }
            });

            return row;
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


}
