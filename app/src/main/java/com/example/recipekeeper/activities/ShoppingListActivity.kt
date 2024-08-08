package com.example.recipekeeper.activities

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
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

    fun addDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_to_shopping_list, null)
        val editText: EditText = dialogView.findViewById(R.id.editText)
        val buttonCancel: Button = dialogView.findViewById(R.id.buttonCancel)
        val buttonOK: Button = dialogView.findViewById(R.id.buttonOK)

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        buttonCancel.setOnClickListener {
            dialog.dismiss()
        }

        buttonOK.setOnClickListener {
            viewModel.addItem(editText.text.toString())
            dialog.dismiss()
        }

        dialog.show()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.shopping_list_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.actionAdd -> {
                addDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}