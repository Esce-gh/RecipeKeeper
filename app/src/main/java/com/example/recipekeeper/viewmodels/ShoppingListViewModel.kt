package com.example.recipekeeper.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.recipekeeper.repository.RecipeDatabase
import com.example.recipekeeper.repository.dao.ShoppingListDao
import com.example.recipekeeper.repository.entities.ShoppingListEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ShoppingListViewModel(application: Application) : AndroidViewModel(application) {
    private val shoppingListDao: ShoppingListDao = RecipeDatabase.getDatabase(application).shoppingListDao()
    private val _items = MutableLiveData<ArrayList<ShoppingListEntity>>()
    val items: MutableLiveData<ArrayList<ShoppingListEntity>> get() = _items

    init {
        viewModelScope.launch(Dispatchers.IO) {
            val shoppingList = shoppingListDao.getAll()
            _items.postValue(shoppingList as ArrayList<ShoppingListEntity>)
        }
    }

    fun removeAllItems() {
        viewModelScope.launch {
            val items = _items.value
            items?.forEach { item ->
                shoppingListDao.delete(item)
            }
            val updatedShoppingList = shoppingListDao.getAll()
            _items.postValue(updatedShoppingList as ArrayList<ShoppingListEntity>)
        }
    }

    fun removeItems(items: List<ShoppingListEntity>) {
        viewModelScope.launch {
            items.forEach { item ->
                shoppingListDao.delete(item)
            }
            val updatedShoppingList = shoppingListDao.getAll()
            _items.postValue(updatedShoppingList as ArrayList<ShoppingListEntity>)
        }
    }
}