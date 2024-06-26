package com.project.nutritionintellij;

import java.util.HashMap;
import java.util.Map;

public class Food {

    private String name;
    private String imgUrl;

    // Constructor
    public Food(String name, String imgUrl) {
        this.name = name;
        this.imgUrl = imgUrl;
    }

    // Getter methods
    public String getName() {
        return name;
    }

    public String getImgUrl() {
        return imgUrl;
    }


}
