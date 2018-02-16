package com.example.amr.onair.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.amr.onair.R;
import com.example.amr.onair.models.Client;
import com.example.amr.onair.models.Staff;

public class ChatActivity extends AppCompatActivity {

    boolean _staff;
    Staff staff;
    Client client;
    String _Name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Intent sentIntent = getIntent();
        Bundle sentBundle = sentIntent.getExtras();
        assert sentBundle != null;
        _staff = sentBundle.getBoolean("staff");

        if (_staff) {
            staff = (Staff) sentBundle.getSerializable("sampleObject");
            assert staff != null;
            _Name = staff.getName();
        } else {
            client = (Client) sentBundle.getSerializable("sampleObject");
            assert client != null;
            _Name = client.getName();
        }

        setTitle(_Name);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
