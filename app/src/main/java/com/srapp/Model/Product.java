package com.srapp.Model;

public class Product {
    String product_name;
    double quantity;
    int prductId;
    boolean selected;

    public Product(int prductId , String product_name, int quantity, boolean selected) {
        this.product_name = product_name;
        this.quantity = quantity;
        this.prductId = prductId;
        this.selected = selected;
    }


    public int getPrductId() {
        return prductId;
    }

    public void setPrductId(int prductId) {
        this.prductId = prductId;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
