package com.example.recipekeeper.scraper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class IngredientsGroup implements Serializable
{
    public final String name;
    public ArrayList<String> ingredients = new ArrayList<>();

    public IngredientsGroup(String name)
    {
        this.name = name;
    }

    public IngredientsGroup(String name, ArrayList<String> ingredients) {
        this.name = name;
        this.ingredients = ingredients;
    }

    public String getName()
    {
        return name;
    }

    public ArrayList<String> getIngredients()
    {
        return ingredients;
    }

    public void addIngredient(String i)
    {
        ingredients.add(i);
    }
}
