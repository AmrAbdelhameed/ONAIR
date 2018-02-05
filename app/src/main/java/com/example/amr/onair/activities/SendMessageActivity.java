package com.example.amr.onair.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.amr.onair.R;
import com.example.amr.onair.models.Client;
import com.example.amr.onair.models.Staff;

public class SendMessageActivity extends AppCompatActivity {

    EditText textMessage;
    TextView textto;
    Button bTnSend;
    boolean _staff;
    Staff staff;
    Client client;
    String _Phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_message);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        textto = findViewById(R.id.textto);
        textMessage = findViewById(R.id.textMessage);
        bTnSend = findViewById(R.id.bTnSend);

        Intent sentIntent = getIntent();
        Bundle sentBundle = sentIntent.getExtras();
        assert sentBundle != null;
        _staff = sentBundle.getBoolean("staff");

        if (_staff) {
            staff = (Staff) sentBundle.getSerializable("sampleObject");
            assert staff != null;
            _Phone = staff.getPhone();
        } else {
            client = (Client) sentBundle.getSerializable("sampleObject");
            assert client != null;
            _Phone = client.getPhone();
        }
        setTitle("Send with SMS");
        textto.setText("To : " + _Phone);

        bTnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (textMessage.getText().toString().isEmpty()) {
                    textMessage.setError("Please Enter Message");
                } else {
                    sendSMS(_Phone, textMessage.getText().toString());
                }
            }
        });
    }

    protected void sendSMS(String phone, String msg) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phone, null, msg, null, null);
            Toast.makeText(getApplicationContext(), "SMS sent.",
                    Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(),
                    "SMS failed, please try again later!",
                    Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
