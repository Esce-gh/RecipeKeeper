package com.example.recipekeeper.fragments.edit

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.recipekeeper.R
import com.example.recipekeeper.activities.MainActivity
import com.example.recipekeeper.activities.SearchActivity
import com.example.recipekeeper.utils.Redirect
import com.example.recipekeeper.viewmodels.EditRecipeViewModel
import com.google.android.material.textfield.TextInputEditText

class DetailsEditFragment : Fragment() {
    private val viewModel: EditRecipeViewModel by activityViewModels()
    private lateinit var nameTextWatcher: TextWatcher
    private lateinit var urlTextWatcher: TextWatcher

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

        viewModel.name.observe(viewLifecycleOwner) { name ->
            textInputName.updateTextSafely(name)
        }
        viewModel.url.observe(viewLifecycleOwner) { url ->
            textInputURL.updateTextSafely(url)
        }

        nameTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                viewModel.setName(s.toString())
            }
        }

        urlTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                viewModel.setUrl(s.toString())
            }
        }

        textInputName.addTextChangedListener(nameTextWatcher)
        textInputURL.addTextChangedListener(urlTextWatcher)

        // button that saves the recipe
        val buttonConfirm: Button = view.findViewById(R.id.buttonConfirm)
        buttonConfirm.setOnClickListener {
            context?.let { con ->
                if (viewModel.name.value?.trim().isNullOrEmpty()) {
                    Toast.makeText(con, getString(R.string.toast_invalid_name), Toast.LENGTH_SHORT).show()
                }
                else if (viewModel.editMode) { // check if existing recipe is being changed
                    viewModel.updateRecipe()
                    Redirect.redirect(con, SearchActivity::class.java)
                    Toast.makeText(con, getString(R.string.toast_recipe_modified), Toast.LENGTH_SHORT).show()
                } else {
                    viewModel.insertRecipe()
                    Redirect.redirect(con, MainActivity::class.java)
                    Toast.makeText(con, getString(R.string.toast_recipe_added), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun EditText.updateTextSafely(newText: String) {
        if (text.toString() != newText) {
            removeTextChangedListener(nameTextWatcher)
            setText(newText)
            setSelection(newText.length)
            addTextChangedListener(nameTextWatcher)
        }
    }
}