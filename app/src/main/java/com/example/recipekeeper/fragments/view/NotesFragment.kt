package com.example.recipekeeper.fragments.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.recipekeeper.R
import com.example.recipekeeper.viewmodels.RecipeViewModel

class NotesFragment : Fragment() {
    private val viewModel: RecipeViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_instructions, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val textView: TextView = view.findViewById(R.id.textView)
        viewModel.recipe.observe(viewLifecycleOwner) { recipe ->
            textView.text = recipe?.notes ?: ""
        }
    }
}