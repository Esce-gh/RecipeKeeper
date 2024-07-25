package com.example.recipekeeper.repository

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    @TypeConverter
    fun fromArrayList(value: ArrayList<String>?): String {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun toArrayList(value: String?): ArrayList<String>? {
        return Gson().fromJson(value, object : TypeToken<ArrayList<String>>() {}.type)
    }
}