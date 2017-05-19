package com.pawel.wozeksklepowy;

/**
 * Created by Damian on 2017-05-16.
 */

public class Product {

    String productName;
    String price;

    Product(){
    }

    Product(String productName, String price){
        this.productName = productName;
        this.price = price;
    }

    public String getProductName() {
        return productName;
    }

    public String getPrice() {
        return price;
    }
}
