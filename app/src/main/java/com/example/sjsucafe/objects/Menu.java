package com.example.sjsucafe.objects;

public class Menu {
    private String _id;
    private String restaurantId;
    private MenuItem[] items;
    private int __v;

    public String getId() {
        return _id;
    }

    public String getRestaurantId() {
        return restaurantId;
    }

    public MenuItem[] getItems() {
        return items;
    }

    public int getVersion() {
        return __v;
    }
}

