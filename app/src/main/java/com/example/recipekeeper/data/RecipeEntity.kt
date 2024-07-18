package com.example.recipekeeper.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters

@Entity(tableName = "recipes")
data class RecipeEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val url: String,
    @TypeConverters(Converters::class)
    val ingredients: ArrayList<String>,
    val instructions: String,
    val notes : String
)
