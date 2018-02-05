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
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class SendMessageGroupActivity extends AppCompatActivity {

    Gson gson;
    List<Staff> staffList;
    List<Client> clientList;
    boolean _staff;
    String ListFilterString, _toolbar = "\n";
    EditText textMessage;
    Button bTnSend;
    List<String> stockList;
    TextView textto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_message_group);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        textto = findViewById(R.id.textto);
        textMessage = findViewById(R.id.textMessage);
        bTnSend = findViewById(R.id.bTnSend);

        gson = new Gson();
        stockList = new ArrayList<>();

        Intent sentIntent = getIntent();
        Bundle sentBundle = sentIntent.getExtras();
        assert sentBundle != null;
        _staff = sentBundle.getBoolean("staff");
        ListFilterString = sentBundle.getString("ListFilterString");

        if (_staff) {
            Type type = new TypeToken<List<Staff>>() {
            }.getType();
            staffList = gson.fromJson(ListFilterString, type);

            for (int i = 0; i < staffList.size(); ++i) {
                if (i == staffList.size() - 1)
                    _toolbar += staffList.get(i).getPhone();
                else
                    _toolbar += staffList.get(i).getPhone() + ", ";

                stockList.add(staffList.get(i).getPhone());
            }
        } else {
            Type type = new TypeToken<List<Client>>() {
            }.getType();
            clientList = gson.fromJson(ListFilterString, type);

            for (int i = 0; i < clientList.size(); ++i) {
                if (i == clientList.size() - 1)
                    _toolbar += clientList.get(i).getPhone();
                else
                    _toolbar += clientList.get(i).getPhone() + ", ";

                stockList.add(clientList.get(i).getPhone());
            }
        }

        setTitle("Send with SMS");
        textto.setText("To" + _toolbar);

        bTnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (textMessage.getText().toString().isEmpty()) {
                    textMessage.setError("Please Enter Message");
                } else {
                    String msg = textMessage.getText().toString();
                    for (int i = 0; i < stockList.size(); ++i)
                        sendSMS(stockList.get(i), msg);
                }
            }
        });
    }

    protected void sendSMS(String phone, String msg) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phone, null, msg, null, null);
            Toast.makeText(getApplicationContext(), phone + " SMS sent.",
                    Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(),
                    phone + " SMS failed, please try again later!",
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
