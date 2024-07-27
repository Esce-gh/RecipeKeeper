package com.example.recipekeeper.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.recipekeeper.repository.RecipeDao
import com.example.recipekeeper.repository.RecipeDatabase
import com.example.recipekeeper.repository.RecipeEntity
import com.example.recipekeeper.scraper.IngredientsGroup
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
    private val _items = MutableLiveData<ArrayList<IngredientsGroup>>()
    val items: LiveData<ArrayList<IngredientsGroup>> get() = _items
    private val _instructions = MutableLiveData<String>()
    val instructions: LiveData<String> get() = _instructions
    private val _notes = MutableLiveData<String>()
    val notes: LiveData<String> get() = _notes

    var editMode: Boolean = false
    var recipeID: Int? = null

    init {
        _items.value = arrayListOf(IngredientsGroup(""))
    }

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
        currentItems[0].addIngredient(item)
        _items.value = currentItems
    }

    fun editItem(position: Int, newItem: String) {
        val currentItems = _items.value ?: ArrayList()
        val currentGroup = currentItems[getGroupIndex(position)]
        currentGroup.ingredients[getIngredientIndex(position)] = newItem
        _items.value = currentItems
    }

    fun removeItem(position: Int) {
        val currentItems = _items.value ?: ArrayList()
        val currentGroup = currentItems[getGroupIndex(position)]
        currentGroup.ingredients.removeAt(getIngredientIndex(position))
        _items.value = currentItems
    }

//    fun removeSelected(indexes: List<Int>) {
//        val currentItems = _items.value
//        val newItems = ArrayList<String>()
//        if (currentItems != null) {
//            for (i in 0..<currentItems.size) {
//                if (!indexes.contains(i)) {
//                    newItems.add(currentItems[i])
//                }
//            }
//        }
//        _items.value = newItems
//    }

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
                _instructions.value = scraper.instructions
                _notes.value = scraper.notes
                val groups = scraper.ingredientsGroups
                if (!groups.any { group ->
                        group.name == ""
                    }) {
                    groups.add(0, IngredientsGroup(""))
                }
                _items.value = groups
            }
        }
    }

    fun pasteIngredients(ingredientsString: String) {
        val ingredients = ingredientsString.split("\n").toCollection(ArrayList())
        val group = arrayListOf(IngredientsGroup("", ingredients))
        _items.value = group
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

    fun getGroupIndex(position: Int): Int {
        var itemIndex = position
        val groups = _items.value
        if (groups != null) {
            for (i in groups.indices) {
                if (itemIndex == 0) return i
                itemIndex--
                if (itemIndex < groups[i].ingredients.size) return i
                itemIndex -= groups[i].ingredients.size
            }
        }
        return -1
    }

    fun getIngredientIndex(position: Int): Int {
        var count = 0
        val groups = _items.value
        if (groups != null) {
            for (group in groups) {
                count += 1
                if (position < count + group.ingredients.size) return position - count
                count += group.ingredients.size
            }
        }
        return -1
    }

    fun getIngredient(position: Int): String {
        val groups = _items.value
        if (groups != null) {
            return groups[getGroupIndex(position)].ingredients[getIngredientIndex(position)]
        }
        return ""
    }
}