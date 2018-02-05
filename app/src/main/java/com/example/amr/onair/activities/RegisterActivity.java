package com.example.amr.onair.activities;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.amr.onair.R;
import com.example.amr.onair.models.Client;
import com.example.amr.onair.models.Staff;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class RegisterActivity extends AppCompatActivity {

    private static final int PICK_IMAGE = 100;
    ArrayList<String> UsertypeArray, DepartmenttypeArray;
    String UserTypeString = "Client", DepartmenttypeString = "";
    TextView txtDate;
    ConstraintLayout Cons, Cons1, Cons2;
    EditText name, email, phone, nationalID, password, confirmpassword, hrCode;
    Button bTnReg;
    ImageView imageUpload;
    FirebaseAuth firebaseAuth;
    String imageurl = "";
    //uri to store file
    Uri filePath;
    //firebase objects
    StorageReference storageReference;
    DatabaseReference mDatabase;
    DatePickerDialog.OnDateSetListener date;
    Calendar myCalendar;
    boolean imageCheck = false;
    Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        setTitle("Registration");

        gson = new Gson();
        firebaseAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        imageUpload = findViewById(R.id.imageUpload);
        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        phone = findViewById(R.id.phone);
        nationalID = findViewById(R.id.nationalID);
        password = findViewById(R.id.password);
        confirmpassword = findViewById(R.id.confirmpassword);
        hrCode = findViewById(R.id.hrCode);
        txtDate = findViewById(R.id.txtDate);
        Cons = findViewById(R.id.Cons);
        Cons1 = findViewById(R.id.Cons1);
        Cons2 = findViewById(R.id.Cons2);
        bTnReg = findViewById(R.id.bTnReg);

        imageUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(gallery, PICK_IMAGE);
            }
        });

        myCalendar = Calendar.getInstance();

        date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateDate();
            }

        };

        txtDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(RegisterActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        UsertypeArray = new ArrayList<>();
        UsertypeArray.add("Client");
        UsertypeArray.add("Staff");

        Spinner Usertype = findViewById(R.id.Usertype);
        ArrayAdapter AdapterUsertype = new ArrayAdapter(RegisterActivity.this, android.R.layout.simple_spinner_item, UsertypeArray);
        AdapterUsertype.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Usertype.setAdapter(AdapterUsertype);

        Usertype.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {

//                ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.colorWhite));
                UserTypeString = UsertypeArray.get(position);

                if (UserTypeString.equals("Staff")) {
                    Cons.setVisibility(View.VISIBLE);
                    Cons1.setVisibility(View.VISIBLE);
                    Cons2.setVisibility(View.VISIBLE);
                } else {
                    Cons.setVisibility(View.GONE);
                    Cons1.setVisibility(View.GONE);
                    Cons2.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });

        DepartmenttypeArray = new ArrayList<>();
        DepartmenttypeArray.add("Select your department");
        DepartmenttypeArray.add("Sales");
        DepartmenttypeArray.add("IT");
        DepartmenttypeArray.add("HR");
        DepartmenttypeArray.add("PR");

        Spinner Departmenttype = findViewById(R.id.Departmenttype);
        ArrayAdapter AdapterDepartmenttype = new ArrayAdapter(RegisterActivity.this, android.R.layout.simple_spinner_item, DepartmenttypeArray);
        AdapterDepartmenttype.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Departmenttype.setAdapter(AdapterDepartmenttype);

        Departmenttype.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {

//                ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.colorWhite));
                DepartmenttypeString = DepartmenttypeArray.get(position);

                if (DepartmenttypeString.equals("Select your department")) {
                    DepartmenttypeString = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });

        bTnReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (name.getText().toString().isEmpty()) {
                    name.setError("Please Enter Name");
                } else if (email.getText().toString().isEmpty()) {
                    email.setError("Please Enter Email");
                } else if (phone.getText().toString().isEmpty()) {
                    phone.setError("Please Enter Phone");
                } else if (nationalID.getText().toString().isEmpty()) {
                    nationalID.setError("Please Enter National ID");
                } else if (password.getText().toString().isEmpty()) {
                    password.setError("Please Enter Password");
                } else if (!confirmpassword.getText().toString().equals(password.getText().toString())) {
                    confirmpassword.setError("Please Confirm Password");
                } else if (UserTypeString.equals("Staff") && hrCode.getText().toString().isEmpty()) {
                    hrCode.setError("Please Enter HR Code");
                } else if (UserTypeString.equals("Staff") && DepartmenttypeString.equals("")) {
                    Toast.makeText(RegisterActivity.this, "Please Select your department", Toast.LENGTH_SHORT).show();
                } else {
                    final ProgressDialog progressDialog = ProgressDialog.show(RegisterActivity.this, "Please wait...", "Processing...", true);
                    (firebaseAuth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString()))
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    progressDialog.dismiss();

                                    if (task.isSuccessful()) {
                                        if (imageCheck)
                                            uploadFile();
                                        else {
                                            FirebaseUser user = firebaseAuth.getCurrentUser();
                                            if (UserTypeString.equals("Client")) {
                                                Client client = new Client();

                                                assert user != null;
                                                client.setId(user.getUid());
                                                client.setName(name.getText().toString());
                                                client.setEmail(email.getText().toString());
                                                client.setPhone(phone.getText().toString());
                                                client.setNationalID(nationalID.getText().toString());
                                                client.setImagreURL(imageurl);

                                                mDatabase.child("users").child("clients").child(user.getUid()).setValue(client);

                                                String clientString = gson.toJson(client);
                                                SharedPreferences sharedPreferences = RegisterActivity.this.getSharedPreferences("sharedPreferencesName", Context.MODE_PRIVATE);
                                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                                editor.putBoolean("staffCheck", false);
                                                editor.putString("sampleObject", clientString);
                                                editor.apply();
                                            } else {
                                                Staff staff = new Staff();

                                                assert user != null;
                                                staff.setId(user.getUid());
                                                staff.setName(name.getText().toString());
                                                staff.setEmail(email.getText().toString());
                                                staff.setPhone(phone.getText().toString());
                                                staff.setNationalID(nationalID.getText().toString());
                                                staff.setImagreURL(imageurl);
                                                staff.setHrCode(hrCode.getText().toString());
                                                staff.setHiringDate(txtDate.getText().toString());
                                                staff.setDepartment(DepartmenttypeString);

                                                mDatabase.child("users").child("staff").child(user.getUid()).setValue(staff);

                                                String staffString = gson.toJson(staff);
                                                SharedPreferences sharedPreferences = RegisterActivity.this.getSharedPreferences("sharedPreferencesName", Context.MODE_PRIVATE);
                                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                                editor.putBoolean("staffCheck", true);
                                                editor.putString("sampleObject", staffString);
                                                editor.apply();
                                            }

                                            Toast.makeText(RegisterActivity.this, "Registration successful", Toast.LENGTH_LONG).show();
                                            Intent i = new Intent(RegisterActivity.this, MainClientOrStaffActivity.class);
                                            startActivity(i);
                                            finish();
                                        }
                                    } else {
                                        Log.e("ERROR", task.getException().toString());
                                        Toast.makeText(RegisterActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                }
            }
        });
    }

    public String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void uploadFile() {
        //checking if file is available
        if (filePath != null) {
            //displaying progress dialog while image is uploading
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading");
            progressDialog.show();

            //getting the storage reference
            StorageReference sRef = storageReference.child(UserTypeString + "/" + System.currentTimeMillis() + "." + getFileExtension(filePath));

            //adding the file to reference
            sRef.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    //displaying success toast
                    //Toast.makeText(getApplicationContext(), "Image Uploaded Successfully", Toast.LENGTH_LONG).show();

//                    String id = mDatabase.push().getKey();

                    FirebaseUser user = firebaseAuth.getCurrentUser();

                    imageurl = taskSnapshot.getDownloadUrl().toString();
                    //Toast.makeText(RegisterActivity.this, imageurl, Toast.LENGTH_SHORT).show();

                    if (UserTypeString.equals("Client")) {
                        Client client = new Client();

                        assert user != null;
                        client.setId(user.getUid());
                        client.setName(name.getText().toString());
                        client.setEmail(email.getText().toString());
                        client.setPhone(phone.getText().toString());
                        client.setNationalID(nationalID.getText().toString());
                        client.setImagreURL(imageurl);

                        mDatabase.child("users").child("clients").child(user.getUid()).setValue(client);

                        String clientString = gson.toJson(client);
                        SharedPreferences sharedPreferences = RegisterActivity.this.getSharedPreferences("sharedPreferencesName", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean("staffCheck", false);
                        editor.putString("sampleObject", clientString);
                        editor.apply();
                    } else {
                        Staff staff = new Staff();

                        assert user != null;
                        staff.setId(user.getUid());
                        staff.setName(name.getText().toString());
                        staff.setEmail(email.getText().toString());
                        staff.setPhone(phone.getText().toString());
                        staff.setNationalID(nationalID.getText().toString());
                        staff.setImagreURL(imageurl);
                        staff.setHrCode(hrCode.getText().toString());
                        staff.setHiringDate(txtDate.getText().toString());
                        staff.setDepartment(DepartmenttypeString);

                        mDatabase.child("users").child("staff").child(user.getUid()).setValue(staff);

                        String staffString = gson.toJson(staff);
                        SharedPreferences sharedPreferences = RegisterActivity.this.getSharedPreferences("sharedPreferencesName", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean("staffCheck", true);
                        editor.putString("sampleObject", staffString);
                        editor.apply();
                    }

                    Toast.makeText(RegisterActivity.this, "Registration successful", Toast.LENGTH_LONG).show();
                    Intent i = new Intent(RegisterActivity.this, MainClientOrStaffActivity.class);
                    startActivity(i);
                    finish();
                    progressDialog.dismiss();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    //displaying the upload progress
                    double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                    progressDialog.setMessage("Uploaded " + ((int) progress) + "% ...");
                }
            });
        } else {
            Toast.makeText(RegisterActivity.this, "Image Uploaded Error", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            imageCheck = true;
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                imageUpload.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void updateDate() {

        String myFormat = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        txtDate.setText(sdf.format(myCalendar.getTime()));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
