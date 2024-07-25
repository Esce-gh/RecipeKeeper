package com.example.recipekeeper.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlin.reflect.KClass

class ApplicationViewModelFactory(private val application: Application, private val viewModelClass: KClass<out AndroidViewModel>) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(viewModelClass.java)) {
            @Suppress("UNCHECKED_CAST")
            return viewModelClass.java.getConstructor(Application::class.java).newInstance(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}