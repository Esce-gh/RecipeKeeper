package com.example.recipekeeper.fragments

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.recipekeeper.R
import com.example.recipekeeper.activities.EditIngredientActivity
import com.example.recipekeeper.adapters.ItemAdapter
import com.example.recipekeeper.models.EditRecipeViewModel

class IngredientsEditFragment : Fragment() {
    private val viewModel: EditRecipeViewModel by activityViewModels()
    private lateinit var editItemLauncher: ActivityResultLauncher<Intent>
    private lateinit var adapter: ItemAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_edit_ingredients, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // recycler view that hold ingredients list
        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerViewIngredients)
        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = ItemAdapter(ArrayList()) { item, position ->
            EditIntent(position, item)
        }
        recyclerView.adapter = adapter
        viewModel.items.observe(viewLifecycleOwner, Observer { items ->
            adapter.updateItems(items)
        })

        val buttonAdd: Button = view.findViewById(R.id.buttonAdd)
        buttonAdd.setOnClickListener {
            val item = ""
            viewModel.addItem(item)
            EditIntent((viewModel.items.value?.size ?: 1) - 1, item)
        }

        editItemLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val data = result.data
                    data?.let {
                        val itemPosition = it.getIntExtra("ITEM_POSITION", -1)
                        val itemRemove = it.getBooleanExtra("ITEM_REMOVE", false)
                        if (itemRemove) {
                            viewModel.removeItem(itemPosition)
                            return@let
                        }

                        val itemName = it.getStringExtra("ITEM_NAME") ?: ""
                        if (itemPosition != -1) {
                            viewModel.editItem(itemPosition, itemName)
                        }
                    }
                }
                else if (result.resultCode == Activity.RESULT_CANCELED) {
                    val itemPosition = viewModel.items.value?.size ?: 1
                    viewModel.removeItem(itemPosition - 1)
                }
            }
    }

    private fun EditIntent(position: Int, item: String) {
        val intent = Intent(activity, EditIngredientActivity::class.java).apply {
            putExtra("ITEM_POSITION", position)
            putExtra("ITEM_NAME", item)
        }
        editItemLauncher.launch(intent)
    }
}