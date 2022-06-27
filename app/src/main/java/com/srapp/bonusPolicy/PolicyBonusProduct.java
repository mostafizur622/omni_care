package com.srapp.bonusPolicy;

import java.io.Serializable;

public class PolicyBonusProduct implements Serializable {

    private String policy_id;
    private String option_id;
    private String bonus_product_id;
    private String bonus_qty;
    private String unit_id;
    private String product_name;
    private String unit_name;

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public String getUnit_name() {
        return unit_name;
    }

    public void setUnit_name(String unit_name) {
        this.unit_name = unit_name;
    }

    public String getPolicy_id() {
        return policy_id;
    }

    public void setPolicy_id(String policy_id) {
        this.policy_id = policy_id;
    }

    public String getOption_id() {
        return option_id;
    }

    public void setOption_id(String option_id) {
        this.option_id = option_id;
    }

    public String getBonus_product_id() {
        return bonus_product_id;
    }

    public void setBonus_product_id(String bonus_product_id) {
        this.bonus_product_id = bonus_product_id;
    }

    public String getBonus_qty() {
        return bonus_qty;
    }

    public void setBonus_qty(String bonus_qty) {
        this.bonus_qty = bonus_qty;
    }

    public String getUnit_id() {
        return unit_id;
    }

    public void setUnit_id(String unit_id) {
        this.unit_id = unit_id;
    }

}
