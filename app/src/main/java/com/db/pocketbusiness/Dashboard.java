package com.db.pocketbusiness;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;

import java.time.LocalDate;

public class Dashboard extends AppCompatActivity {

    private FirebaseDatabase database;
    private String userID;
    private double totalDues;
    public static boolean dashboardReturn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        totalDues = 0.0;
        Intent intent = getIntent();
        userID = intent.getStringExtra("userID");

        database = FirebaseDatabase.getInstance(MainActivity.DATABASELINK);

        database.getReference().child("Data").child(userID).child("dues").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(!String.valueOf(task.getResult().getValue()).equals("null"))
                    getDuesValue(String.valueOf(task.getResult().getValue()));
            }
        });

        database.getReference().child("Data").child(userID).child("dashboard").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                getMonthlyValue(String.valueOf(task.getResult().getValue()));
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        totalDues = 0.0;
        if (dashboardReturn){
            dashboardReturn = false;
            onBackPressed();
        }

    }

    public void getDuesValue(String data){
        Log.d("displayString", data);

        double dues=0.0;
        String name;

        int count = 0;
        while(data.indexOf("}") != 0 || count == 0) {
            if (count == 0)
                name = data.substring((data.indexOf("{")+1), (data.indexOf("=")));
            else
                name = data.substring((data.indexOf(",")+2),(data.indexOf("=")));
            data = data.substring((data.indexOf("=")+2) , (data.length()));

            dues = Double.parseDouble(data.substring((data.indexOf("=")+1) , (data.indexOf(","))));
            data = data.substring((data.indexOf("}")+1) , (data.length()));

            count++;

            updateDuesValue(dues);
        }
    }

    public void updateDuesValue(double dues){

        TextView tvDues = findViewById(R.id.dues_dashboardPage);

        totalDues += dues;
        tvDues.setText("₹" + totalDues+"");

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void getMonthlyValue(String data){
        Log.d("displayString", data);

        String month, daily_income_flag, monthly_income, daily_income, day, monthly_income_flag;

            monthly_income_flag = data.substring((data.indexOf("=")+1), (data.indexOf(",")));
            data = data.substring((data.indexOf(",")+2) , (data.length()));

            month = data.substring((data.indexOf("=")+1), (data.indexOf(",")));
            data = data.substring((data.indexOf(",")+2) , (data.length()));

            daily_income_flag = data.substring((data.indexOf("=")+1), (data.indexOf(",")));
            data = data.substring((data.indexOf(",")+2) , (data.length()));

            monthly_income = data.substring((data.indexOf("=")+1), (data.indexOf(",")));
            data = data.substring((data.indexOf(",")+2) , (data.length()));

            daily_income = data.substring((data.indexOf("=")+1), (data.indexOf(",")));
            data = data.substring((data.indexOf(",")+2) , (data.length()));

            day = data.substring((data.indexOf("=")+1), (data.indexOf("}")));

            updateMonthlyValue(month, daily_income_flag, monthly_income, daily_income, day, monthly_income_flag);

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void updateMonthlyValue(String month,String  daily_income_flag,String  monthly_income,String  daily_income,
                                   String  day,String  monthly_income_flag){
        Log.d("displayString", "");

        LocalDate date = LocalDate.now();
        String dateVal = String.valueOf(date);
        Log.d("displayString", String.valueOf(date));

        String currentMonth = dateVal.substring((dateVal.indexOf("-")+1) , (dateVal.indexOf("-")+3));
        String currentDay = dateVal.substring((dateVal.indexOf("-")+4) , dateVal.length());

        if(!currentMonth.equals(month)){
            DashboardData d = new DashboardData("0.0", "0.0", currentDay, currentMonth, false, false);
            database.getReference().child("Data").child(userID).child("dashboard").setValue(d);
        }
        if(!currentDay.equals(day)){
            DashboardData d = new DashboardData(monthly_income, "0.0", currentDay, currentMonth, false, false);
            database.getReference().child("Data").child(userID).child("dashboard").setValue(d);
        }



        Log.d("displayString", currentDay + "/" + currentMonth);

        TextView tvMonthly = findViewById(R.id.monthly_dashboardPage);
        TextView tvDaily = findViewById(R.id.daily_dashboardPage);

        tvMonthly.setText(monthly_income);
        tvDaily.setText(daily_income);



//        double dues=0.0;
//        String name;
//
//        int count = 0;
//        while(data.indexOf("}") != 0 || count == 0) {
//            if (count == 0)
//                name = data.substring((data.indexOf("{")+1), (data.indexOf("=")));
//            else
//                name = data.substring((data.indexOf(",")+2),(data.indexOf("=")));
//            data = data.substring((data.indexOf("=")+2) , (data.length()));
//
//            dues = Double.parseDouble(data.substring((data.indexOf("=")+1) , (data.indexOf(","))));
//            data = data.substring((data.indexOf("}")+1) , (data.length()));
//
//            count++;
//
//            updateDuesValue(dues);
//        }
    }


    public void productDetailsPage(View v){
        Intent intent = new Intent(Dashboard.this, ProductDetailsPage.class);
        intent.putExtra("userID", userID);
        ProductDetailsPage.onResumeRefreshFlag = false;
        startActivity(intent);
    }

    public void billingPage(View v){
        Intent intent = new Intent(Dashboard.this, BillingPage.class);
        intent.putExtra("userID", userID);
        startActivity(intent);
    }

    public void customerDuesPage(View v){
        Intent intent = new Intent(Dashboard.this, CustomerDuesPage.class);
        intent.putExtra("userID", userID);
        startActivity(intent);
    }
}