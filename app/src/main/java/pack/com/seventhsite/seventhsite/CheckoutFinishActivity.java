package pack.com.seventhsite.seventhsite;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.HashMap;


public class CheckoutFinishActivity extends ActionBarActivity {
    int idtrans,ongkir,total;
    TextView txtket;
    SessionManagement session;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout_finish);
        session = new SessionManagement(getApplicationContext());
        HashMap<String,String> detailuser=session.getUserDetails();

        Intent i=getIntent();
        idtrans=Integer.parseInt(i.getExtras().getString("idtrans").toString());
        ongkir=Integer.parseInt(i.getExtras().getString("ongkir").toString());
        total=Integer.parseInt(i.getExtras().getString("totaltrans").toString());

        if(detailuser.get(SessionManagement.KEY_STATUS).toString().equals("reseller"))
        {
            ongkir=0;
        }

        DecimalFormat kursIndonesia = (DecimalFormat) DecimalFormat.getCurrencyInstance();
        DecimalFormatSymbols formatRp = new DecimalFormatSymbols();

        formatRp.setCurrencySymbol("Rp. ");
        formatRp.setMonetaryDecimalSeparator(',');
        formatRp.setGroupingSeparator('.');

        kursIndonesia.setDecimalFormatSymbols(formatRp);

        txtket=(TextView)findViewById(R.id.txtKet);

        txtket.setText("Your Order Id is "+idtrans+"\nYour Total Transaction is "+kursIndonesia.format(total)+"\nYour Shipping Fee is "+kursIndonesia.format(ongkir)+"\n\n"+txtket.getText().toString());

    }



}
