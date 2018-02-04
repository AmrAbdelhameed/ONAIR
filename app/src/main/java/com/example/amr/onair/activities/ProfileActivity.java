package com.example.amr.onair.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.example.amr.onair.R;
import com.example.amr.onair.models.Client;
import com.example.amr.onair.models.Staff;
import com.squareup.picasso.Picasso;

public class ProfileActivity extends AppCompatActivity {

    ConstraintLayout Cons, Cons1;
    de.hdodenhof.circleimageview.CircleImageView image;
    TextView name, txtPhone, txtEmail, txtNationalID, txtDate, department;
    String _id, _name, _image, _email, _nationalID, _phone, _hireDate = "", _department = "";
    boolean _staff;
    Staff staff;
    Client client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Cons = findViewById(R.id.Cons);
        Cons1 = findViewById(R.id.Cons1);
        image = findViewById(R.id.image);
        name = findViewById(R.id.name);
        txtPhone = findViewById(R.id.txtPhone);
        txtEmail = findViewById(R.id.txtEmail);
        txtNationalID = findViewById(R.id.txtNationalID);
        txtDate = findViewById(R.id.txtDate);
        department = findViewById(R.id.department);

        Intent sentIntent = getIntent();
        Bundle sentBundle = sentIntent.getExtras();
        assert sentBundle != null;
        _staff = sentBundle.getBoolean("staff");

        if (_staff) {
            staff = (Staff) sentBundle.getSerializable("sampleObject");
            assert staff != null;
            _id = staff.getId();
            _image = staff.getImagreURL();
            _name = staff.getName();
            _email = staff.getEmail();
            _nationalID = staff.getNationalID();
            _phone = staff.getPhone();
            _hireDate = staff.getHiringDate();
            _department = staff.getDepartment();

            Cons.setVisibility(View.VISIBLE);
            Cons1.setVisibility(View.VISIBLE);
            txtDate.setText("Date of Hiring : " + _hireDate);
            department.setText("Department of " + _department);
        } else {
            client = (Client) sentBundle.getSerializable("sampleObject");
            assert client != null;
            _id = client.getId();
            _image = client.getImagreURL();
            _name = client.getName();
            _email = client.getEmail();
            _nationalID = client.getNationalID();
            _phone = client.getPhone();
        }

        if (!"".equals(_image))
            Picasso.with(ProfileActivity.this).load(_image).placeholder(R.drawable.ic_account_circle_black_48dp).into(image);

        setTitle(_name);
        name.setText(_name);
        txtPhone.setText(_phone);
        txtEmail.setText(_email);
        txtNationalID.setText("National ID : " + _nationalID);

        txtPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", _phone, null)));
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
