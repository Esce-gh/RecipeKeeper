package com.example.recipekeeper.models

import java.io.Serializable

data class Recipe(val name: String, val url: String, val ingredients: ArrayList<String>, val instructions: String, val notes: String) : Serializable {
    constructor() : this("", "", ArrayList(), "","")
}