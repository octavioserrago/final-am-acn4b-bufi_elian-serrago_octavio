package com.project.nutritionintellij;


import java.util.HashMap;
import java.util.Map;

public class Food {
    private String name;
    private String imgUrl;

    public Food(String name, String imgUrl) {
        this.name = name;
        this.imgUrl = imgUrl;
    }

    // Getters y Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }
}
