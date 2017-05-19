package com.pawel.wozeksklepowy;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.constraint.solver.Cache;
import android.support.design.widget.Snackbar;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;

public class AddProductActivity extends Activity {

    EditText productName;
    EditText price;
    EditText barcode;

    Button mAddButton;
    Button mCancelButton;
    Button mBarcodeButton;

    Uri outputFileUri;
    BarcodeDetector detector;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        productName = (EditText) findViewById(R.id.product_name_edit);
        price = (EditText) findViewById(R.id.price_edit);
        barcode = (EditText) findViewById(R.id.barcode_edit);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference("produkty");


        mAddButton = (Button) findViewById(R.id.add_button);
        mCancelButton = (Button) findViewById(R.id.cancel_button);
        mBarcodeButton = (Button) findViewById(R.id.barcode_button);

        mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean ok = true;
                if (productName.getText().toString().isEmpty()){
                    productName.setError("Fill this first!");
                    ok = false;
                }
                if (price.getText().toString().isEmpty()) {
                    price.setError("Fill this first!");
                    ok = false;
                }
                if(barcode.getText().toString().isEmpty()) {
                    barcode.setError("Fill this first!");
                    ok = false;
                }
                if (ok){
                    Product product = new Product(productName.getText().toString(),
                            price.getText().toString());
                    myRef.child(barcode.getText().toString()).setValue(product);
                    finish();
                    setResult(1);
                }


            }
        });

        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(0);
                finish();
            }
        });

        detector = new BarcodeDetector.Builder(getApplicationContext())
                .build();

        if(!detector.isOperational()){
            Toast.makeText(this, "Could not set up detector", Toast.LENGTH_LONG).show();
            return;
        }

        mBarcodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    File file = new File(Environment.getExternalStoragePublicDirectory
                            (Environment.DIRECTORY_DOWNLOADS), "temp_pic.jpg");
                    outputFileUri = Uri.fromFile(file);

                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
                startActivityForResult(intent, 1);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {

        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), outputFileUri);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Frame frame = new Frame.Builder().setBitmap(bitmap).build();
        SparseArray<Barcode> barcodes = detector.detect(frame);

        if (barcodes.size()>0) {
            Barcode thisCode = barcodes.valueAt(0);
            barcode.setText(thisCode.rawValue);
        }
        else{
            Toast.makeText(this, "no barcode found", Toast.LENGTH_LONG).show();
        }
    }
}
