package com.srapp.pricing;

public class CombinationPrices {
    private String _id;
    private String effective_date;
    private String combinedQty;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getEffective_date() {
        return effective_date;
    }

    public void setEffective_date(String effective_date) {
        this.effective_date = effective_date;
    }

    public String getCombinedQty() {
        return combinedQty;
    }

    public void setCombinedQty(String combinedQty) {
        this.combinedQty = combinedQty;
    }

}
