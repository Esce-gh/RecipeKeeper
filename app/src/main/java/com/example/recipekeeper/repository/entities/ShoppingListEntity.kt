package com.example.recipekeeper.repository.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "shopping_list")
data class ShoppingListEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val ingredient: String
)
