package com.example.recipekeeper.models

import android.app.Application
import android.widget.ArrayAdapter
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.example.recipekeeper.data.RecipeDao
import com.example.recipekeeper.data.RecipeDatabase
import com.example.recipekeeper.data.RecipeEntity

class SearchViewModel(application: Application) : AndroidViewModel(application) {
    private val recipeDao: RecipeDao = RecipeDatabase.getDatabase(application).recipeDao()

    val allRecipes: LiveData<List<RecipeEntity>> = recipeDao.getAllRecipes()
    private val _recipes = MutableLiveData<List<RecipeEntity>>()
    val recipes: LiveData<List<RecipeEntity>> get() = _recipes
    private val _queries = MutableLiveData<ArrayList<String>>()
    val queries: LiveData<ArrayList<String>> get() = _queries

    init {
        allRecipes.observeForever(object : Observer<List<RecipeEntity>> {
            override fun onChanged(value: List<RecipeEntity>) {
                _recipes.value = value
                allRecipes.removeObserver(this)
            }
        })
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
                    recipe.name.contains(query, ignoreCase = true
                    ) || recipe.ingredients.any { ingredient ->
                        ingredient.contains(query, ignoreCase = true)
                    }
                }
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