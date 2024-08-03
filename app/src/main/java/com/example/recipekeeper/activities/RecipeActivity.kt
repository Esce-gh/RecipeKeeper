package com.example.recipekeeper.activities

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.example.recipekeeper.R
import com.example.recipekeeper.adapters.RecipePagerAdapter
import com.example.recipekeeper.utils.ApplicationViewModelFactory
import com.example.recipekeeper.utils.Redirect
import com.example.recipekeeper.utils.ToolbarUtil
import com.example.recipekeeper.viewmodels.RecipeViewModel
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class RecipeActivity : AppCompatActivity() {
    private lateinit var viewModel: RecipeViewModel

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe)

        val viewModelFactory = ApplicationViewModelFactory(application, RecipeViewModel::class)
        viewModel = ViewModelProvider(this, viewModelFactory)[RecipeViewModel::class.java]

        val recipeId = intent.getIntExtra(getString(R.string.extra_recipe_id), -1)
        if (recipeId != -1) {
            viewModel.getRecipe(recipeId)
        }
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        ToolbarUtil.InitializeToolbar(this, toolbar, "")
        toolbar.overflowIcon = ContextCompat.getDrawable(this, R.drawable.ic_more)

        viewModel.recipe.observe(this) { recipe ->
            toolbar.setTitle(recipe?.name)
        }

        val viewPager: ViewPager2 = findViewById(R.id.viewPager)
        val tabLayout: TabLayout = findViewById(R.id.tabLayout)
        val pagerAdapter = RecipePagerAdapter(this)
        viewPager.adapter = pagerAdapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> getString(R.string.pager_ingredients)
                1 -> getString(R.string.pager_instructions)
                2 -> getString(R.string.pager_notes)
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
        builder.setMessage(getString(R.string.dialog_recipe_removal_confirmation))
            .setPositiveButton(getString(R.string.text_remove)) { _, _ ->
                viewModel.deleteRecipe()
                Redirect.redirect(this, SearchActivity::class.java)
            }
            .setNegativeButton(getString(R.string.text_cancel)) { dialog, _ ->
                dialog.dismiss()
            }
        builder.create().show()
    }
}