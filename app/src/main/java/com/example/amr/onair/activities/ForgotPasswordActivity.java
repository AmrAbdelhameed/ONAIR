package com.example.amr.onair.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.amr.onair.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {

    Button resetPassword;
    EditText email;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        setTitle("Reset Password");

        firebaseAuth = FirebaseAuth.getInstance();

        email = (EditText) findViewById(R.id.email);
        resetPassword = (Button) findViewById(R.id.resetPassword);

        resetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (email.getText().toString().isEmpty()) {
                    email.setError("Please Enter Email");
                } else {
                    firebaseAuth.sendPasswordResetEmail(email.getText().toString())
                            .addOnCompleteListener(ForgotPasswordActivity.this, new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(Task<Void> task) {

                                    if (!task.isSuccessful()) {
                                        Toast.makeText(ForgotPasswordActivity.this, "Failed to send reset Email!", Toast.LENGTH_LONG).show();
                                    } else {
                                        Toast.makeText(ForgotPasswordActivity.this, "We have sent you instructions to reset your password on mail!", Toast.LENGTH_LONG).show();
                                        finish();
                                    }
                                }
                            });
                }
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
