package com.srapp.Model;

public class OrderDetailsModel {

    private String productName;
    private String price;
    private int quantity;
    private String total;

    public OrderDetailsModel(String productName, String price, int quantity, String total) {
        this.productName = productName;
        this.price = price;
        this.quantity = quantity;
        this.total = total;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }
}
