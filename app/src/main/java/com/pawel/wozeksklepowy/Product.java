package com.pawel.wozeksklepowy;

/**
 * Created by Damian on 2017-05-16.
 */

public class Product {

    private String productName;
    private String price;
    private String barcode;

    Product(){
    }

    Product(String productName, String price, String barcode){
        this.productName = productName;
        this.price = price;
        this.barcode = barcode;
    }

    String getProductName() {
        return productName;
    }

    String getPrice() {
        return price;
    }

    String getBarcode() {
        return barcode;
    }
}
