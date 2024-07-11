package com.example.recipekeeper.scraper;

import java.io.Serializable;

public class Ingredient implements Serializable {
    private String amount;
    private String unit;
    private String name;

    public Ingredient(String amount, String unit, String name) {
        this.amount = amount;
        this.unit = unit;
        this.name = name;
    }

    public Ingredient() {
        amount = "";
        unit = "";
        name = "";
    }

    public String getAmount() {
        return amount;
    }

    public String getUnit() {
        return unit;
    }

    public String getName() {
        return name;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public void setName(String name) {
        this.name = name;
    }
}
