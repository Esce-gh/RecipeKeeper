package com.example.recipekeeper.repository.converters

import androidx.room.TypeConverter
import com.example.recipekeeper.scraper.IngredientsGroup
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    @TypeConverter
    fun fromIngredientGroupList(value: ArrayList<IngredientsGroup>?): String {
        val gson = Gson()
        val type = object : TypeToken<ArrayList<IngredientsGroup>>() {}.type
        return gson.toJson(value, type)
    }

    @TypeConverter
    fun toIngredientGroupList(value: String): ArrayList<IngredientsGroup> {
        val gson = Gson()
        val type = object : TypeToken<ArrayList<IngredientsGroup>>() {}.type
        return gson.fromJson(value, type)
    }
}