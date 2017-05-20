package com.pawel.wozeksklepowy;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;

public class MainActivity extends AppCompatActivity {


    private ListView listView;
    private FloatingActionButton add;
    private int REQUEST_READ_EXTERNAL_STORAGE = 1;

    private static ArrayList<Product> listOfMyProducts;
    private static TextView priceText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        listView = (ListView) findViewById(R.id.myList);
        add = (FloatingActionButton) findViewById(R.id.fab);
        listView = (ListView) findViewById(R.id.myList);
        listOfMyProducts = new ArrayList<>();
        priceText = (TextView) findViewById(R.id.price);

        listView.setAdapter(new GetProductsAdapter(this, listOfMyProducts));

        // sprawdzam urawnienia
        checkExternalStoragePermissions();

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), ProductsActivity.class);
                startActivityForResult(intent, 1);
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //usuwam element z listy moich zakupów
                listOfMyProducts.remove(position);
                listView.setAdapter(new GetProductsAdapter(MainActivity.this, listOfMyProducts));
                setActualPrice();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        // wykrywam ktory przycisk z menu kontekstowego wybralem(te 3 kropki u gory w rogu)
        if (id == R.id.action_add_product) {
            Intent intent = new Intent(this, AddProductActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onResume() {
        super.onResume();

        //odswierzam liste produktow po powruceniu do tego Activity, (mozna usunac..)
        listView.setAdapter(new GetProductsAdapter(this, listOfMyProducts));

    }

    //musze sprawdzic uprawnienia w glownym watku w systemie android >= 6.0
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
     * odpowedź gdy uprawnienia zostaną zarządane
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

    // dodaje do @listOfMyProducts wybrany wczesniej produkt
    public static void addListOfMyProducts(Product product){
        listOfMyProducts.add(product);
        setActualPrice();
    }

    //aktualizuje informacje o cenie wszystkich produktow w koszyku
    static void setActualPrice(){
        double price = 0;
        for(Product product : listOfMyProducts)
        price += Double.valueOf(product.getPrice());

        String myPrice = String.format("Koszt: %.2f zł", price);

        if(listOfMyProducts.size() > 0){
            priceText.setText(myPrice);
        }
        else priceText.setText("Koszyk jest pusty!");
    }
}

