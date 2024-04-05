package com.srapp.Model;

import android.widget.ImageView;

public class SR_Target {
    String name;
    ImageView image;


    Boolean isVisible = true;
    public SR_Target(String name, ImageView image) {
        this.name = name;
        this.image = image;
    }

    public SR_Target(String name, ImageView image,Boolean isVisible) {
        this.name = name;
        this.image = image;
        this.isVisible=isVisible;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ImageView getImage() {
        return image;
    }

    public Boolean getVisible() {
        return isVisible;
    }

    public void setVisible(Boolean visible) {
        isVisible = visible;
    }

    public void setImage(ImageView image) {
        this.image = image;
    }
}
