package com.example.amr.onair.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.amr.onair.R;
import com.example.amr.onair.models.Client;
import com.example.amr.onair.models.Staff;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

public class LoginActivity extends AppCompatActivity {

    TextView register, forgotpassword;
    EditText email, password;
    Button bTnLogin;
    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;
    FirebaseDatabase database;
    Client client;
    Staff staff;
    String ID;
    ProgressDialog pdialog;
    boolean checkVisit = false;
    Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        setTitle("Login");

        gson = new Gson();

        pdialog = new ProgressDialog(LoginActivity.this);
        pdialog.setIndeterminate(true);
        pdialog.setCancelable(false);
        pdialog.setMessage("Loading. Please wait...");

        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference();
        firebaseAuth = FirebaseAuth.getInstance();

        register = findViewById(R.id.register);
        forgotpassword = findViewById(R.id.forgotpassword);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        bTnLogin = findViewById(R.id.bTnLogin);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(i);
            }
        });

        forgotpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                startActivity(i);
            }
        });

        bTnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (email.getText().toString().isEmpty()) {
                    email.setError("Please Enter Email");
                } else if (password.getText().toString().isEmpty()) {
                    password.setError("Please Enter Password");
                } else {
                    final ProgressDialog progressDialog = ProgressDialog.show(LoginActivity.this, "Please wait...", "Proccessing...", true);

                    (firebaseAuth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString()))
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
// admin.onair
                                    if (task.isSuccessful()) {
                                        if (email.getText().toString().equals("admin@onair.com")) {
                                            Toast.makeText(LoginActivity.this, "Login successfully", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                            finish();
                                        } else {
                                            ID = firebaseAuth.getCurrentUser().getUid();
                                            pdialog.show();
                                            FunClients(ID);
                                        }
                                    } else {
                                        Log.e("ERROR", task.getException().toString());
                                        Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                    progressDialog.dismiss();
                                }
                            });
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() != null) {
            if (firebaseAuth.getCurrentUser().getEmail().equals("admin@onair.com"))
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
            else
                startActivity(new Intent(LoginActivity.this, MainClientOrStaffActivity.class));
            finish();
        }
    }

    public void FunClients(final String ID) {
        databaseReference.child("users").child("clients").addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                for (DataSnapshot child : children) {
                    String clientID = child.getKey();
                    if (ID.equals(clientID)) {
                        client = child.getValue(Client.class);
                        checkVisit = true;
                    }
                }
                if (checkVisit) {
                    pdialog.dismiss();
                    String clientString = gson.toJson(client);
                    Toast.makeText(LoginActivity.this, "Login successfully", Toast.LENGTH_SHORT).show();
                    SharedPreferences sharedPreferences = LoginActivity.this.getSharedPreferences("sharedPreferencesName", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("staffCheck", false);
                    editor.putString("sampleObject", clientString);
                    editor.apply();
                    Intent i = new Intent(LoginActivity.this, MainClientOrStaffActivity.class);
                    startActivity(i);
                    finish();
                } else
                    FunStaff(ID);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void FunStaff(final String ID) {
        databaseReference.child("users").child("staff").addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                for (DataSnapshot child : children) {
                    String staffID = child.getKey();
                    if (ID.equals(staffID)) {
                        staff = child.getValue(Staff.class);
                    }
                }
                pdialog.dismiss();
                String staffString = gson.toJson(staff);
                Toast.makeText(LoginActivity.this, "Login successfully", Toast.LENGTH_SHORT).show();
                SharedPreferences sharedPreferences = LoginActivity.this.getSharedPreferences("sharedPreferencesName", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("staffCheck", true);
                editor.putString("sampleObject", staffString);
                editor.apply();
                Intent i = new Intent(LoginActivity.this, MainClientOrStaffActivity.class);
                startActivity(i);
                finish();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
