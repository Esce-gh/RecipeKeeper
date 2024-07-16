package com.example.recipekeeper.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.example.recipekeeper.R
import com.example.recipekeeper.models.EditRecipeViewModel

class NotesEditFragment : Fragment() {
    private val viewModel: EditRecipeViewModel by activityViewModels()
    private lateinit var editText: EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_edit_instructions, container, false)
        editText = view.findViewById(R.id.editText)
        editText.setHint("Enter notes")
        viewModel.notes.observe(viewLifecycleOwner, Observer { notes ->
            if (editText.text.toString() != notes) {
                editText.setText(notes)
            }
        })
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                viewModel.setNotes(s.toString())
            }
        })

    }
}