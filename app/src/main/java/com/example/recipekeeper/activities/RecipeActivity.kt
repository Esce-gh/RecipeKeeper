package com.example.recipekeeper.activities

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.recipekeeper.R
import com.example.recipekeeper.adapters.ItemAdapter
import com.example.recipekeeper.models.Recipe
import com.example.recipekeeper.scraper.Ingredient
import com.example.recipekeeper.utils.FileManager
import com.example.recipekeeper.utils.Redirect
import java.util.ArrayList

class RecipeActivity : AppCompatActivity( ){
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe)

        val recipe = intent.getSerializableExtra("RECIPE", Recipe::class.java)
        val name = recipe?.name ?: ""
        val url = recipe?.url ?: ""
        val items = recipe?.ingredients ?: ArrayList()

        val textViewName: TextView = findViewById(R.id.textViewName)
        textViewName.text = name
        val buttonLink: Button = findViewById(R.id.buttonLink)
        if (url.trim() == "") {
            buttonLink.visibility = View.INVISIBLE
        }
        buttonLink.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            startActivity(intent)
        }

        val recyclerView: RecyclerView = findViewById(R.id.recyclerViewIngredients)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = ItemAdapter(items)

        val buttonRemove: Button = findViewById(R.id.buttonRemove)
        buttonRemove.setOnClickListener {
            FileManager.deleteRecipe(this, name)
            Redirect.redirect(this, SearchActivity::class.java)
        }

        val buttonEdit: Button = findViewById(R.id.buttonEdit)
        buttonEdit.setOnClickListener {
            val intent = Intent(this, EditActivity::class.java).apply {
                putExtra("DATA", items)
                putExtra("NAME", name)
                putExtra("URL", url)
                putExtra("EDIT", true)
            }
            startActivity(intent)
        }
    }
}