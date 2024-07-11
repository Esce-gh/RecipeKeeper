package com.example.recipekeeper.scraper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class IngredientsGroup implements Serializable
{
    private final String name;
    private List<Ingredient> ingredients = new ArrayList<>();

    public IngredientsGroup(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }

    public List<Ingredient> getIngredients()
    {
        return ingredients;
    }

    public void addIngredient(Ingredient i)
    {
        ingredients.add(i);
    }
}
