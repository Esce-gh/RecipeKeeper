package com.example.recipekeeper.fragments.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.recipekeeper.R
import com.example.recipekeeper.adapters.ItemAdapterView
import com.example.recipekeeper.viewmodels.RecipeViewModel


class IngredientsFragment : Fragment() {
    private val viewModel: RecipeViewModel by activityViewModels()
    private lateinit var adapter: ItemAdapterView
    private lateinit var buttonShoppingList: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_ingredients, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        buttonShoppingList = view.findViewById(R.id.buttonAddToCart)
        buttonShoppingList.setOnClickListener {
            viewModel.addShoppingList(adapter.getShoppingList())
            adapter.clearShoppingSelection()
            Toast.makeText(context, getString(R.string.text_shopping_list_added), Toast.LENGTH_SHORT).show()
            updateButtonVisibility()
        }

        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerViewIngredients)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = ItemAdapterView(ArrayList()) { updateButtonVisibility() }
        recyclerView.adapter = adapter

        viewModel.recipe.observe(viewLifecycleOwner) { recipe ->
            recipe?.let { adapter.updateItems(ArrayList(it.ingredients)) }
        }
    }

    private fun updateButtonVisibility() {
        buttonShoppingList.visibility = if (adapter.getShoppingListIndexes().isNotEmpty()) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }
}