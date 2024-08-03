package com.example.recipekeeper.fragments.edit

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.recipekeeper.R
import com.example.recipekeeper.viewmodels.EditRecipeViewModel

class InstructionsEditFragment : Fragment() {
    private val viewModel: EditRecipeViewModel by activityViewModels()
    private lateinit var editText: EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_edit_instructions, container, false)
        editText = view.findViewById(R.id.editText)
        viewModel.instructions.observe(viewLifecycleOwner) { instructions ->
            if (editText.text.toString() != instructions) {
                editText.setText(instructions)
            }
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                viewModel.setInstructions(s.toString())
            }
        })

        val buttonFormat: Button = view.findViewById(R.id.buttonFormat)
        buttonFormat.setOnClickListener {
            showFormatDialog()
        }
    }

    private fun showFormatDialog() {
        val dialogView =
            LayoutInflater.from(context).inflate(R.layout.dialog_format, null)
        val buttonCancel: Button = dialogView.findViewById(R.id.buttonCancel)
        val buttonOK: Button = dialogView.findViewById(R.id.buttonOK)
        val checkBoxNumbers: CheckBox = dialogView.findViewById(R.id.checkBoxNumbers)
        val checkBoxLines: CheckBox = dialogView.findViewById(R.id.checkBoxNewLines)

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        buttonCancel.setOnClickListener {
            dialog.dismiss()
        }

        buttonOK.setOnClickListener {
            viewModel.formatInstructions(checkBoxNumbers.isChecked, checkBoxLines.isChecked)
            dialog.dismiss()
        }

        dialog.show()
    }
}