package com.example.recipekeeper.scraper;

import java.io.Serializable;
import java.util.ArrayList;

public class IngredientsGroup implements Serializable
{
    public String name;
    public ArrayList<String> ingredients = new ArrayList<>();

    public IngredientsGroup(String name)
    {
        this.name = name;
    }

    public IngredientsGroup(String name, ArrayList<String> ingredients) {
        this.name = name;
        this.ingredients = ingredients;
    }

    public ArrayList<String> getIngredients()
    {
        return ingredients;
    }

    public void addIngredient(String i)
    {
        ingredients.add(i);
    }

    public void addIngredient(ArrayList<String> items) {
        ingredients.addAll(items);
    }
}
