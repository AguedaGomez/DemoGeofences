package com.ssii.demogeofences2.Objects;

/**
 * Created by Ague on 08/08/2018.
 */

public class Concept {
    private String name;
    private String image;
    private String category;

    public Concept(String name, String image, String category) {
        this.name = name;
        this.image = image;
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
