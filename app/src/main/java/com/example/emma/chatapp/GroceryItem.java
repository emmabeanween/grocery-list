package com.example.emma.chatapp;

import java.util.UUID;

/**
 * Created by Emma on 7/2/17.
 */

public class GroceryItem {

    private UUID itemID;
    private String title;
    private double price;

    GroceryItem() {

        itemID = UUID.randomUUID();
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public UUID getID() {

        return itemID;
    }


}
