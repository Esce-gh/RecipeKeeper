package com.example.recipekeeper.models

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.recipekeeper.data.RecipeDao
import com.example.recipekeeper.data.RecipeDatabase
import com.example.recipekeeper.data.RecipeEntity
import kotlinx.coroutines.launch

class RecipeViewModel(application: Application) : AndroidViewModel(application) {
    private val recipeDao: RecipeDao = RecipeDatabase.getDatabase(application).recipeDao()
    private val _recipe = MutableLiveData<RecipeEntity?>()
    val recipe: MutableLiveData<RecipeEntity?> get() = _recipe

    fun getRecipe(id: Int) = viewModelScope.launch {
        val fetchedRecipe = recipeDao.getRecipeById(id)
        _recipe.postValue(fetchedRecipe)
    }
}