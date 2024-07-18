package com.example.recipekeeper.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.recipekeeper.R
import com.google.android.material.textfield.TextInputEditText

class EditIngredientActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_item)

        val itemPosition = intent.getIntExtra("ITEM_POSITION", -1)
        val itemName = intent.getStringExtra("ITEM_NAME")

        val textInputName = findViewById<TextInputEditText>(R.id.textInputName)
        val buttonSave = findViewById<Button>(R.id.buttonSave)
        val buttonDelete = findViewById<Button>(R.id.buttonDelete)

        textInputName.setText(itemName)

        buttonSave.setOnClickListener {
            val newName = textInputName.text.toString()
            val resultIntent = Intent()

            resultIntent.putExtra("ITEM_NAME", newName)
            resultIntent.putExtra("ITEM_POSITION", itemPosition)

            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        }

        buttonDelete.setOnClickListener {
            val resultIntent = Intent()
            resultIntent.putExtra("ITEM_POSITION", itemPosition)
            resultIntent.putExtra("ITEM_REMOVE", true)
            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        }
    }
}