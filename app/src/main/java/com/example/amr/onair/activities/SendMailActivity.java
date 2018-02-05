package com.example.amr.onair.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.amr.onair.R;
import com.example.amr.onair.models.Client;
import com.example.amr.onair.models.Staff;

import java.util.ArrayList;
import java.util.List;

public class SendMailActivity extends AppCompatActivity {

    EditText textSubject, textMessage;
    TextView textto;
    Button bTnSend;
    boolean _staff;
    Staff staff;
    Client client;
    String _Email;
    List<String> stockList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_mail);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        textto = findViewById(R.id.textto);
        textSubject = findViewById(R.id.textSubject);
        textMessage = findViewById(R.id.textMessage);
        bTnSend = findViewById(R.id.bTnSend);

        Intent sentIntent = getIntent();
        Bundle sentBundle = sentIntent.getExtras();
        assert sentBundle != null;
        _staff = sentBundle.getBoolean("staff");

        if (_staff) {
            staff = (Staff) sentBundle.getSerializable("sampleObject");
            assert staff != null;
            _Email = staff.getEmail();
        } else {
            client = (Client) sentBundle.getSerializable("sampleObject");
            assert client != null;
            _Email = client.getEmail();
        }
        setTitle("Send with email");
        textto.setText("To " + _Email);

        stockList = new ArrayList<>();
        stockList.add(_Email);

        bTnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (textSubject.getText().toString().isEmpty()) {
                    textSubject.setError("Please Enter Subject");
                } else if (textMessage.getText().toString().isEmpty()) {
                    textMessage.setError("Please Enter Message");
                } else {
                    sendEmail(stockList, textSubject.getText().toString(), textMessage.getText().toString());
                }
            }
        });
    }

    protected void sendEmail(List<String> stockList, String subject, String msg) {

        String[] TO = new String[stockList.size()];
        TO = stockList.toArray(TO);

        Intent emailIntent = new Intent(Intent.ACTION_SEND);

        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        emailIntent.putExtra(Intent.EXTRA_TEXT, msg);

        try {
            startActivity(Intent.createChooser(emailIntent, "Send mail..."));
            finish();
            Toast.makeText(this, "Finished sending email...", Toast.LENGTH_SHORT).show();
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(SendMailActivity.this, "There is no email client installed.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
