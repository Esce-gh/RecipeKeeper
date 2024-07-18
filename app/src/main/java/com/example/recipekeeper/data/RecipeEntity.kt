package com.example.recipekeeper.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import java.io.Serializable

@Entity(tableName = "recipes")
data class RecipeEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    var name: String,
    var url: String,
    @TypeConverters(Converters::class)
    val ingredients: ArrayList<String>,
    var instructions: String,
    var notes : String
) : Serializable {
    constructor() : this(-1, "", "", ArrayList(), "","")
}
