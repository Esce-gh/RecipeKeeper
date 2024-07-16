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
import androidx.viewpager2.widget.ViewPager2
import com.example.recipekeeper.R
import com.example.recipekeeper.adapters.EditPagerAdapter
import com.example.recipekeeper.models.EditRecipeViewModel
import com.example.recipekeeper.scraper.Ingredient
import com.example.recipekeeper.utils.ToolbarUtil
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import java.util.concurrent.Executors

class EditActivity : AppCompatActivity() {
    private val viewModel: EditRecipeViewModel by viewModels()
    private val myExecutor = Executors.newSingleThreadExecutor()
    private val myHandler = Handler(Looper.getMainLooper())

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        ToolbarUtil.InitializeToolbar(this, toolbar, "Edit recipe")

        val viewPager: ViewPager2 = findViewById(R.id.viewPager)
        val tabLayout: TabLayout = findViewById(R.id.tabLayout)
        val pagerAdapter = EditPagerAdapter(this)
        viewPager.adapter = pagerAdapter
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Details"
                1 -> "Ingredients"
                2 -> "Instructions"
                3 -> "Notes"
                else -> ""
            }
        }.attach()

        val editMode = intent.getBooleanExtra("EDIT", false)
        if (editMode) {
            val name = intent.getStringExtra("NAME") ?: ""
            val url = intent.getStringExtra("URL") ?: ""
            viewModel.setName(name)
            viewModel.setUrl(url)
            viewModel.editMode = true
            // load ingredient data from previous activity
            val data = intent.getSerializableExtra("DATA", ArrayList::class.java)
            if (data != null) {
                for (a in data) {
                    viewModel.addItem(a as Ingredient)
                }
            }
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