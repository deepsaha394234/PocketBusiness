package com.db.pocketbusiness;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;

import java.time.LocalDate;


public class CustomerDuesPage extends AppCompatActivity {

    private FirebaseDatabase database;
    private String userID;
    private LinearLayout layout;
    private double monthlyIncome;
    private double dailyIncome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_dues_page);

        Intent intent = getIntent();
        userID = intent.getStringExtra("userID");

        layout = findViewById(R.id.billGenerateListContainer);

        database = FirebaseDatabase.getInstance(MainActivity.DATABASELINK);

        database.getReference().child("Data").child(userID).child("dues").get()
                .addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if (!task.getResult().toString().contains("null")){
                            databaseReader(String.valueOf(task.getResult().getValue()));
                        }
                    }
                });

        database.getReference().child("Data").child(userID).child("dashboard").child("monthly_income").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                getMonthlyValue(String.valueOf(task.getResult().getValue()));
            }
        });
        database.getReference().child("Data").child(userID).child("dashboard").child("daily_income").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                getDailyValue(String.valueOf(task.getResult().getValue()));
            }
        });
    }

    protected void getMonthlyValue(String data){
        monthlyIncome = Double.parseDouble(data);
    }

    protected void getDailyValue(String data){
        dailyIncome = Double.parseDouble(data);
    }

    protected void databaseReader(String data){
        Log.d("DATABASE READER", data);

        String customerName, amount, phone;
        int count = 0;

        while((data.indexOf('}') != 0)){
            if(count == 0)
                customerName = data.substring((data.indexOf('{'))+1, (data.indexOf('=')));
            else
                customerName = data.substring(2, (data.indexOf('=')));

            data = data.substring((data.indexOf('='))+2, data.length());

            amount = data.substring((data.indexOf("=")+1), data.indexOf(','));
            data = data.substring((data.indexOf(',')+2), data.length());

            phone = data.substring((data.indexOf("=")+1), data.indexOf('}'));
            data = data.substring((data.indexOf('}')+1), data.length());

            count++;

            addCustomerDuesList(customerName, amount, phone);
        }

    }

    public void addCustomerDuesList(String customerName, String amount, String phone){

        Log.d("DATABASE READER", customerName);
        View view = getLayoutInflater().inflate(R.layout.customer_dues_list, null);

        TextView tvcustomerName = view.findViewById(R.id.customerName_customerDuesLayout);
        TextView tvcustomerPhone = view.findViewById(R.id.customerPhone_customerDuesLayout);
        TextView tvcustomerDue = view.findViewById(R.id.customerDue_customerDuesLayout);
        Button paidButton = view.findViewById(R.id.paidButton_customerDuesLayout);

        tvcustomerName.setText(customerName);
        tvcustomerPhone.setText(phone);
        tvcustomerDue.setText(amount);

        paidButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                database.getReference().child("Data").child(userID).child("dues").child(customerName).removeValue();
                Toast.makeText(CustomerDuesPage.this, "Fully paid", Toast.LENGTH_SHORT).show();

                LoginPage.loginReturn = true;
                Dashboard.dashboardReturn = true;

                LocalDate date = LocalDate.now();
                String dateVal = String.valueOf(date);
                Log.d("displayString", String.valueOf(date));

                String currentMonth = dateVal.substring((dateVal.indexOf("-")+1) , (dateVal.indexOf("-")+3));
                String currentDay = dateVal.substring((dateVal.indexOf("-")+4) , dateVal.length());

                double updatedMonthlyIncome = Double.parseDouble(amount) + monthlyIncome;
                double updatedDailyIncome = Double.parseDouble(amount) + dailyIncome;

                DashboardData d = new DashboardData(updatedMonthlyIncome+"", updatedDailyIncome+"", currentDay, currentMonth, false, false);
                database.getReference().child("Data").child(userID).child("dashboard").setValue(d);

                layout.removeView(view);
            }
        });

        layout.addView(view);
    }
}