package com.srapp.bonusPolicy;

public class ExclusionProduct {
    private String id;
    private String dis_bonus_policy_id;
    private String dis_bonus_policy_option_id;
    private String product_id;
    private String min_qty;
    private String updated_at;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDis_bonus_policy_id() {
        return dis_bonus_policy_id;
    }

    public void setDis_bonus_policy_id(String dis_bonus_policy_id) {
        this.dis_bonus_policy_id = dis_bonus_policy_id;
    }

    public String getDis_bonus_policy_option_id() {
        return dis_bonus_policy_option_id;
    }

    public void setDis_bonus_policy_option_id(String dis_bonus_policy_option_id) {
        this.dis_bonus_policy_option_id = dis_bonus_policy_option_id;
    }

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public String getMin_qty() {
        return min_qty;
    }

    public void setMin_qty(String min_qty) {
        this.min_qty = min_qty;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

}
