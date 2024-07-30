package com.example.recipekeeper.activities

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.recipekeeper.R
import com.example.recipekeeper.adapters.ShoppingListAdapter
import com.example.recipekeeper.utils.ApplicationViewModelFactory
import com.example.recipekeeper.utils.ToolbarUtil
import com.example.recipekeeper.viewmodels.ShoppingListViewModel

class ShoppingListActivity : AppCompatActivity() {
    private lateinit var adapter: ShoppingListAdapter
    private lateinit var viewModel: ShoppingListViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shopping)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        ToolbarUtil.InitializeToolbar(this, toolbar, getString(R.string.text_shopping_list))

        val viewModelFactory = ApplicationViewModelFactory(application, ShoppingListViewModel::class)
        viewModel = ViewModelProvider(this, viewModelFactory)[ShoppingListViewModel::class.java]

        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = ShoppingListAdapter(ArrayList())
        recyclerView.adapter = adapter

        viewModel.items.observe(this) { items ->
            adapter.updateItems(items)
        }

        val buttonRemoveSelected: Button = findViewById(R.id.buttonRemoveSelected)
        buttonRemoveSelected.setOnClickListener {
            viewModel.removeItems(adapter.getSelected())
            adapter.clearSelection()
        }

        val buttonRemoveAll: Button = findViewById(R.id.buttonRemoveAll)
        buttonRemoveAll.setOnClickListener {
            removeAllDialog()
        }
    }

    private fun removeAllDialog() {
        AlertDialog.Builder(this)
            .setMessage(getString(R.string.dialog_shopping_list_removal))
            .setPositiveButton(getString(R.string.text_confirm)) { _, _ ->
                viewModel.removeAllItems()
                adapter.clearSelection()
            }
            .setNegativeButton(getString(R.string.text_cancel), null)
            .show()
    }
}