package com.example.recipekeeper.activities

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.recipekeeper.R
import com.example.recipekeeper.adapters.ItemAdapter
import com.example.recipekeeper.scraper.Ingredient

class RecipeActivity : AppCompatActivity( ){
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe)

        val title = intent.getStringExtra("TITLE") ?: ""
        val url = intent.getStringExtra("URL") ?: ""
        val items = ArrayList<Ingredient>()

        val data = intent.getSerializableExtra("DATA", ArrayList::class.java)
        if (data != null) {
            for (a in data) {
                items.add(a as Ingredient)
            }
        }

        val textViewTitle: TextView = findViewById(R.id.textViewTitle)
        textViewTitle.text = title
        val buttonLink: Button = findViewById(R.id.buttonLink)
        buttonLink.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            startActivity(intent)
        }

        val recyclerView: RecyclerView = findViewById(R.id.recyclerViewIngredients)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = ItemAdapter(items)
    }
}