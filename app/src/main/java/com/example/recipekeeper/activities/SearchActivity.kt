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
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.recipekeeper.R
import com.example.recipekeeper.adapters.RecipeAdapter
import com.example.recipekeeper.models.ApplicationViewModelFactory
import com.example.recipekeeper.models.SearchViewModel

class SearchActivity : AppCompatActivity() {
    private lateinit var viewModel: SearchViewModel
    private lateinit var recipeAdapter: RecipeAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val viewModelFactory = ApplicationViewModelFactory(application, SearchViewModel::class)
        viewModel = ViewModelProvider(this, viewModelFactory).get(SearchViewModel::class.java)

        val recyclerView: RecyclerView = findViewById(R.id.recyclerViewRecipeList)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // TODO: add some info when no recipes are found
        recipeAdapter = RecipeAdapter() { recipe ->
            val intent = Intent(this, RecipeActivity::class.java).apply {
                putExtra(getString(R.string.extra_recipe_id), recipe.id)
            }
            startActivity(intent)
        }
        recyclerView.adapter = recipeAdapter

        viewModel.recipes.observe(this, Observer { recipes ->
            recipes?.let { recipeAdapter.submitList(it) }
        })

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
                viewModel.addQuery(ingredient)
                displayTags(tagsContainer)
                viewModel.updateResults()
                ingredientEditText.text.clear()
            }
        }
    }

    private fun displayTags(tagsContainer: LinearLayout) {
        tagsContainer.removeAllViews()
        viewModel.queries.value?.forEach { ingredient ->
            val tagView = TextView(this).apply {
                text = ingredient
                setBackgroundColor(Color.LTGRAY)
                setPadding(8, 8, 8, 8)
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                setOnClickListener {
                    viewModel.removeQuery(ingredient)
                    displayTags(tagsContainer)
                    viewModel.updateResults()
                }
            }
            tagsContainer.addView(tagView)
        }
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