package com.pawel.wozeksklepowy;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Damian on 2017-05-17.
 */

public class GetProductsAdapter extends ArrayAdapter<Product> {

    Activity context;
    List<Product> mProducts;

    /*
    adapter generuje liste rzeczy ktore maja sie znajdowac w ListView. Tutaj generujemy liste
    wszystkich produktów
    */

    public GetProductsAdapter(Activity context, List<Product> mProducts){
        super(context, R.layout.tabele_row, mProducts);

        this.context = context;
        this.mProducts = mProducts;
    }

    static class ViewHolder {
        protected TextView productName;
        protected TextView productPrice;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View view = inflater.inflate(R.layout.tabele_row, null, true);

        ViewHolder holder = new ViewHolder();

        holder.productName = (TextView) view.findViewById(R.id.textFirst);
        holder.productPrice = (TextView) view.findViewById(R.id.textSecond);

        Product products = mProducts.get(position);

        holder.productName.setText(products.getProductName());
        holder.productPrice.setText(products.getPrice() + " zł");

        return view;
    }

}
