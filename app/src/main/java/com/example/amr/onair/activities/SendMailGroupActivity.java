package com.example.amr.onair.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
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

public class SendMailGroupActivity extends AppCompatActivity {

    Gson gson;
    List<Staff> staffList;
    List<Client> clientList;
    boolean _staff;
    String ListFilterString, _toolbar = "\n";
    EditText textSubject, textMessage;
    Button bTnSend;
    List<String> stockList;
    TextView textto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_mail_group);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        textto = findViewById(R.id.textto);
        textSubject = findViewById(R.id.textSubject);
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
                    _toolbar += staffList.get(i).getEmail();
                else
                    _toolbar += staffList.get(i).getEmail() + ", ";

                stockList.add(staffList.get(i).getEmail());
            }
        } else {
            Type type = new TypeToken<List<Client>>() {
            }.getType();
            clientList = gson.fromJson(ListFilterString, type);

            for (int i = 0; i < clientList.size(); ++i) {
                if (i == clientList.size() - 1)
                    _toolbar += clientList.get(i).getEmail();
                else
                    _toolbar += clientList.get(i).getEmail() + ", ";

                stockList.add(clientList.get(i).getEmail());
            }
        }

        setTitle("Send with email");
        textto.setText("To" + _toolbar);

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
            Toast.makeText(SendMailGroupActivity.this, "There is no email client installed.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.edit_members, menu);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
