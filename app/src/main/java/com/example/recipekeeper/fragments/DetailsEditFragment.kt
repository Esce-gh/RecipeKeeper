package com.example.recipekeeper.fragments

import android.content.Context
import android.os.Bundle
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
import com.example.recipekeeper.models.Recipe
import com.example.recipekeeper.utils.FileManager
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
                // check if recipe with same name already exists
                if (!viewModel.editMode && !NameAvailable(con, textInputName.text.toString())) {
                    activity?.runOnUiThread {
                        Toast.makeText(
                            con,
                            "Recipe with this name already exists!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else if (viewModel.editMode) { // check if existing recipe is being changed
                    FileManager.deleteRecipe(con, viewModel.name.value ?: "")
                    FileManager.saveRecipe(
                        con,
                        Recipe(
                            textInputName.text.toString(),
                            textInputURL.text.toString(),
                            viewModel.items.value ?: ArrayList(),
                            viewModel.instructions.value ?: "",
                            viewModel.notes.value ?: ""
                        )
                    )
                    Redirect.redirect(con, SearchActivity::class.java)
                    activity?.runOnUiThread {
                        Toast.makeText(con, "Recipe modified!", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    FileManager.saveRecipe(
                        con,
                        Recipe(
                            textInputName.text.toString(),
                            textInputURL.text.toString(),
                            viewModel.items.value ?: ArrayList(),
                            viewModel.instructions.value ?: "",
                            viewModel.notes.value ?: ""
                        )
                    )
                    Redirect.redirect(con, MainActivity::class.java)
                    activity?.runOnUiThread {
                        Toast.makeText(con, "Recipe added!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun NameAvailable(context: Context, name: String): Boolean {
        val recipes = FileManager.loadRecipes(context)
        for (recipe in recipes) {
            if (recipe.name == name) {
                return false
            }
        }
        return true
    }
}