package com.example.recipekeeper.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.recipekeeper.R
import com.example.recipekeeper.adapters.ItemAdapter
import com.example.recipekeeper.models.EditRecipeViewModel
import com.example.recipekeeper.models.RecipeViewModel
import com.example.recipekeeper.scraper.Ingredient


class IngredientsFragment() : Fragment() {
    private val viewModel: RecipeViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_ingredients, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerViewIngredients)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = ItemAdapter(viewModel.recipe.ingredients)
    }
}