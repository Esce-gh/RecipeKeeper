package com.example.recipekeeper.models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.recipekeeper.scraper.Ingredient
import com.example.recipekeeper.scraper.Scraper

class EditRecipeViewModel : ViewModel() {
    private val _name = MutableLiveData<String>()
    val name: LiveData<String> get() = _name
    private val _url = MutableLiveData<String>()
    val url: LiveData<String> get() = _url
    private val _items = MutableLiveData<ArrayList<Ingredient>>()
    val items: LiveData<ArrayList<Ingredient>> get() = _items

    var scrapedItems: ArrayList<Ingredient> = ArrayList()
    var scrapedName: String = ""
    var scrapedUrl: String = ""
    var editMode: Boolean = false

    fun addItem(item: Ingredient) {
        val currentItems = _items.value ?: ArrayList()
        currentItems.add(item)
        _items.value = currentItems
    }

    fun editItem(position: Int, newName: String, newUnit: String, newAmount: String) {
        val currentItems = _items.value
        if (currentItems != null && position < currentItems.size) {
            currentItems[position].name = newName
            currentItems[position].unit = newUnit
            currentItems[position].amount = newAmount
            _items.value = ArrayList(currentItems) // Trigger LiveData update
        }
    }

    fun removeItem(position: Int) {
        val currentItems = _items.value ?: ArrayList()
        currentItems.removeAt(position)
        _items.value = currentItems
    }

    fun refreshValues() {
        _name.value = scrapedName
        _url.value = scrapedUrl
        _items.value = scrapedItems
    }

    fun setName(newName: String) {
        _name.value = newName
    }

    fun setUrl(newUrl: String) {
        _url.value = newUrl
    }

    fun importURL(importUrl: String) {
        var scraper: Scraper? = null
        try {
            scraper = Scraper(importUrl)
        } catch (e: Exception) {
            // TODO: error "failed to connect"
            e.printStackTrace()
        }
        if (scraper != null) {
            scrapedItems = scraper.ingredientsList
            scrapedName = scraper.name
            scrapedUrl = importUrl
        }
    }
}