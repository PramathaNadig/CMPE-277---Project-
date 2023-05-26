package com.example.sjsucafe.objects;

public class MenuItem {
    private String _id;
    private String name;
    private String description;
    private double price;
    private String image;

    private int quantity;

    public MenuItem(String id, String name, String description, double price, String image) {
        this._id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.image = image;
        this.quantity = 0;
    }

    public MenuItem(String id, String name, String description, double price, String image, int quantity) {
        this._id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.image = image;
        this.quantity = quantity;
    }

    public String getId() {
        return _id;
    }

    public void setId(String id) {
        this._id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getQuantity(){
        return this.quantity;
    }

    // set quantity
    public void setQuantity(int quantity){
        this.quantity = quantity;
    }
}

