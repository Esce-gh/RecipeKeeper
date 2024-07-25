package com.example.recipekeeper.activities

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.recipekeeper.R
import com.example.recipekeeper.adapters.RecipeAdapter
import com.example.recipekeeper.viewmodels.ApplicationViewModelFactory
import com.example.recipekeeper.viewmodels.SearchViewModel
import com.example.recipekeeper.utils.ToolbarUtil
import com.google.android.flexbox.FlexboxLayout

class SearchActivity : AppCompatActivity() {
    private lateinit var viewModel: SearchViewModel
    private lateinit var recipeAdapter: RecipeAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val viewModelFactory = ApplicationViewModelFactory(application, SearchViewModel::class)
        viewModel = ViewModelProvider(this, viewModelFactory)[SearchViewModel::class.java]

        val recyclerView: RecyclerView = findViewById(R.id.recyclerViewRecipeList)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        ToolbarUtil.InitializeToolbar(this, toolbar, getString(R.string.find_recipe_title))

        // TODO: add some info when no recipes are found
        recipeAdapter = RecipeAdapter { recipe ->
            val intent = Intent(this, RecipeActivity::class.java).apply {
                putExtra(getString(R.string.extra_recipe_id), recipe.id)
            }
            startActivity(intent)
        }
        recyclerView.adapter = recipeAdapter

        viewModel.recipes.observe(this) { recipes ->
            recipes?.let { recipeAdapter.submitList(it) }
        }

        overrideBackButton()
        initializeTagSearch()
    }

    private fun initializeTagSearch() {
        val ingredientEditText = findViewById<EditText>(R.id.editTextToolbar)
        val tagsContainer = findViewById<FlexboxLayout>(R.id.tagsContainer)
        ingredientEditText.setOnEditorActionListener { view, actionId, keyEvent ->
            if (actionId == EditorInfo.IME_ACTION_DONE || keyEvent?.keyCode == KeyEvent.KEYCODE_ENTER) {
                addTag(ingredientEditText)
                displayTags(tagsContainer)
            }
            return@setOnEditorActionListener true
        }
    }

    private fun addTag(editText: EditText) {
        val ingredient = editText.text.toString().trim()
        if (ingredient.isNotEmpty()) {
            viewModel.addQuery(ingredient)
            viewModel.updateResults()
            editText.text.clear()
        }
    }

    private fun displayTags(tagsContainer: FlexboxLayout) {
        tagsContainer.removeAllViews()
        viewModel.queries.value?.forEach { ingredient ->
            val tagView = TextView(this).apply {
                text = ingredient
                setBackgroundColor(Color.LTGRAY)
                setPadding(8, 8, 8, 8)
                layoutParams = FlexboxLayout.LayoutParams(
                    FlexboxLayout.LayoutParams.WRAP_CONTENT,
                    FlexboxLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(8, 8, 8, 8)
                }
                setOnClickListener {
                    viewModel.removeQuery(ingredient)
                    displayTags(tagsContainer)
                    viewModel.updateResults()
                }
            }
            tagsContainer.addView(tagView)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.sort_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.sort_name_asc -> {
                viewModel.sortName(true)
                true
            }
            R.id.sort_name_desc -> {
                viewModel.sortName(false)
                true
            }
            R.id.sort_date_new -> {
                viewModel.sortId(true)
                true
            }
            R.id.sort_date_old -> {
                viewModel.sortId(false)
                true
            }
            else -> super.onOptionsItemSelected(item)
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