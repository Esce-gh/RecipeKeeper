package com.example.recipekeeper.fragments

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.example.recipekeeper.R
import com.example.recipekeeper.activities.MainActivity
import com.example.recipekeeper.activities.SearchActivity
import com.example.recipekeeper.models.EditRecipeViewModel
import com.example.recipekeeper.utils.Redirect
import com.google.android.material.textfield.TextInputEditText

class DetailsEditFragment() : Fragment() {
    private val viewModel: EditRecipeViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_edit_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val textInputName: TextInputEditText = view.findViewById(R.id.textInputName)
        val textInputURL: TextInputEditText = view.findViewById(R.id.textInputURL)

        viewModel.name.observe(viewLifecycleOwner, Observer { name ->
            textInputName.setText(name)
        })
        viewModel.url.observe(viewLifecycleOwner, Observer { url ->
            textInputURL.setText(url)
        })


        // TODO: add the button to the toolbar
        // button that saves the recipe
        val buttonConfirm: Button = view.findViewById(R.id.buttonConfirm)
        buttonConfirm.setOnClickListener {
            context?.let { con ->
                viewModel.setName(textInputName.text.toString())
                viewModel.setUrl(textInputURL.text.toString())
                if (viewModel.editMode) { // check if existing recipe is being changed
                    viewModel.updateRecipe()
                    Redirect.redirect(con, SearchActivity::class.java)
                    Toast.makeText(con, "Recipe modified!", Toast.LENGTH_SHORT).show()
                } else {
                    viewModel.insertRecipe()
                    Redirect.redirect(con, MainActivity::class.java)
                    Toast.makeText(con, "Recipe added!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}