package com.db.pocketbusiness;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.FirebaseDatabase;

public class ProductEditPage extends AppCompatActivity {

    private String productName,productID,unit;
    private double buyPrice,quantity, sellPrice;
    private TextView tvbuyPrice,tvquantity,tvsellPrice,tvproductName;
    private Button add;
    private AlertDialog dialog;
    private FirebaseDatabase database;
    private String userID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_edit_page);

        ProductDetailsPage.onResumeRefreshFlag = true;

        Intent intent = getIntent();

        productName = intent.getStringExtra("productName_productDetails");
        productID = intent.getStringExtra("productID_productDetails");
        unit = intent.getStringExtra("productUnit_productDetails");
        buyPrice = Double.parseDouble(intent.getStringExtra("buyPrice_productDetails"));
        quantity = Double.parseDouble(intent.getStringExtra("quantity_productDetails"));
        sellPrice = Double.parseDouble(intent.getStringExtra("sellPrice_productDetails"));
        userID = intent.getStringExtra("userID");

        tvproductName = findViewById(R.id.productName_productEditPage);
        tvbuyPrice = findViewById(R.id.buyPrice_productEditPage);
        tvquantity = findViewById(R.id.quantity_productEditPage);
        tvsellPrice = findViewById(R.id.sellPrice_productEditPage);
        add = findViewById(R.id.addButton_productEditPage);

        tvproductName.setText(productName);
        tvbuyPrice.setText((buyPrice+""));
        tvquantity.setText((quantity+""));
        tvsellPrice.setText((sellPrice+""));

        database = FirebaseDatabase.getInstance(
                MainActivity.DATABASELINK);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buildDialog();
                dialog.show();
            }
        });

    }

    private void buildDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        View view = getLayoutInflater().inflate(R.layout.product_editpage_edit_layout,null);


        builder.setView(view);

        builder.setTitle("Add new product details")
                .setPositiveButton("ADD", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        EditText etnewBuyPrice = view.findViewById(R.id.productBuyPrice_productEditDialog);
                        EditText etnewQuantity = view.findViewById(R.id.productQuantity_productEditDialog);
                        EditText etnewSellPrice = view.findViewById(R.id.productSellPrice_productEditDialog);

                        double newBuyPrice = Double.parseDouble(etnewBuyPrice.getText().toString());
                        double newQuantity = Double.parseDouble(etnewQuantity.getText().toString());
                        double newSellPrice = Double.parseDouble(etnewSellPrice.getText().toString());

                        double editedBuyPrice,editedSellPrice,editedQuantity;

                        double totalProfit = ((newSellPrice-buyPrice)*quantity) +
                                ((newSellPrice-newBuyPrice)*newQuantity);
                        double totalSellPrice = (quantity + newQuantity) * newSellPrice;

                        editedBuyPrice = (totalSellPrice - totalProfit)/(quantity+newQuantity);
                        editedBuyPrice = (int)(editedBuyPrice*100);
                        editedBuyPrice = editedBuyPrice/100;
                        editedQuantity = (quantity+newQuantity);
                        editedSellPrice = newSellPrice;

                        tvbuyPrice.setText((editedBuyPrice+""));
                        tvquantity.setText((editedQuantity+""));
                        tvsellPrice.setText((editedSellPrice+""));
                        buyPrice = editedBuyPrice;
                        quantity = editedQuantity;
                        sellPrice = editedSellPrice;

                        Log.d("EDIT BUY PRICE",editedBuyPrice+" "+editedQuantity+" "+editedSellPrice);
                        AddItemData data = new AddItemData(productName, editedQuantity+"",editedBuyPrice+"",editedSellPrice+"",unit);
                        database.getReference().child("Data").child(userID).child("portfolio").child(productID).setValue(data);

                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        dialog = builder.create();

//        double newBuyPrice = Double.parseDouble(etnewBuyPrice.getText().toString());
//        int newQuantity = Integer.parseInt(etnewQuantity.getText().toString());
//        double newSellPrice = Double.parseDouble(etnewSellPrice.getText().toString());


    }


}