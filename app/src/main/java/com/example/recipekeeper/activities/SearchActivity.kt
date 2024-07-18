package com.example.recipekeeper.activities

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.recipekeeper.R
import com.example.recipekeeper.adapters.RecipeAdapter
import com.example.recipekeeper.models.Recipe
import com.example.recipekeeper.utils.FileManager

class SearchActivity : AppCompatActivity() {
    private val queries = mutableListOf<String>()
    private lateinit var recipeAdapter: RecipeAdapter
    private lateinit var recipeData: ArrayList<Recipe>
    private lateinit var recipeAdapterData: ArrayList<Recipe>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        recipeData = FileManager.loadRecipes(this)
        recipeAdapterData = ArrayList(recipeData)

        val recyclerView: RecyclerView = findViewById(R.id.recyclerViewRecipeList)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // TODO: add some info when no recipes are found
        recipeAdapter = RecipeAdapter(recipeAdapterData) { recipe ->
            val intent = Intent(this, RecipeActivity::class.java).apply {
                putExtra("RECIPE", recipe)
            }
            startActivity(intent)
        }
        recyclerView.adapter = recipeAdapter

        overrideBackButton()
        initializeTagSearch()
    }

    private fun initializeTagSearch() {
        val ingredientEditText = findViewById<EditText>(R.id.ingredientEditText)
        val addTagButton = findViewById<Button>(R.id.addTagButton)
        val tagsContainer = findViewById<LinearLayout>(R.id.tagsContainer)
        addTagButton.setOnClickListener {
            val ingredient = ingredientEditText.text.toString().trim()
            if (ingredient.isNotEmpty()) {
                queries.add(ingredient)
                displayTags(tagsContainer)
                updateSearchResults()
                ingredientEditText.text.clear()
            }
        }
    }

    private fun displayTags(tagsContainer: LinearLayout) {
        tagsContainer.removeAllViews()
        queries.forEach { ingredient ->
            val tagView = TextView(this).apply {
                text = ingredient
                setBackgroundColor(Color.LTGRAY)
                setPadding(8, 8, 8, 8)
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                setOnClickListener {
                    queries.remove(ingredient)
                    displayTags(tagsContainer)
                    updateSearchResults()
                }
            }
            tagsContainer.addView(tagView)
        }
    }

    private fun updateSearchResults() {
        val matchingRecipes = recipeData.filter { recipe ->
            recipe.ingredients.any { ingredient ->
                queries.all { query ->
                    ingredient.contains(query, ignoreCase = true) || recipe.name.contains(query, ignoreCase = true)
                }
            }
        }
        recipeAdapter.updateRecipes(matchingRecipes as ArrayList<Recipe>)
    }

    private fun overrideBackButton() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val intent = Intent(this@SearchActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        })
    }
}