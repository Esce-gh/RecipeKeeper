package com.example.recipekeeper.fragments.edit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.recipekeeper.R
import com.example.recipekeeper.adapters.ItemAdapterEdit
import com.example.recipekeeper.utils.ItemTouchHelperCallback
import com.example.recipekeeper.viewmodels.EditRecipeViewModel

class IngredientsEditFragment : Fragment() {
    private val viewModel: EditRecipeViewModel by activityViewModels()
    private lateinit var adapter: ItemAdapterEdit

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

        adapter = ItemAdapterEdit(ArrayList()) { position ->
            showEditDialog(position, false)
        }
        recyclerView.adapter = adapter
        val callback = ItemTouchHelperCallback(adapter)
        val itemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper.attachToRecyclerView(recyclerView)
        viewModel.items.observe(viewLifecycleOwner) { items ->
            if (items != null) {
                adapter.updateItems(items)
            }
        }

        val buttonAdd: ImageButton = view.findViewById(R.id.buttonAdd)
        buttonAdd.setOnClickListener {
            showEditDialog(-1, true)
        }

        val buttonRemoveSelected: ImageButton = view.findViewById(R.id.buttonRemoveSelected)
        buttonRemoveSelected.setOnClickListener {
            val selectedItems = adapter.getSelectedItems()
            if (selectedItems.isEmpty()) {
                Toast.makeText(
                    context,
                    getString(R.string.toast_no_ingredients_selected),
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                viewModel.removeSelected(selectedItems)
                adapter.deselectItems()
            }
        }

        val buttonPaste: ImageButton = view.findViewById(R.id.buttonPaste)
        buttonPaste.setOnClickListener {
            showPasteDialog()
        }
    }

    private fun showPasteDialog() {
        val dialogView =
            LayoutInflater.from(context).inflate(R.layout.dialog_paste_ingredients, null)
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
            adapter.deselectItems()
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun showEditDialog(position: Int, newItem: Boolean) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_edit_ingredient, null)
        val editText: EditText = dialogView.findViewById(R.id.editText)
        val checkBoxGroup: CheckBox = dialogView.findViewById(R.id.checkBoxGroup)

        val items = viewModel.items.value
        if (items != null && !newItem) {
            editText.setText(viewModel.getIngredientOrGroup(position))
        }

        if (!newItem) {
            checkBoxGroup.isVisible = false
        }

        val buttonCancel: Button = dialogView.findViewById(R.id.buttonCancel)
        val buttonOK: Button = dialogView.findViewById(R.id.buttonOK)
        val buttonRemove: Button = dialogView.findViewById(R.id.buttonRemove)

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        buttonCancel.setOnClickListener {
            dialog.dismiss()
        }

        buttonRemove.setOnClickListener {
            if (!newItem) {
                viewModel.removeItem(position)
            }
            dialog.dismiss()
        }

        buttonOK.setOnClickListener {
            val newName = editText.text.toString()
            if (newItem) {
                if (checkBoxGroup.isChecked) { // create new group
                    viewModel.addGroup(newName)
                } else { // create new item
                    viewModel.addItem(newName)
                }
            } else {
                viewModel.editItem(position, newName)
            }
            dialog.dismiss()
        }

        dialog.show()
    }
}