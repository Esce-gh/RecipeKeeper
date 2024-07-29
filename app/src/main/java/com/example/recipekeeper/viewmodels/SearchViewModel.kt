package com.example.recipekeeper.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.recipekeeper.repository.RecipeDao
import com.example.recipekeeper.repository.RecipeDatabase
import com.example.recipekeeper.repository.RecipeEntity
import kotlinx.coroutines.launch

class SearchViewModel(application: Application) : AndroidViewModel(application) {
    private val recipeDao: RecipeDao = RecipeDatabase.getDatabase(application).recipeDao()

    var allRecipes: LiveData<List<RecipeEntity>> = recipeDao.getAllRecipes()
    private val _recipes = MutableLiveData<List<RecipeEntity>>()
    val recipes: LiveData<List<RecipeEntity>> get() = _recipes
    private val _queries = MutableLiveData<ArrayList<String>>()
    val queries: LiveData<ArrayList<String>> get() = _queries

    init {
        allRecipes.observeForever { value ->
            _recipes.value = value
            updateResults()
        }
    }

    fun addQuery(query: String) {
        val currentQueries = _queries.value ?: ArrayList()
        currentQueries.add(query)
        _queries.value = currentQueries
    }

    fun removeQuery(query: String) {
        val currentQueries = _queries.value ?: ArrayList()
        currentQueries.remove(query)
        _queries.value = currentQueries
    }

    fun updateResults() {
        if (queries.value != null && allRecipes.value != null) {
            _recipes.value = allRecipes.value!!.filter { recipe ->
                queries.value!!.all { query ->
                    recipe.name.contains(query, ignoreCase = true) ||
                            recipe.ingredients.any { group ->
                                group.ingredients.any { ingredient ->
                                    ingredient.contains(query, ignoreCase = true)
                                }
                            }
                }
            }
        }
    }

    fun removeRecipes(recipes: List<RecipeEntity>) {
        viewModelScope.launch {
            recipes.forEach { recipe ->
                recipeDao.delete(recipe)
            }
        }
    }

    fun sortName(ascending: Boolean) {
        if (ascending) {
            _recipes.value = _recipes.value?.sortedBy { it.name }
        } else {
            _recipes.value = _recipes.value?.sortedByDescending { it.name }
        }
    }

    fun sortId(ascending: Boolean) {
        if (ascending) {
            _recipes.value = _recipes.value?.sortedBy { it.id }
        } else {
            _recipes.value = _recipes.value?.sortedByDescending { it.id }
        }
    }
}