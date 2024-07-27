package com.example.recipekeeper.fragments.edit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.recipekeeper.R
import com.example.recipekeeper.adapters.ItemAdapter
import com.example.recipekeeper.viewmodels.EditRecipeViewModel
import com.example.recipekeeper.viewmodels.IngredientGroup
import com.example.recipekeeper.viewmodels.ItemTouchHelperCallback

class IngredientsEditFragment : Fragment() {
    private val viewModel: EditRecipeViewModel by activityViewModels()
    private lateinit var adapter: ItemAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_edit_ingredients, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // recycler view that hold ingredients list
        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerViewIngredients)
        recyclerView.layoutManager = LinearLayoutManager(context)

        // TODO: fix
        val ingredients1 = arrayListOf("a", "b", "c")
        val ingredients2 = arrayListOf("asd","qwe")
        val ingredients3 = arrayListOf("trwe","fgsd", "xgysd")
        val ungrouped = IngredientGroup("", ingredients3)
        val group1 = IngredientGroup("group1", ingredients1)
        val group2 = IngredientGroup("group2", ingredients2)
        val groups = arrayListOf(ungrouped, group1, group2)

        adapter = ItemAdapter(groups) { position ->
            showEditDialog(position, false)
        }
        recyclerView.adapter = adapter
        val callback = ItemTouchHelperCallback(adapter)
        val itemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper.attachToRecyclerView(recyclerView)
//        viewModel.items.observe(viewLifecycleOwner) { items ->
//            if (items != null) {
//                adapter.updateItems(items)
//            }
//        }

        val buttonAdd: Button = view.findViewById(R.id.buttonAdd)
        buttonAdd.setOnClickListener {
            val item = ""
            viewModel.addItem(item)
            showEditDialog((viewModel.items.value?.size ?: 1) - 1, true)
        }

        val buttonRemoveSelected: Button = view.findViewById(R.id.buttonRemoveSelected)
        buttonRemoveSelected.setOnClickListener {
            val selectedItems = adapter.getSelectedItems()
            if (selectedItems.isEmpty()) {
                Toast.makeText(context, getString(R.string.toast_no_ingredients_selected), Toast.LENGTH_SHORT).show()
            } else {
                viewModel.removeSelected(selectedItems)
                adapter.deselectItems()
            }
        }

        val buttonPaste: Button = view.findViewById(R.id.buttonPaste)
        buttonPaste.setOnClickListener {
            showPasteDialog()
        }
    }

    private fun showPasteDialog() {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_paste_ingredients, null)
        val editText: EditText = dialogView.findViewById(R.id.editText)
        val buttonCancel: Button = dialogView.findViewById(R.id.buttonCancel)
        val buttonOK: Button = dialogView.findViewById(R.id.buttonOK)

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        buttonCancel.setOnClickListener {
            dialog.dismiss()
        }

        buttonOK.setOnClickListener {
            viewModel.pasteIngredients(editText.text.toString())
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun showEditDialog(position: Int, newItem: Boolean) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_edit_ingredient, null)
        val editText: EditText = dialogView.findViewById(R.id.editText)
        editText.hint = getString(R.string.hint_ingredient)
        editText.setText(viewModel.items.value?.get(position) ?: "")

        val buttonCancel: Button = dialogView.findViewById(R.id.buttonCancel)
        val buttonOK: Button = dialogView.findViewById(R.id.buttonOK)
        val buttonRemove: Button = dialogView.findViewById(R.id.buttonRemove)

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        buttonCancel.setOnClickListener {
            if (newItem) {
                viewModel.removeItem(position)
            }
            dialog.dismiss()
        }

        buttonRemove.setOnClickListener {
            viewModel.removeItem(position)
            dialog.dismiss()
        }

        buttonOK.setOnClickListener {
            viewModel.editItem(position, editText.text.toString())
            dialog.dismiss()
        }

        dialog.show()
    }
}