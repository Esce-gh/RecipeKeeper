package com.example.recipekeeper.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.recipekeeper.repository.RecipeDatabase
import com.example.recipekeeper.repository.dao.RecipeDao
import com.example.recipekeeper.repository.dao.ShoppingListDao
import com.example.recipekeeper.repository.entities.RecipeEntity
import com.example.recipekeeper.repository.entities.ShoppingListEntity
import kotlinx.coroutines.launch

class RecipeViewModel(application: Application) : AndroidViewModel(application) {
    private val database = RecipeDatabase.getDatabase(application)
    private val recipeDao: RecipeDao = database.recipeDao()
    private val shoppingListDao: ShoppingListDao = database.shoppingListDao()
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

    fun addShoppingList(items: ArrayList<String>) {
        viewModelScope.launch {
            items.forEach { item ->
                shoppingListDao.insert(ShoppingListEntity(ingredient = item))
            }
        }
    }
}