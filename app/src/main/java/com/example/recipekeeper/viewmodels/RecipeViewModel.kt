package com.example.recipekeeper.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.recipekeeper.repository.RecipeDao
import com.example.recipekeeper.repository.RecipeDatabase
import com.example.recipekeeper.repository.RecipeEntity
import kotlinx.coroutines.launch

class RecipeViewModel(application: Application) : AndroidViewModel(application) {
    private val recipeDao: RecipeDao = RecipeDatabase.getDatabase(application).recipeDao()
    private val _recipe = MutableLiveData<RecipeEntity?>()
    val recipe: MutableLiveData<RecipeEntity?> get() = _recipe

    fun getRecipe(id: Int) = viewModelScope.launch {
        val fetchedRecipe = recipeDao.getRecipeById(id)
        _recipe.postValue(fetchedRecipe)
    }

    fun deleteRecipe() = viewModelScope.launch {
        if (recipe.value != null) {
            recipeDao.delete(recipe.value!!)
        }
    }
}