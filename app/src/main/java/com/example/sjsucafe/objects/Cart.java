package com.example.sjsucafe.objects;

import java.util.ArrayList;
import java.util.List;

public class Cart {

    private String userId;
    private String restaurantId;
    private List<MenuItem> items;
    private double total;

    public Cart(String userId, String restaurantId) {
        this.userId = userId;
        this.restaurantId = restaurantId;
        this.items = new ArrayList<>();
        this.total = 0;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
    }

    public List<MenuItem> getItems() {
        return items;
    }

    public void setItems(List<MenuItem> items) {
        this.items = items;
        calculateTotal();
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public void addItem(MenuItem item) {
        items.add(item);
        calculateTotal();
    }

    public void removeItem(MenuItem item) {
        items.remove(item);
        calculateTotal();
    }

    private void calculateTotal() {
        double totalCost = 0;
        for (MenuItem item : items) {
            totalCost += item.getPrice() * item.getQuantity();
        }
        total = totalCost;
    }

    public void setId(String id) {
        this.userId = id;
    }

    public String getId() {
        return userId;
    }
}
