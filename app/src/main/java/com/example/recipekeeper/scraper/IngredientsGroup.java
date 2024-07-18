package com.example.recipekeeper.scraper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class IngredientsGroup implements Serializable
{
    private final String name;
    private List<String> ingredients = new ArrayList<>();

    public IngredientsGroup(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }

    public List<String> getIngredients()
    {
        return ingredients;
    }

    public void addIngredient(String i)
    {
        ingredients.add(i);
    }
}
