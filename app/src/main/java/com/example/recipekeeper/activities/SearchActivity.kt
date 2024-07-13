package com.example.recipekeeper.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.recipekeeper.R
import com.example.recipekeeper.adapters.RecipeAdapter
import com.example.recipekeeper.models.Recipe
import com.example.recipekeeper.utils.FileManager

class SearchActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val recyclerView: RecyclerView = findViewById(R.id.recyclerViewRecipeList)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val recipeData: ArrayList<Recipe> = FileManager.loadRecipes(this)

        // TODO: add some info when no recipes are found
        val adapter = RecipeAdapter(recipeData) { recipe ->
            val intent = Intent(this, RecipeActivity::class.java).apply {
                putExtra("RECIPE", recipe)
            }
            startActivity(intent)
        }
        recyclerView.adapter = adapter

        onBackPressedDispatcher.addCallback(this, object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val intent = Intent(this@SearchActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        })
    }
}