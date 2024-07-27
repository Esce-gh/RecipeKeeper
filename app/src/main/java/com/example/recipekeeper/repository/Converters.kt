package com.example.recipekeeper.repository

import androidx.room.TypeConverter
import com.example.recipekeeper.scraper.IngredientsGroup
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    // TODO: remove later
    @TypeConverter
    fun fromArrayList(value: ArrayList<String>?): String {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun toArrayList(value: String?): ArrayList<String>? {
        return Gson().fromJson(value, object : TypeToken<ArrayList<String>>() {}.type)
    }

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