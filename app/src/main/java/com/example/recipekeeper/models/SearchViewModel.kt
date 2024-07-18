package com.example.recipekeeper.models

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.recipekeeper.data.RecipeDao
import com.example.recipekeeper.data.RecipeDatabase
import com.example.recipekeeper.data.RecipeEntity

class SearchViewModel(application: Application) : AndroidViewModel(application) {
    private val recipeDao: RecipeDao = RecipeDatabase.getDatabase(application).recipeDao()
    val recipes: LiveData<List<RecipeEntity>> = recipeDao.getAllRecipes()

}