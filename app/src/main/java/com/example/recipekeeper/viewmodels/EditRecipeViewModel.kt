package com.example.recipekeeper.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.recipekeeper.repository.RecipeDao
import com.example.recipekeeper.repository.RecipeDatabase
import com.example.recipekeeper.repository.RecipeEntity
import com.example.recipekeeper.scraper.Scraper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EditRecipeViewModel(application: Application) : AndroidViewModel(application) {
    private val recipeDao: RecipeDao = RecipeDatabase.getDatabase(application).recipeDao()

    private val _name = MutableLiveData<String>()
    val name: LiveData<String> get() = _name
    private val _url = MutableLiveData<String>()
    val url: LiveData<String> get() = _url
    private val _items = MutableLiveData<ArrayList<String>>()
    val items: LiveData<ArrayList<String>> get() = _items
    private val _instructions = MutableLiveData<String>()
    val instructions: LiveData<String> get() = _instructions
    private val _notes = MutableLiveData<String>()
    val notes: LiveData<String> get() = _notes

    var editMode: Boolean = false
    var recipeID: Int? = null

    fun insertRecipe() {
        viewModelScope.launch {
            recipeDao.insert(
                RecipeEntity(
                    name = name.value ?: "",
                    url = url.value ?: "",
                    ingredients = items.value ?: ArrayList(),
                    instructions = instructions.value ?: "",
                    notes = notes.value ?: ""
                )
            )
        }
    }

    fun updateRecipe() {
        viewModelScope.launch {
            if (recipeID != null) {
                recipeDao.update(
                    RecipeEntity(
                        id = recipeID!!,
                        name = name.value ?: "",
                        url = url.value ?: "",
                        ingredients = items.value ?: ArrayList(),
                        instructions = instructions.value ?: "",
                        notes = notes.value ?: ""
                    )
                )
            }
        }
    }

    fun addItem(item: String) {
        val currentItems = _items.value ?: ArrayList()
        currentItems.add(item)
        _items.value = currentItems
    }

    fun editItem(position: Int, newItem: String) {
        val currentItems = _items.value
        if (currentItems != null && position < currentItems.size) {
            currentItems[position] = newItem
            _items.value = ArrayList(currentItems) // Trigger LiveData update
        }
    }

    fun removeItem(position: Int) {
        val currentItems = _items.value ?: ArrayList()
        currentItems.removeAt(position)
        _items.value = currentItems
    }

    fun removeSelected(indexes: List<Int>) {
        val currentItems = _items.value
        val newItems = ArrayList<String>()
        if (currentItems != null) {
            for (i in 0..<currentItems.size) {
                if (!indexes.contains(i)) {
                    newItems.add(currentItems[i])
                }
            }
        }
        _items.value = newItems
    }

    @Throws(Exception::class)
    fun importURL(importUrl: String) {
        val scraper: Scraper
        try {
            scraper = Scraper(importUrl)
        } catch (e: Exception) {
            throw Exception("Failed to connect", e)
        }

        viewModelScope.launch {
            withContext(Dispatchers.Main) {
                _name.value = scraper.name
                _url.value = importUrl
                _items.value = scraper.ingredientsList
                _instructions.value = scraper.instructions
                _notes.value = scraper.notes
            }
        }
    }

    fun pasteIngredients(ingredientsString: String) {
        _items.value = ingredientsString.split("\n").toCollection(ArrayList())
    }

    fun loadRecipe(id: Int) = viewModelScope.launch {
        editMode = true
        val fetchedRecipe = recipeDao.getRecipeById(id)
        recipeID = fetchedRecipe?.id
        _name.value = fetchedRecipe?.name
        _url.value = fetchedRecipe?.url
        _instructions.value = fetchedRecipe?.instructions
        _notes.value = fetchedRecipe?.notes
        _items.value = fetchedRecipe?.ingredients
    }

    fun setName(newName: String) {
        _name.value = newName
    }

    fun setUrl(newUrl: String) {
        _url.value = newUrl
    }

    fun setInstructions(newInstructions: String) {
        _instructions.value = newInstructions
    }

    fun setNotes(newNotes: String) {
        _notes.value = newNotes
    }
}