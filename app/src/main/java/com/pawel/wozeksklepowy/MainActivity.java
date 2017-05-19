package com.pawel.wozeksklepowy;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;

public class MainActivity extends AppCompatActivity {

    Uri outputFileUri;
    BarcodeDetector detector;


    ListView listView;
    FloatingActionButton add;
    int REQUEST_READ_EXTERNAL_STORAGE = 1;

    ArrayList<Product> listOfMyProducts;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        listView = (ListView) findViewById(R.id.myList);
        add = (FloatingActionButton) findViewById(R.id.fab);

        detector = new BarcodeDetector.Builder(getApplicationContext())
                .build();

        if(!detector.isOperational()){
            Toast.makeText(this, "Could not set up detector", Toast.LENGTH_LONG).show();
            return;
        }

        add.setOnClickListener(new View.OnClickListener() {
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



        ListView listView = (ListView) findViewById(R.id.myList);

        listOfMyProducts = new ArrayList<>();
        listView.setAdapter(new GetProductsAdapter(this, listOfMyProducts));

        // use the SimpleCursorAdapter to show the
        // elements in a ListView

        checkExternalStoragePermissions();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, ProductsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private boolean checkExternalStoragePermissions() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }

        requestPermissions(new String[]{READ_EXTERNAL_STORAGE}, REQUEST_READ_EXTERNAL_STORAGE);
        return false;
    }

    /**
     * Callback received when a permissions request has been completed.
     */

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_EXTERNAL_STORAGE) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            }
            else {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Potrzebujesz uprawnień do poprawnego dzialania aplikacji." +
                        " Uruchom aplikacje ponownie i zakceptuj je.");
                builder.setTitle("Uwaga!");
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
                builder.show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {

        Bitmap bitmap = null;


        File file = new File(Environment.getExternalStoragePublicDirectory
                (Environment.DIRECTORY_DOWNLOADS), "temp_pic.jpg");

        try {
            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), outputFileUri);
            bitmap.compress(Bitmap.CompressFormat.WEBP, 50, new FileOutputStream(file));

        } catch (IOException e) {
            e.printStackTrace();
        }

            Frame frame = new Frame.Builder()
                    .setBitmap(bitmap)
                    .build();



        SparseArray<Barcode> barcodes = detector.detect(frame);

        if (barcodes.size()>0) {
            Barcode thisCode = barcodes.valueAt(0);
            Toast.makeText(this, "code: " + thisCode.displayValue, Toast.LENGTH_LONG).show();
        }
        else{
            Toast.makeText(this, "no barcode found", Toast.LENGTH_LONG).show();
        }
    }
}

