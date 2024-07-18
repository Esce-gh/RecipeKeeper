package com.example.recipekeeper.activities

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.widget.Toolbar
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.example.recipekeeper.R
import com.example.recipekeeper.adapters.EditPagerAdapter
import com.example.recipekeeper.models.EditRecipeViewModel
import com.example.recipekeeper.models.EditRecipeViewModelFactory
import com.example.recipekeeper.models.Recipe
import com.example.recipekeeper.utils.ToolbarUtil
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import java.util.concurrent.Executors

class EditActivity : AppCompatActivity() {
    private lateinit var viewModel: EditRecipeViewModel
    private val myExecutor = Executors.newSingleThreadExecutor()
    private val myHandler = Handler(Looper.getMainLooper())

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)

        val viewModelFactory = EditRecipeViewModelFactory(application)
        try {
            viewModel = ViewModelProvider(this, viewModelFactory).get(EditRecipeViewModel::class.java)
        } catch (e: Exception){
            e.printStackTrace()
        }

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        ToolbarUtil.InitializeToolbar(this, toolbar, getString(R.string.edit_recipe_title))

        val viewPager: ViewPager2 = findViewById(R.id.viewPager)
        val tabLayout: TabLayout = findViewById(R.id.tabLayout)
        val pagerAdapter = EditPagerAdapter(this)
        viewPager.adapter = pagerAdapter
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> getString(R.string.edit_pager_details)
                1 -> getString(R.string.edit_pager_ingredients)
                2 -> getString(R.string.edit_pager_instructions)
                3 -> getString(R.string.edit_pager_notes)
                else -> ""
            }
        }.attach()

        val editMode = intent.getBooleanExtra(getString(R.string.extra_edit_mode), false)
        if (editMode) {
            val recipe = intent.getSerializableExtra(getString(R.string.extra_recipe), Recipe::class.java)
            viewModel.loadRecipe(recipe ?: Recipe())
        }
    }

    private fun showImportDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_import_url, null)
        val editTextURL: EditText = dialogView.findViewById(R.id.editTextURL)
        val buttonCancel: Button = dialogView.findViewById(R.id.buttonCancel)
        val buttonOK: Button = dialogView.findViewById(R.id.buttonOK)

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        buttonCancel.setOnClickListener {
            dialog.dismiss()
        }

        buttonOK.setOnClickListener {
            val url = editTextURL.text.toString()
            myExecutor.execute {
                viewModel.importURL(url)
                myHandler.post {
                    viewModel.refreshValues()
                    dialog.dismiss()
                }
            }
        }

        dialog.show()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.edit_recipe_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.actionImport -> {
                showImportDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}