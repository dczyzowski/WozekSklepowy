package com.pawel.wozeksklepowy;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.SparseArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.analytics.ecommerce.ProductAction;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ProductsActivity extends ListActivity {

    //deklaruje obiekty do uzycia bazy danych Google Firebase
    FirebaseDatabase mDataBase;
    DatabaseReference myRef;

    // Obiekty do dzialania czytnika kodów kreskowych
    Uri outputFileUri;
    BarcodeDetector detector;

    // lista produktow  do ktorej zapisze zapisane produkty
    List<Product> products;
    GetProductsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products);

        //deklaracje obiektóω
        mDataBase = FirebaseDatabase.getInstance();
        products = new ArrayList<>();

        myRef = mDataBase.getReference("produkty");
        adapter = new GetProductsAdapter(this, products);
        setListAdapter(adapter);

        // detektor kodów kreskowych, inicjalizacja
        detector = new BarcodeDetector.Builder(getApplicationContext()).build();

        // nie mozna skonfigurowac detektora
        if(!detector.isOperational()){
            Toast.makeText(this, "Could not set up detector", Toast.LENGTH_LONG).show();
            return;
        }
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        // do listy moich produktow dodaje wybrany element z listy
        MainActivity.addListOfMyProducts(products.get(position));
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();

        // sprawdzam czy została wprowadzona zmiana w bazie danych
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                products.clear();

                // pobieramy wszystkie dane od nowa gdy zostanie wprowadzona zmiana
                for (DataSnapshot productsSnapshot : dataSnapshot.getChildren()){
                    Product product = productsSnapshot.getValue(Product.class);
                    products.add(product);
                }

                // ponownie generuje listView
                adapter = new GetProductsAdapter(ProductsActivity.this, products);
                ProductsActivity.this.setListAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    // wcisnieto przycisk skanowania kodow kreskowych
    public void onScan(View view) {
        File file = new File(Environment.getExternalStoragePublicDirectory
                (Environment.DIRECTORY_DOWNLOADS), "temp_pic.jpg");
        outputFileUri = Uri.fromFile(file);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
        startActivityForResult(intent, 1);
    }

    // wynik skanowania kodow kreskowych
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        Bitmap bitmap = null;

        File file = new File(Environment.getExternalStoragePublicDirectory
                (Environment.DIRECTORY_DOWNLOADS), "temp_pic.jpg");

        // kompresowanie pliku do mniejszego formatu
        try {
            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), outputFileUri);
            bitmap.compress(Bitmap.CompressFormat.WEBP, 50, new FileOutputStream(file));

        } catch (IOException e) {
            e.printStackTrace();
        }

        // ramka do odczytywania kodów kreskowych
        Frame frame = new Frame.Builder()
                .setBitmap(bitmap)
                .build();

        //tablica odczytanych kodow kreskowych, moze byc wiecej na jednym zdjeciu
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
