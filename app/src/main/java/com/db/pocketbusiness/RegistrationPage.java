package com.db.pocketbusiness;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.time.LocalDate;

public class RegistrationPage extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private EditText emailRegistrationPage, passwordRegistrationPage;
    private EditText nameRegistrationPage, phoneRegistrationPage;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_page);
    }

    public void registerUser(View v){

        progressDialog = new ProgressDialog(RegistrationPage.this);
        progressDialog.setTitle("Registration");
        progressDialog.setMessage("Registration in progress");
        progressDialog.show();

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance(MainActivity.DATABASELINK);
        emailRegistrationPage = findViewById(R.id.emailRegistrationPage);
        passwordRegistrationPage = findViewById(R.id.passwordRegistrationPage);
        nameRegistrationPage = findViewById(R.id.nameRegistrationPage);
        phoneRegistrationPage = findViewById(R.id.phoneRegistrationPage);

        mAuth.createUserWithEmailAndPassword(emailRegistrationPage.getText().toString(),
                passwordRegistrationPage.getText().toString())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    UserData data = new UserData(nameRegistrationPage.getText().toString(),
                            emailRegistrationPage.getText().toString(),
                            passwordRegistrationPage.getText().toString(),
                            phoneRegistrationPage.getText().toString(),false);

                    String userID = task.getResult().getUser().getUid();
                    database.getReference().child("Users").child(userID).setValue(data);

                    LocalDate date = LocalDate.now();
                    String dateVal = String.valueOf(date);
                    Log.d("displayString", String.valueOf(date));

                    String currentMonth = dateVal.substring((dateVal.indexOf("-")+1) , (dateVal.indexOf("-")+3));
                    String currentDay = dateVal.substring((dateVal.indexOf("-")+4) , dateVal.length());

                    DashboardData d = new DashboardData("0.0", "0.0", currentDay, currentMonth, false, false);
                    database.getReference().child("Data").child(userID).child("dashboard").setValue(d);

                    progressDialog.dismiss();
                    Toast.makeText(RegistrationPage.this, "Your registration is complete" , Toast.LENGTH_LONG).show();
                }
                else{
                    progressDialog.dismiss();
                    Toast.makeText(RegistrationPage.this, task.getException().getMessage() , Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}