package com.db.pocketbusiness;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    public static String DATABASELINK = "https://pocket-business-dfa01-default-rtdb.firebaseio.com/";

    Button registrationButton, loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Write a message to the database



        registrationButton = findViewById(R.id.registrationButton);
        loginButton = findViewById(R.id.loginButton);

        registrationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                FirebaseDatabase database = FirebaseDatabase.getInstance("https://pocketbusiness-4d2fb-default-rtdb.firebaseio.com/");
//                DatabaseReference myRef = database.getReference("message");
//                myRef.setValue("Hello, World!");

                Intent registrationPageIntent = new Intent(MainActivity.this, RegistrationPage.class);
                startActivity(registrationPageIntent);

            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent loginPageIntent = new Intent(MainActivity.this, LoginPage.class);
                startActivity(loginPageIntent);
            }
        });

    }


}