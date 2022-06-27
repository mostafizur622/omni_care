package com.srapp.Model;

import android.widget.ImageView;

public class SR_Target {
    String name;
    ImageView image;

    public SR_Target(String name, ImageView image) {
        this.name = name;
        this.image = image;
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

    public void setImage(ImageView image) {
        this.image = image;
    }
}
