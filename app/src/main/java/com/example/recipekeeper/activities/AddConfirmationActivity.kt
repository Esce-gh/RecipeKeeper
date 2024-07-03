package com.example.recipekeeper.activities

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.recipekeeper.adapters.ItemAdapter
import com.example.recipekeeper.R
import com.example.recipekeeper.scraper.Ingredient

class AddConfirmationActivity : AppCompatActivity() {
    private lateinit var editItemLauncher: ActivityResultLauncher<Intent>
    private lateinit var adapter: ItemAdapter
    private var items = ArrayList<Ingredient>()

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_confirmation)

        val data = intent.getSerializableExtra("DATA", ArrayList::class.java)
        if (data != null) {
            for (a in data) {
                items.add(a as Ingredient)
            }
        }

        val recyclerView: RecyclerView = findViewById(R.id.recyclerViewIngredients)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = ItemAdapter(items) { item, position ->
            val intent = Intent(this, EditActivity::class.java).apply {
                putExtra("ITEM_POSITION", position)
                putExtra("ITEM_UNIT", item.unit)
                putExtra("ITEM_NAME", item.name)
                putExtra("ITEM_AMOUNT", item.amount)
            }
            editItemLauncher.launch(intent)
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
                        updateItem(itemPosition, itemName, itemAmount, itemUnit)
                    }
                }
            }
    }

    private fun updateItem(
        position: Int,
        itemName: String?,
        itemAmount: String?,
        itemUnit: String?
    ) {
        if (position != -1) {
            items[position].name = itemName ?: items[position].name
            items[position].unit = itemUnit ?: items[position].unit
            items[position].amount = itemAmount ?: items[position].amount
            adapter.notifyItemChanged(position)
        }
    }
}