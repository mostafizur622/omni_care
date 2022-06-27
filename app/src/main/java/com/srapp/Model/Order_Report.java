package com.srapp.Model;

import android.widget.ImageView;

public class Order_Report {
    public String srl_no;
    public String sale_date;
    public String memo;
    public String outlet;
    public String m_amount;
    public ImageView image;

    public Order_Report(String srl_no, String sale_date, String memo, String outlet, String m_amount, ImageView image) {
        this.srl_no = srl_no;
        this.sale_date = sale_date;
        this.memo = memo;
        this.outlet = outlet;
        this.m_amount = m_amount;
        this.image = image;
    }

    public String getSrl_no() {
        return srl_no;
    }

    public void setSrl_no(String srl_no) {
        this.srl_no = srl_no;
    }

    public String getSale_date() {
        return sale_date;
    }



    public void setSale_date(String sale_date) {
        this.sale_date = sale_date;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public String getOutlet() {
        return outlet;
    }

    public void setOutlet(String outlet) {
        this.outlet = outlet;
    }

    public String getM_amount() {
        return m_amount;
    }

    public void setM_amount(String m_amount) {
        this.m_amount = m_amount;
    }

    public ImageView getImage() {
        return image;
    }

    public void setImage(ImageView image) {
        this.image = image;
    }
}
