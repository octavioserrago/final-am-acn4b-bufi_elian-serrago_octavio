package com.project.nutritionintellij;

import java.util.HashMap;
import java.util.Map;

public class Food {
    private String name;
    private String imgUrl;
    private Double kcals;

    public Food(String name, String imgUrl, Double kcals) {
        this.name = name;
        this.imgUrl = imgUrl;
        this.kcals = kcals;
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

    public Double getKcals() {
        return kcals;
    }

    public void setKcals(Double kcals) {
        this.kcals = kcals;
    }
}