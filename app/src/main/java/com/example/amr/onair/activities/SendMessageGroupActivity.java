package com.example.amr.onair.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
    String ListFilterString;
    EditText textMessage;
    Button bTnSend;
    List<String> stockListPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_message_group);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        textMessage = findViewById(R.id.textMessage);
        bTnSend = findViewById(R.id.bTnSend);

        gson = new Gson();
        stockListPhone = new ArrayList<>();

        Intent sentIntent = getIntent();
        Bundle sentBundle = sentIntent.getExtras();
        assert sentBundle != null;
        _staff = sentBundle.getBoolean("staff");
        ListFilterString = sentBundle.getString("ListFilterString");

        if (_staff) {
            Type type = new TypeToken<List<Staff>>() {
            }.getType();
            staffList = gson.fromJson(ListFilterString, type);

            for (int i = 0; i < staffList.size(); ++i)
                stockListPhone.add(staffList.get(i).getPhone());

        } else {
            Type type = new TypeToken<List<Client>>() {
            }.getType();
            clientList = gson.fromJson(ListFilterString, type);

            for (int i = 0; i < clientList.size(); ++i)
                stockListPhone.add(clientList.get(i).getPhone());

        }

        setTitle("Send with SMS");

        bTnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (textMessage.getText().toString().isEmpty()) {
                    textMessage.setError("Please Enter Message");
                } else {
                    if (stockListPhone.size() > 0) {
                        String msg = textMessage.getText().toString();
                        for (int i = 0; i < stockListPhone.size(); ++i)
                            sendSMS(stockListPhone.get(i), msg);
                    } else
                        Toast.makeText(SendMessageGroupActivity.this, "Not Available", Toast.LENGTH_SHORT).show();
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.edit_members, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_edit) {
            String stockListString = gson.toJson(stockListPhone);
            Intent i2 = new Intent(SendMessageGroupActivity.this, DeleteActivity.class);
            Bundle b2 = new Bundle();
            b2.putString("stockListString", stockListString);
            i2.putExtras(b2);
            startActivityForResult(i2, 1);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // check if the request code is same as what is passed  here it is 2
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                Bundle b = data.getExtras();
                assert b != null;
                String stockListString = b.getString("stockListString");

                stockListPhone.clear();
                Type type = new TypeToken<List<String>>() {
                }.getType();
                stockListPhone = gson.fromJson(stockListString, type);
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
