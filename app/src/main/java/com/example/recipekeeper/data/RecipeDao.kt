package com.example.recipekeeper.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface RecipeDao {
    @Insert
    suspend fun insert(recipe: RecipeEntity)

    @Update
    suspend fun update(recipe: RecipeEntity)

    @Delete
    suspend fun delete(recipe: RecipeEntity)

    @Query("SELECT * FROM recipes WHERE id = :id")
    suspend fun getRecipeById(id: Int): RecipeEntity?

    @Query("SELECT * FROM recipes")
    fun getAllRecipes(): LiveData<List<RecipeEntity>>
}