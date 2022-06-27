package com.srapp.pricing;

import java.io.Serializable;

public class GSOPrice implements Serializable {

    public static final int FLAG_FOR_FAVOURITE= 1;

    private String priceSlabEffectiveDate;
    private String min_quantity;
    private String price;
    private String special_group_id;
    private String special_price;
    private String outlet_category_id;
    private String outlet_category_price;
    private String slab_id;

    public String getSlab_id() {
        return slab_id;
    }

    public void setSlab_id(String slab_id) {
        this.slab_id = slab_id;
    }

    public String getMin_quantity() {
        return min_quantity;
    }

    public void setMin_quantity(String min_quantity) {
        this.min_quantity = min_quantity;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getSpecial_group_id() {
        return special_group_id;
    }

    public void setSpecial_group_id(String special_group_id) {
        this.special_group_id = special_group_id;
    }

    public String getSpecial_price() {
        return special_price;
    }

    public void setSpecial_price(String special_price) {
        this.special_price = special_price;
    }

    public String getOutlet_category_id() {
        return outlet_category_id;
    }

    public void setOutlet_category_id(String outlet_category_id) {
        this.outlet_category_id = outlet_category_id;
    }

    public String getOutlet_category_price() {
        return outlet_category_price;
    }

    public void setOutlet_category_price(String outlet_category_price) {
        this.outlet_category_price = outlet_category_price;
    }

    public String getPriceSlabEffectiveDate() {
        return priceSlabEffectiveDate;
    }

    public void setPriceSlabEffectiveDate(String priceSlabEffectiveDate) {
        this.priceSlabEffectiveDate = priceSlabEffectiveDate;
    }

}
