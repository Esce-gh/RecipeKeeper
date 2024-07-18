package com.example.recipekeeper.activities

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.viewpager2.widget.ViewPager2
import com.example.recipekeeper.R
import com.example.recipekeeper.adapters.RecipePagerAdapter
import com.example.recipekeeper.models.EditRecipeViewModel
import com.example.recipekeeper.models.Recipe
import com.example.recipekeeper.models.RecipeViewModel
import com.example.recipekeeper.scraper.Ingredient
import com.example.recipekeeper.utils.FileManager
import com.example.recipekeeper.utils.Redirect
import com.example.recipekeeper.utils.ToolbarUtil
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class RecipeActivity : AppCompatActivity() {
    private val viewModel: RecipeViewModel by viewModels()

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe)

        val recipe = intent.getSerializableExtra("RECIPE", Recipe::class.java)
        viewModel.recipe = recipe ?: Recipe()

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        ToolbarUtil.InitializeToolbar(this, toolbar, viewModel.recipe.name)

        val viewPager: ViewPager2 = findViewById(R.id.viewPager)
        val tabLayout: TabLayout = findViewById(R.id.tabLayout)
        val pagerAdapter = RecipePagerAdapter(this)
        viewPager.adapter = pagerAdapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Ingredients"
                1 -> "Instructions"
                2 -> "Notes"
                else -> ""
            }
        }.attach()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.recipe_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_delete -> {
                deleteConfirmation()
                true
            }

            R.id.action_edit -> {
                val intent = Intent(this, EditActivity::class.java).apply {
                    putExtra("RECIPE", viewModel.recipe)
                    putExtra("EDIT", true)
                }
                startActivity(intent)
                true
            }

            R.id.action_open -> {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(viewModel.recipe.url)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        super.onPrepareOptionsMenu(menu)
        if (viewModel.recipe.url.trim() == "") {
            val urlMenuItem = menu?.findItem(R.id.action_open)
            urlMenuItem?.isVisible = false
        }
        return true
    }

    private fun deleteConfirmation() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Are you sure you want to delete this recipe?")
            .setPositiveButton("Delete") { dialog, id ->
                FileManager.deleteRecipe(this, viewModel.recipe.name)
                Redirect.redirect(this, SearchActivity::class.java)
            }
            .setNegativeButton("Cancel") { dialog, id ->
                dialog.dismiss()
            }
        builder.create().show()
    }
}