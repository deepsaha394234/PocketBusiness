package com.db.pocketbusiness;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.util.ArrayList;
import java.util.List;

public class ProductDetailsPage extends AppCompatActivity {

    public static boolean onResumeRefreshFlag = false;

    private LinearLayout linearLayout;
    private Button delete,add, edit;
    private AlertDialog dialog;
    private AlertDialog deleteConfirmationDialog;
    private AlertDialog qrCodeDisplay;

    private List<String> quantityUnitList = new ArrayList<>();

    private FirebaseDatabase database;

    private String userID;

    public boolean notNewUser;

    private ProgressDialog progressDialog;

    int countItems = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details_page);


        Intent intent = getIntent();
        userID = intent.getStringExtra("userID");

        linearLayout = findViewById(R.id.itemContainer);
        add = findViewById(R.id.addButton_productPage);

        progressDialog = new ProgressDialog(ProductDetailsPage.this);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Loading data please wait");
        progressDialog.show();

        database = FirebaseDatabase.getInstance(
                MainActivity.DATABASELINK);
        database.getReference().child("Users").child(userID).child("flag").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                notNewUser = (String.valueOf(task.getResult().getValue()).equals("false"))? false: true;
                if(!notNewUser){
                    progressDialog.dismiss();
                    countItems = 0;
                }

                else{
                    database.getReference().child("Data").child(userID).child("portfolio").get().addOnCompleteListener(
                            new OnCompleteListener<DataSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DataSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        //Toast.makeText(ProductDetailsPage.this, String.valueOf(task.getResult().getValue()), Toast.LENGTH_LONG).show();
                                        databaseReader(String.valueOf(task.getResult().getValue()));
                                    } else {
                                        Log.e("firebase", "Error getting data", task.getException());
                                    }
                                }
                            }
                    );
                }

            }
        });


        quantityUnitList.add("Kg");
        quantityUnitList.add("No Unit");

        buildDialog();

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.show();
            }
        });
    }

    private void databaseReader(String data){
        Log.d("DATABASE READER", data);
        data = data.substring(1,data.length());
        String productID, productName,buyPrice,unit,quantity,sellPrice;
        int count = 0;
        while((data.indexOf(',') == 0 && count!=0) || count == 0){
            if(count==0){
                productID = data.substring(0,data.indexOf('='));
                data = data.substring((data.indexOf('=')+2),data.length());
            }
            else{
                productID = data.substring(2,data.indexOf('='));
                data = data.substring((data.indexOf('=')+2),data.length());
            }

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

            count++;

            Log.d("firebase", (productID + " , " + productName + " , " + buyPrice + " , " + unit + " , " + quantity + " , " + sellPrice));

            countItems++;
            Toast.makeText(ProductDetailsPage.this,(countItems+""), Toast.LENGTH_SHORT).show();
            addItem(productName,quantity,buyPrice,sellPrice,unit,productID, false);


            Log.d("firebase", data);
        }
        progressDialog.dismiss();

    }


    public void addItem(String productName, String quantity, String buyPrice, String sellPrice, String unit, String productID, boolean flag){

        Log.d("ADD ITEM", productName + quantity + buyPrice + sellPrice + unit + productID);
        //If new items are added by user and not filled up by database
        if (flag){
            if(!notNewUser){
                database.getReference().child("Users").child(userID).child("flag").setValue("true");
                notNewUser = true;
            }
            countItems++;
            Toast.makeText(ProductDetailsPage.this,(countItems+""), Toast.LENGTH_SHORT).show();
            AddItemData data = new AddItemData(productName,quantity,buyPrice,sellPrice,unit);
            database.getReference().child("Data").child(userID).child("portfolio").child(productID).setValue(data);

        }

        View item = getLayoutInflater().inflate(R.layout.product_layout, null);

        TextView tvname = item.findViewById(R.id.productName_productPage);
        TextView tvquantity = item.findViewById(R.id.productQuantity_productPage);
        TextView tvunit = item.findViewById(R.id.productUnit_productPage);
        TextView tvbuyprice = item.findViewById(R.id.productBuyPrice_productPage);
        TextView tvsellprice = item.findViewById(R.id.productSellPrice_productPage);
        TextView tvproductID = item.findViewById(R.id.productID_productPage);
        ImageView qrImage = item.findViewById(R.id.qrImage_productDetailsPage);

        tvname.setText(productName);
        tvquantity.setText(quantity);
        tvbuyprice.setText(buyPrice);
        tvsellprice.setText(sellPrice);
        tvunit.setText(unit);
        tvproductID.setText(productID);

        MultiFormatWriter writer = new MultiFormatWriter();

        BitMatrix matrix = null;
        try {
            matrix = writer.encode(productID, BarcodeFormat.QR_CODE, 35,35);

            BarcodeEncoder encoder = new BarcodeEncoder();

            Bitmap bitmap = encoder.createBitmap(matrix);

            qrImage.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }

        delete  = item.findViewById(R.id.productDelete_productPage);

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                buildConfirmationDialog(linearLayout, item , productID);
                deleteConfirmationDialog.show();

            }
        });

        edit = item.findViewById(R.id.productEdit_productPage);

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(ProductDetailsPage.this, ProductEditPage.class);
                intent.putExtra("productName_productDetails", productName);
                intent.putExtra("buyPrice_productDetails",buyPrice);
                intent.putExtra("quantity_productDetails",quantity);
                intent.putExtra("sellPrice_productDetails",sellPrice);
                intent.putExtra("productID_productDetails",productID);
                intent.putExtra("productUnit_productDetails",unit);
                intent.putExtra("userID", userID);

                startActivity(intent);
            }
        });

        qrImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ProductDetailsPage.this, "IMAGE CLICKED", Toast.LENGTH_SHORT).show();
                buildQrCodeDisplayDialog(productID);
                qrCodeDisplay.show();
            }
        });

        linearLayout.addView(item);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(onResumeRefreshFlag) {
            onBackPressed();
        }
    }

    private void buildDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.product_layout_edit,null);

        EditText productName = view.findViewById(R.id.productName_productDialog);
        EditText productQuantity = view.findViewById(R.id.productQuantity_productDialog);
        EditText productBuyPrice = view.findViewById(R.id.productBuyPrice_productDialog);
        EditText productSellPrice = view.findViewById(R.id.productSellPrice_productDialog);

        Spinner quantityUnit = view.findViewById(R.id.unit_productDialog);

        ArrayAdapter quantityUnitAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, quantityUnitList);
        quantityUnit.setAdapter(quantityUnitAdapter);

        builder.setView(view);
        builder.setTitle("Enter details of product")
                .setPositiveButton("ADD", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int productIDGen = (int)(Math.random()* Math.pow(10,5));
                        addItem(productName.getText().toString(), Double.parseDouble(productQuantity.getText().toString())+"",
                                Double.parseDouble(productBuyPrice.getText().toString())+"",Double.parseDouble(productSellPrice.getText().toString())+"",
                                quantityUnit.getSelectedItem().toString(), (productIDGen + ""),true);
                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        dialog = builder.create();
    }

    public void buildConfirmationDialog(LinearLayout linearLayout, View item, String productID){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Are you sure ?")
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        database.getReference().child("Data").child(userID).child("portfolio").child(productID).removeValue();
                        countItems--;
                        Toast.makeText(ProductDetailsPage.this,(countItems+""), Toast.LENGTH_SHORT).show();
                        if(countItems == 0){
                            database.getReference().child("Users").child(userID).child("flag").setValue("false");
                            notNewUser = false;
                        }
                        linearLayout.removeView(item);
                    }
                })
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        deleteConfirmationDialog = builder.create();
    }

    public void buildQrCodeDisplayDialog(String productID){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        View view = getLayoutInflater().inflate(R.layout.qr_code_display_dialog,null);

        ImageView qrCodeImage = view.findViewById(R.id.qrImage_qrCodeDisplayDialog);

        MultiFormatWriter writer = new MultiFormatWriter();

        BitMatrix matrix = null;
        try {
            matrix = writer.encode(productID, BarcodeFormat.QR_CODE, 350,350);

            BarcodeEncoder encoder = new BarcodeEncoder();

            Bitmap bitmap = encoder.createBitmap(matrix);

            qrCodeImage.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }

        builder.setView(view)
                .setTitle("QR CODE")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        qrCodeDisplay = builder.create();


    }

}