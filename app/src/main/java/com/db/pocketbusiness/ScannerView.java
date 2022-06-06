package com.db.pocketbusiness;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class ScannerView extends AppCompatActivity implements ZXingScannerView.ResultHandler{
    ZXingScannerView scannerView;
    private LinearLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        scannerView = new ZXingScannerView(this);
        setContentView(scannerView);

        Dexter.withContext(getApplicationContext())
                .withPermission(Manifest.permission.CAMERA)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        scannerView.startCamera();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).check();

    }

    @Override
    public void handleResult(com.google.zxing.Result rawResult) {
        BillingPage.productID = rawResult.getText();
        Log.d("QRCODE SCAN DATA", BillingPage.productID);
        BillingPage.scanSuccess = true;
        scannerView.stopCamera();

        //TO access billing edit page contents
//        View billEdit = getLayoutInflater().inflate(R.layout.billing_edit_layout, null);
//
//        TextView productNameEdit = billEdit.findViewById(R.id.productName_billEditLayout);
//        EditText productQuantityEdit = billEdit.findViewById(R.id.productQuantity_billEditLayout);
//        Button addButtonEdit = billEdit.findViewById(R.id.addButton_billingEditLayout);
//
//        BillingPage.layout.addView(billEdit);

        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        scannerView.stopCamera();
    }

    @Override
    protected void onPause() {
        super.onPause();
        scannerView.stopCamera();
    }

    @Override
    protected void onResume() {
        super.onResume();
        scannerView.setResultHandler(this);
        scannerView.startCamera();
    }
}