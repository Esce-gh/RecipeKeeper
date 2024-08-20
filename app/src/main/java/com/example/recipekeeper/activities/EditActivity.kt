package com.example.recipekeeper.activities

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.example.recipekeeper.R
import com.example.recipekeeper.adapters.EditPagerAdapter
import com.example.recipekeeper.scraper.FailedToConnectException
import com.example.recipekeeper.scraper.WebsiteNotSupportedException
import com.example.recipekeeper.utils.ApplicationViewModelFactory
import com.example.recipekeeper.utils.Redirect
import com.example.recipekeeper.utils.ToolbarUtil
import com.example.recipekeeper.viewmodels.EditRecipeViewModel
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import java.util.concurrent.Executors

class EditActivity : AppCompatActivity() {
    private lateinit var viewModel: EditRecipeViewModel
    private lateinit var adapter: EditPagerAdapter
    private val executor = Executors.newSingleThreadExecutor()
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)

        val viewModelFactory = ApplicationViewModelFactory(application, EditRecipeViewModel::class)
        try {
            viewModel = ViewModelProvider(this, viewModelFactory)[EditRecipeViewModel::class.java]
        } catch (e: Exception){
            Log.e(getString(R.string.error_tag), "${e.message}", e)
        }

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        ToolbarUtil.InitializeToolbar(this, toolbar, getString(R.string.edit_recipe_title))

        val viewPager: ViewPager2 = findViewById(R.id.viewPager)
        val tabLayout: TabLayout = findViewById(R.id.tabLayout)
        adapter = EditPagerAdapter(this)
        viewPager.adapter = adapter
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> getString(R.string.pager_details)
                1 -> getString(R.string.pager_ingredients)
                2 -> getString(R.string.pager_instructions)
                3 -> getString(R.string.pager_notes)
                else -> ""
            }
        }.attach()

        val editMode = intent.getBooleanExtra(getString(R.string.extra_edit_mode), false)
        if (editMode) {
            val recipeID = intent.getIntExtra(getString(R.string.extra_recipe_id), -1)
            viewModel.loadRecipe(recipeID)
        }
    }

     fun showImportDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_import_url, null)
        val editTextURL: EditText = dialogView.findViewById(R.id.editText)
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
            executor.execute {
                try {
                    viewModel.importURL(url)
                    handler.post {
                        Toast.makeText(this, getString(R.string.text_import_success), Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                    }
                } catch (e: FailedToConnectException) {
                    Log.e(getString( R.string.error_tag ), e.message, e)
                    handler.post {
                        Toast.makeText(this, getString(R.string.error_msg_failed_connection), Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                    }
                } catch (e: WebsiteNotSupportedException) {
                    Log.e(getString( R.string.error_tag ), e.message, e)
                    handler.post {
                        Toast.makeText(this, getString(R.string.error_website_not_supported), Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                    }
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
            R.id.actionSave -> {
                if (viewModel.name.value?.trim().isNullOrEmpty()) {
                    Toast.makeText(this, getString(R.string.toast_invalid_name), Toast.LENGTH_SHORT).show()
                }
                else if (viewModel.editMode) { // check if existing recipe is being changed
                    viewModel.updateRecipe()
                    Redirect.redirect(this, SearchActivity::class.java)
                    Toast.makeText(this, getString(R.string.toast_recipe_modified), Toast.LENGTH_SHORT).show()
                } else {
                    viewModel.insertRecipe()
                    Redirect.redirect(this, MainActivity::class.java)
                    Toast.makeText(this, getString(R.string.toast_recipe_added), Toast.LENGTH_SHORT).show()
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}