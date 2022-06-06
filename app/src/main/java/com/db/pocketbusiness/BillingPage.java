package com.db.pocketbusiness;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class BillingPage extends AppCompatActivity {

    public static String productID = "";
    public static boolean scanSuccess = false;
    public LinearLayout layout, layoutItem;

    public static String userID;
    public FirebaseDatabase database;

    private ProgressDialog progressDialog;

    private List<String> unitList = new ArrayList<>();

    public double totalCalculatedPrice;

    public static String arr[][];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_billing_page);

        Intent intent = getIntent();
        userID = intent.getStringExtra("userID");

        layout = findViewById(R.id.billListContainer);
        layoutItem = findViewById(R.id.billListContainer);
        database = FirebaseDatabase.getInstance(MainActivity.DATABASELINK);

        arr = new String[1][2];
        arr[0][0] = "null";


    }

    public void scanQRCode(View v){

        Intent intent = new Intent(BillingPage.this, ScannerView.class);
        startActivity(intent);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(scanSuccess){
            scanSuccess = false;

            progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Please wait");
            progressDialog.setMessage("We are adding your item in billing list");
            progressDialog.show();

            database.getReference().child("Data").child(userID).child("portfolio").child(productID).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if (task.isSuccessful()) {
//                        Toast.makeText(BillingPage.this, String.valueOf(task.getResult().getValue()), Toast.LENGTH_LONG).show();
                        databaseReader(String.valueOf(task.getResult().getValue()));
                    } else {
                        Log.e("firebase", "Error getting data", task.getException());
                        progressDialog.dismiss();
                    }

                }
            });


        }
    }

    private void databaseReader(String data){
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


        Log.d("firebase", (productID + " , " + productName + " , " + buyPrice + " , " + unit + " , " + quantity + " , " + sellPrice));

//       Toast.makeText(ProductDetailsPage.this,(countItems+""), Toast.LENGTH_SHORT).show();
        addBillItem(productName,quantity,buyPrice,sellPrice,unit,productID);


        Log.d("firebase", data);

    }

    public String returnQuantity(String data){

        String productQuantity = data.substring(data.indexOf(',')+10, data.indexOf('}')-1);
        Log.d("app", productQuantity);
        return productQuantity;

    }

    public void addArrayItem(String productID, String quantity){
        Log.d("array", "product ID = " + productID);

        if(!arr[0][0].equals("null")) {

            for (int i = 0; i < arr.length; i++) {
                if (arr[i][0].equals(productID)){
                    Log.d("array", "Duplicate");
                    return;
                }
            }

            String temp[][] = new String[arr.length + 1][2];

            for (int i = 0; i < arr.length; i++) {
                temp[i][0] = arr[i][0];
                temp[i][1] = arr[i][1];
            }
            temp[temp.length-1][0] = productID;
            temp[temp.length-1][1] = quantity;
            arr = new String[temp.length][2];
            for (int i = 0; i < temp.length; i++) {
                arr[i][0] = temp[i][0];
                arr[i][1] = temp[i][1];
            }
        }
        else{
            arr[0][0] = productID;
            arr[0][1] = quantity;
        }
    }

    public void editArrayItem(String productID, String editValue, boolean isDelete){

        if(isDelete){

            database.getReference().child("Data").child(userID).child("portfolio").child(productID).child("quantity")
                    .get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if(String.valueOf(task.getResult().getValue()).equals(editValue)){
                        Log.d("displayarray", "END REACHED" + arr.length);
                        if(arr.length != 1) {
                            int index = 0;
                            for (int i = 0; i < arr.length; i++)
                                if (arr[i][0].equals(productID))
                                    arr[i][0] = "-";
                            String temp[][] = new String[arr.length - 1][2];
                            for (int i = 0; i < arr.length - 1; i++) {
                                if (!arr[i][0].equals("-")) {
                                    temp[index][0] = arr[i][0];
                                    temp[index][1] = arr[i][1];
                                    index++;
                                }
                            }
                            arr = new String[temp.length][2];
                            for (int i = 0; i < temp.length; i++) {
                                arr[i][0] = temp[i][0];
                                arr[i][1] = temp[i][1];
                            }
                        }
                        else
                            arr[0][0] = "null";

                    }
                }
            });


        }
        else {
            for (int i = 0; i < arr.length; i++)
                if (arr[i][0].equals(productID))
                    arr[i][1] = editValue;
        }

    }

    public int returnIndexOfProduct(String productID){
        for(int i=0;i<arr.length;i++)
            if(arr[i][0].equals(productID))
                return i;
            return 0;
    }

    public void displayArray(){
        for(int i=0;i<arr.length;i++)
            Log.d("displayarray" , arr[i][0]+ "   "+ arr[i][1]);
    }

    public void addBillItem(String productName, String quantity, String buyPrice, String sellPrice, String unit, String productID){
//        View billEdit = layout.getRootView();
//        layout.removeView(billEdit);

        progressDialog.dismiss();

        addArrayItem(productID, quantity);
        displayArray();

        View billEdit = getLayoutInflater().inflate(R.layout.billing_edit_layout, null);

        TextView productNameEdit = billEdit.findViewById(R.id.productName_billEditLayout);
        EditText productQuantityEdit = billEdit.findViewById(R.id.productQuantity_billEditLayout);
        Button addButtonEdit = billEdit.findViewById(R.id.addButton_billingEditLayout);
        Spinner spinnerEdit = billEdit.findViewById(R.id.unit_billEditLayout);

        if(unit.equals("Kg")) {

            unitList.add("Kg");
            unitList.add("Gm");

            ArrayAdapter unitEdit = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, unitList);
            spinnerEdit.setAdapter(unitEdit);
        }
        else if(unit.equals("No Unit")){
            unitList.add("No Unit");

            ArrayAdapter unitEdit = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, unitList);
            spinnerEdit.setAdapter(unitEdit);
        }

        productNameEdit.setText(productName);

        layout.addView(billEdit);


        addButtonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                View billItem = getLayoutInflater().inflate(R.layout.billing_layout,null);

                TextView productQuantityList = billItem.findViewById(R.id.productQuantity_billListLayout);
                TextView productUnitList = billItem.findViewById(R.id.productUnit_billListLayout);
                TextView productNameList = billItem.findViewById(R.id.productName_billListLayout);
                TextView calculatedPrice = billItem.findViewById(R.id.calculatedPrice_billListLayout);
                Button deleteBillItem = billItem.findViewById(R.id.deleteButton_billListLayout);

                productUnitList.setText(spinnerEdit.getSelectedItem().toString());
                productNameList.setText(productName);


                //Price calculate section
                String productUnit = spinnerEdit.getSelectedItem().toString();
                double calculatedPriceValue=0.0;

                double quantityEdit = Double.parseDouble(productQuantityEdit.getText().toString());
                if(Double.parseDouble(arr[returnIndexOfProduct(productID)][1]) < quantityEdit){
                    quantityEdit = Double.parseDouble(arr[returnIndexOfProduct(productID)][1]);
                }


                productQuantityList.setText(quantityEdit + "");

                if(productUnit.equals("Kg") || productUnit.equals("No Unit")){

                    calculatedPriceValue = quantityEdit * Double.parseDouble(sellPrice);

                    editArrayItem(productID, (Double.parseDouble(arr[returnIndexOfProduct(productID)][1]) - quantityEdit)+"", false);
                    displayArray();
//                    AddItemData data = new AddItemData(productName, ((Double.parseDouble(quantity) - quantityEdit)+""), buyPrice,sellPrice,unit);
//
//                    database.getReference().child("Data").child(userID).child("portfolio").child(productID).setValue(data);
                }
                else if(productUnit.equals("Gm")){
                    calculatedPriceValue = (quantityEdit /1000.0) * (Double.parseDouble(sellPrice));

                    editArrayItem(productID, ((Double.parseDouble(arr[returnIndexOfProduct(productID)][1])) - (quantityEdit/1000))+"", false);
                    displayArray();

//                    AddItemData data = new AddItemData(productName, ((Double.parseDouble(quantity) - (quantityEdit/1000))+""), buyPrice,sellPrice,unit);
//
//                    database.getReference().child("Data").child(userID).child("portfolio").child(productID).setValue(data);
                }

                calculatedPrice.setText(calculatedPriceValue + "");
                totalCalculatedPrice += calculatedPriceValue;
                Toast.makeText(BillingPage.this, totalCalculatedPrice+"", Toast.LENGTH_SHORT).show();

                layout.removeView(billEdit);

                layoutItem.addView(billItem);


                double finalCalculatedPriceValue = calculatedPriceValue;
                deleteBillItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        totalCalculatedPrice -= finalCalculatedPriceValue;

                        editArrayItem(productID, ((Double.parseDouble(arr[returnIndexOfProduct(productID)][1])+Double.parseDouble(productQuantityList.getText().toString()))+""), true);
                        displayArray();

                        layoutItem.removeView(billItem);

//                        database.getReference().child("Data").child(userID).child("portfolio").child(productID).child("quantity").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
//                            @Override
//                            public void onComplete(@NonNull Task<DataSnapshot> task) {
//
//                                double addQuantity = Double.parseDouble(returnQuantity(task.getResult().toString()));
//                                AddItemData data = new AddItemData(productName, ((addQuantity+Double.parseDouble(productQuantityList.getText().toString()))+""), buyPrice,sellPrice,unit);
//
//                                database.getReference().child("Data").child(userID).child("portfolio").child(productID).setValue(data);
//                                layoutItem.removeView(billItem);
//                            }
//                        });

                        Toast.makeText(BillingPage.this, totalCalculatedPrice+"", Toast.LENGTH_SHORT).show();

                    }
                });

            }
        });


    }


//    BillGeneratePage drawView;
    public void billGen(View v){

        Intent intent = new Intent(BillingPage.this, BillGeneratePage.class);
        startActivity(intent);
//
//        drawView = new BillGeneratePage(this);
//        drawView.setBackgroundColor(Color.WHITE);
//        setContentView(drawView);

    }

}