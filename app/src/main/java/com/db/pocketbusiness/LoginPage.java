package com.db.pocketbusiness;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class LoginPage extends AppCompatActivity {

    EditText emailLoginPage, passwordLoginPage;
    FirebaseAuth auth;
    ProgressDialog progressDialog;
    public static boolean loginReturn = false;
    public String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        emailLoginPage = findViewById(R.id.emailLoginPage);
        passwordLoginPage = findViewById(R.id.passwordLoginPage);

    }

    public void checkLogin(View v){

        progressDialog = new ProgressDialog(LoginPage.this);
        progressDialog.setTitle("Login");
        progressDialog.setMessage("Login in progress");
        progressDialog.show();

        auth = FirebaseAuth.getInstance();

        auth.signInWithEmailAndPassword(emailLoginPage.getText().toString(),
                passwordLoginPage.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressDialog.dismiss();
                if(task.isSuccessful()){
                    Intent intent = new Intent(LoginPage.this, Dashboard.class);
                    Log.d("firebase" , auth.getUid());
                    userID = auth.getUid();
                    intent.putExtra("userID", auth.getUid());
                    startActivity(intent);
                }
                else{
                    Toast.makeText(LoginPage.this, task.getException().getLocalizedMessage(),
                            Toast.LENGTH_LONG).show();
                }

            }
        });



    }

    @Override
    protected void onResume() {
        super.onResume();
        if(loginReturn){
            loginReturn = false;
            Intent intent = new Intent(LoginPage.this, Dashboard.class);
            intent.putExtra("userID", userID);
            startActivity(intent);
        }
    }
}