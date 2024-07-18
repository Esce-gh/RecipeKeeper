package com.example.recipekeeper.models

import com.example.recipekeeper.scraper.Ingredient
import java.io.Serializable

data class Recipe(val name: String, val url: String, val ingredients: ArrayList<Ingredient>, val instructions: String, val notes: String) : Serializable {
    constructor() : this("", "", ArrayList(), "","")
}