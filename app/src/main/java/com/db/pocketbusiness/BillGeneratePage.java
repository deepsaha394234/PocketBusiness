package com.db.pocketbusiness;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDate;

public class BillGeneratePage extends AppCompatActivity {

    private String array[][] = BillingPage.arr;
    private FirebaseDatabase database;
    private LinearLayout layout;
    private double totalPrice;
    private double monthlyIncome, dailyIncome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill_generate_page);

        database = FirebaseDatabase.getInstance(MainActivity.DATABASELINK);
        layout = findViewById(R.id.billGenContainer);

        EditText customerName = findViewById(R.id.customerName_billGenPage);
        EditText customerPhone = findViewById(R.id.customerPhone_billGenPage);
        EditText amount = findViewById(R.id.amount_billGenPage);
        Button payButton= findViewById(R.id.payButton_billGenPage);

        for (int i = 0; i < array.length; i++) {
            Log.d("array", array[i][0]);

            int finalI = i;
            database.getReference().child("Data").child(BillingPage.userID).child("portfolio")
                    .child(array[i][0]).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {

                    databaseReader(String.valueOf(task.getResult().getValue()),array[finalI][1]);

                }
            });

        }

        database.getReference().child("Data").child(BillingPage.userID).child("dashboard").child("monthly_income").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                getMonthlyValue(String.valueOf(task.getResult().getValue()));
            }
        });
        database.getReference().child("Data").child(BillingPage.userID).child("dashboard").child("daily_income").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                getDailyValue(String.valueOf(task.getResult().getValue()));
            }
        });

        payButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                LoginPage.loginReturn = true;
                Dashboard.dashboardReturn = true;
                LocalDate date = LocalDate.now();
                String dateVal = String.valueOf(date);
                Log.d("displayString", String.valueOf(date));

                String currentMonth = dateVal.substring((dateVal.indexOf("-")+1) , (dateVal.indexOf("-")+3));
                String currentDay = dateVal.substring((dateVal.indexOf("-")+4) , dateVal.length());

                if (Double.parseDouble(amount.getText().toString()) < totalPrice){
                    CustomerDuesData data = new CustomerDuesData(customerPhone.getText().toString(), totalPrice - Double.parseDouble(amount.getText().toString())+"");
                    database.getReference().child("Data").child(BillingPage.userID).child("dues").child(customerName.getText().toString()).setValue(data);

                    DashboardData d = new DashboardData((monthlyIncome + Double.parseDouble(amount.getText().toString()))+"", (dailyIncome + Double.parseDouble(amount.getText().toString()))+"", currentDay, currentMonth, false, false);
                    database.getReference().child("Data").child(BillingPage.userID).child("dashboard").setValue(d);

                    Toast.makeText(BillGeneratePage.this, "Check Dues Page", Toast.LENGTH_SHORT).show();
                    Dashboard.dashboardReturn = true;
                    onBackPressed();
                }
                else{
                    DashboardData d = new DashboardData((monthlyIncome + Double.parseDouble(amount.getText().toString()))+"", (dailyIncome + Double.parseDouble(amount.getText().toString()))+"", currentDay, currentMonth, false, false);
                    database.getReference().child("Data").child(BillingPage.userID).child("dashboard").setValue(d);
                    Toast.makeText(BillGeneratePage.this, "PAID SUCCESSFULLY", Toast.LENGTH_SHORT).show();
                    onBackPressed();
                }
            }
        });

    }

    protected void getMonthlyValue(String data){
        monthlyIncome = Double.parseDouble(data);
    }

    protected void getDailyValue(String data){
        dailyIncome = Double.parseDouble(data);
    }

    private void databaseReader(String data, String quantityLeft){
        Log.d("DATABASE READER", data);
        data = data.substring(1,data.length());
        String  productName,buyPrice,unit,quantity,sellPrice;

        buyPrice = data.substring((data.indexOf('=')+1),(data.indexOf(',')));
        data = data.substring((data.indexOf(',')+2),data.length());

        unit = data.substring((data.indexOf('=')+1),(data.indexOf(',')));
        data = data.substring((data.indexOf(',')+2),data.length());

        quantity = data.substring((data.indexOf('=')+1),(data.indexOf(',')));
        data = data.substring((data.indexOf(',')+2),data.length());

        sellPrice = data.substring((data.indexOf('=')+1),(data.indexOf(',')));
        data = data.substring((data.indexOf(',')+2),data.length());

        productName = data.substring((data.indexOf('=')+1),(data.indexOf('}')));
        data = data.substring((data.indexOf('}')+1),data.length());


        Log.d("firebase", (productName + " , " + buyPrice + " , " + unit + " , " + quantity + " , " + sellPrice));

//       Toast.makeText(ProductDetailsPage.this,(countItems+""), Toast.LENGTH_SHORT).show();
        addBillGenItem(productName,quantity,buyPrice,sellPrice,unit, quantityLeft);


        Log.d("firebase", data);

    }

    public void addBillGenItem(String productName, String quantity, String buyPrice, String sellPrice, String unit, String quantityLeft){

        View view = getLayoutInflater().inflate(R.layout.bill_gen_list, null);

        TextView tvItemName = view.findViewById(R.id.finalItemName_billGenList);
        TextView tvQuantity = view.findViewById(R.id.finalQuantity_billGenList);
        TextView tvUnit = view.findViewById(R.id.finalUnit_billGenList);
        TextView tvPrice = view.findViewById(R.id.finalItemPrice_billGenList);

        double itemQuantity = Double.parseDouble(quantity) - Double.parseDouble(quantityLeft);
        double priceCalc = itemQuantity* Double.parseDouble(sellPrice);
        totalPrice += priceCalc;

        TextView tvTotalPrice = findViewById(R.id.totalPrice_billGenPage);
        tvTotalPrice.setText(totalPrice+"");

        tvItemName.setText(productName);
        tvQuantity.setText(itemQuantity+"");
        tvUnit.setText(unit);
        tvPrice.setText(priceCalc+"");

        layout.addView(view);


    }

}