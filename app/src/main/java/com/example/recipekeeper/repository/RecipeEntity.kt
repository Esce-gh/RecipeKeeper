package com.example.recipekeeper.repository

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.recipekeeper.scraper.IngredientsGroup
import java.io.Serializable

@Entity(tableName = "recipes")
data class RecipeEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val url: String,
    @TypeConverters(Converters::class)
    val ingredients: ArrayList<IngredientsGroup>,
    val instructions: String,
    val notes : String
) : Serializable
