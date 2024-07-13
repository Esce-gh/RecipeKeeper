package com.example.recipekeeper.activities

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
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
import com.google.android.material.textfield.TextInputEditText

class EditActivity : AppCompatActivity() {
    private lateinit var editItemLauncher: ActivityResultLauncher<Intent>
    private lateinit var adapter: ItemAdapter
    private var items = ArrayList<Ingredient>()

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_confirmation)

        val textInputName: TextInputEditText = findViewById(R.id.textInputName)
        val textInputURL: TextInputEditText = findViewById(R.id.textInputURL)
        val buttonConfirm: Button = findViewById(R.id.buttonConfirm)
        val buttonAdd: Button = findViewById(R.id.buttonAdd)

        val editMode = intent.getBooleanExtra("EDIT", false)

        // set the tile and url
        val name = intent.getStringExtra("NAME") ?: ""
        val url = intent.getStringExtra("URL") ?: ""
        textInputName.setText(name)
        textInputURL.setText(url)

        // button that adds new ingredient
        buttonAdd.setOnClickListener {
            val item = Ingredient()
            items.add(item)
            EditIntent(items.size - 1, item)
        }

        // button that saves the recipe
        // TODO: check for same names
        buttonConfirm.setOnClickListener {
            if (editMode) {
                FileManager.deleteRecipe(this, name)
                FileManager.saveRecipe(
                    this,
                    Recipe(textInputName.text.toString(), textInputURL.text.toString(), items)
                )
                Redirect.redirect(this, SearchActivity::class.java)
                runOnUiThread {
                    Toast.makeText(this, "Recipe modified!", Toast.LENGTH_SHORT).show()
                }
            } else {
                FileManager.saveRecipe(
                    this,
                    Recipe(textInputName.text.toString(), textInputURL.text.toString(), items)
                )
                Redirect.redirect(this, MainActivity::class.java)
                runOnUiThread {
                    Toast.makeText(this, "Recipe added!", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // load ingredient data from previous activity
        val data = intent.getSerializableExtra("DATA", ArrayList::class.java)
        if (data != null) {
            for (a in data) {
                items.add(a as Ingredient)
            }
        }

        // recycler view that hold ingredients list
        val recyclerView: RecyclerView = findViewById(R.id.recyclerViewIngredients)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = ItemAdapter(items) { item, position ->
            EditIntent(position, item)
        }
        recyclerView.adapter = adapter

        editItemLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val data = result.data
                    data?.let {
                        val itemPosition = it.getIntExtra("ITEM_POSITION", -1)
                        val itemName = it.getStringExtra("ITEM_NAME")
                        val itemAmount = it.getStringExtra("ITEM_AMOUNT")
                        val itemUnit = it.getStringExtra("ITEM_UNIT")
                        if (itemPosition != -1) {
                            items[itemPosition].name = itemName ?: items[itemPosition].name
                            items[itemPosition].unit = itemUnit ?: items[itemPosition].unit
                            items[itemPosition].amount = itemAmount ?: items[itemPosition].amount
                            adapter.notifyItemChanged(itemPosition)
                        }
                    }
                }
            }
    }

    // TODO:
    private fun EditIntent(position: Int, item: Ingredient) {
        val intent = Intent(this, EditIngredientActivity::class.java).apply {
            putExtra("ITEM_POSITION", position)
            putExtra("ITEM_UNIT", item.unit)
            putExtra("ITEM_NAME", item.name)
            putExtra("ITEM_AMOUNT", item.amount)
        }
        editItemLauncher.launch(intent)
    }
}