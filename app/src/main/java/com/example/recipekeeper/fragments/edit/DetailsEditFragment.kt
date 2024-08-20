package com.example.recipekeeper.fragments.edit

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
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
import com.example.recipekeeper.scraper.FailedToConnectException
import com.example.recipekeeper.scraper.WebsiteNotSupportedException
import com.example.recipekeeper.utils.Redirect
import com.example.recipekeeper.viewmodels.EditRecipeViewModel
import com.google.android.material.textfield.TextInputEditText
import java.util.concurrent.Executors

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
            textInputName.updateTextSafely(name, nameTextWatcher)
        }
        viewModel.url.observe(viewLifecycleOwner) { url ->
            textInputURL.updateTextSafely(url, urlTextWatcher)
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

        val buttonImport: Button = view.findViewById(R.id.buttonImport)
        buttonImport.setOnClickListener {
            val url = viewModel.url.value ?: ""
            val executor = Executors.newSingleThreadExecutor()
            val handler = Handler(Looper.getMainLooper())

            Toast.makeText(context, getString(R.string.text_importing_recipe), Toast.LENGTH_SHORT).show()
            executor.execute {
                try {
                    viewModel.importURL(url)
                    handler.post {
                        Toast.makeText(context, getString(R.string.text_import_success), Toast.LENGTH_SHORT).show()
                    }
                } catch (e: FailedToConnectException) {
                    Log.e(getString( R.string.error_tag ), e.message, e)
                    handler.post {
                        Toast.makeText(context, getString(R.string.error_msg_failed_connection), Toast.LENGTH_SHORT).show()
                    }
                } catch (e: WebsiteNotSupportedException) {
                    Log.e(getString( R.string.error_tag ), e.message, e)
                    handler.post {
                        Toast.makeText(context, getString(R.string.error_website_not_supported), Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun EditText.updateTextSafely(newText: String, textWatcher: TextWatcher) {
        if (text.toString() != newText) {
            removeTextChangedListener(textWatcher)
            setText(newText)
            setSelection(newText.length)
            addTextChangedListener(textWatcher)
        }
    }
}