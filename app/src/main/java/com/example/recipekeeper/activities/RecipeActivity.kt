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
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.example.recipekeeper.R
import com.example.recipekeeper.adapters.RecipePagerAdapter
import com.example.recipekeeper.data.RecipeEntity
import com.example.recipekeeper.models.ApplicationViewModelFactory
import com.example.recipekeeper.models.RecipeViewModel
import com.example.recipekeeper.models.SearchViewModel
import com.example.recipekeeper.utils.Redirect
import com.example.recipekeeper.utils.ToolbarUtil
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class RecipeActivity : AppCompatActivity() {
    private lateinit var viewModel: RecipeViewModel

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe)

        val viewModelFactory = ApplicationViewModelFactory(application, RecipeViewModel::class)
        viewModel = ViewModelProvider(this, viewModelFactory).get(RecipeViewModel::class.java)

        val recipeId = intent.getIntExtra(getString(R.string.extra_recipe_id), -1)
        if (recipeId != -1) {
            viewModel.getRecipe(recipeId)
        }
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        ToolbarUtil.InitializeToolbar(this, toolbar, viewModel.recipe.value?.name ?: "")

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
                    putExtra(getString(R.string.extra_recipe_id), viewModel.recipe.value?.id)
                    putExtra(getString(R.string.extra_edit_mode), true)
                }
                startActivity(intent)
                true
            }

            R.id.action_open -> {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(viewModel.recipe.value?.url ?: "")
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        super.onPrepareOptionsMenu(menu)
        if (viewModel.recipe.value?.url?.trim() == "") {
            val urlMenuItem = menu?.findItem(R.id.action_open)
            urlMenuItem?.isVisible = false
        }
        return true
    }

    private fun deleteConfirmation() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Are you sure you want to delete this recipe?")
            .setPositiveButton("Delete") { dialog, id ->
                viewModel.deleteRecipe()
                Redirect.redirect(this, SearchActivity::class.java)
            }
            .setNegativeButton("Cancel") { dialog, id ->
                dialog.dismiss()
            }
        builder.create().show()
    }
}