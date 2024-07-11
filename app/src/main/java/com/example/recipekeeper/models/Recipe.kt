package com.example.recipekeeper.models

import com.example.recipekeeper.scraper.Ingredient
import java.io.Serializable

data class Recipe(val title: String, val url: String, val ingredients: ArrayList<Ingredient>) : Serializable {
}