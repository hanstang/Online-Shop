package pack.com.seventhsite.seventhsite;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.HashMap;


public class ProfileActivity extends Activity {
    TextView txtemail,txtname,txtaddress,txtcity,txtpostalcode,txtphone,txtcellphone,txtstatus;
    Button btneditprofile,btnchangepassword;
    SessionManagement session;
    varGlobal vg=new varGlobal();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        txtemail=(TextView)findViewById(R.id.txtEmail);
        txtname=(TextView)findViewById(R.id.txtName);
        txtaddress=(TextView)findViewById(R.id.txtAddress);
        txtcity=(TextView)findViewById(R.id.txtCity);
        txtpostalcode=(TextView)findViewById(R.id.txtPostalCode);
        txtphone=(TextView)findViewById(R.id.txtPhone);
        txtcellphone=(TextView)findViewById(R.id.txtCellPhone);
        txtstatus=(TextView)findViewById(R.id.txtStatus);
        btneditprofile=(Button)findViewById(R.id.btnEditProfile);
        btnchangepassword=(Button)findViewById(R.id.btnChangePassword);
        btneditprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(ProfileActivity.this, EditProfileActivity.class);
                ProfileActivity.this.startActivity(myIntent);
            }
        });
        btnchangepassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(ProfileActivity.this, ChangePasswordActivity.class);
                ProfileActivity.this.startActivity(myIntent);
            }
        });
    }

    @Override
    protected void onPostResume() {
        super.onResume();
        session = new SessionManagement(getApplicationContext());
        HashMap<String,String> detailuser=session.getUserDetails();
        txtemail.setText(detailuser.get(SessionManagement.KEY_EMAIL));
        txtname.setText(detailuser.get(SessionManagement.KEY_NAME));
        txtaddress.setText(detailuser.get(SessionManagement.KEY_ADDRESS));
        txtcity.setText(detailuser.get(SessionManagement.KEY_CITY));
        txtpostalcode.setText(detailuser.get(SessionManagement.KEY_POSTAL));
        txtphone.setText(detailuser.get(SessionManagement.KEY_PHONE));
        txtcellphone.setText(detailuser.get(SessionManagement.KEY_CELL));
        txtstatus.setText(detailuser.get(SessionManagement.KEY_STATUS));
        //session = new SessionManagement(getApplicationContext());
        if(session.isLoggedIn()==false)
        {
            finish();
        }
    }
}
