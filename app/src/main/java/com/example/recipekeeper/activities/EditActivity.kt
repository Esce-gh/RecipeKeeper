package com.example.recipekeeper.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.recipekeeper.R
import com.google.android.material.textfield.TextInputEditText

class EditActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_item)

        val itemPosition = intent.getIntExtra("ITEM_POSITION", -1)
        val itemName = intent.getStringExtra("ITEM_NAME")
        val itemAmount = intent.getStringExtra("ITEM_AMOUNT")
        val itemUnit = intent.getStringExtra("ITEM_UNIT")

        val textInputName = findViewById<TextInputEditText>(R.id.textInputName)
        val textInputAmount = findViewById<TextInputEditText>(R.id.textInputAmount)
        val textInputUnit = findViewById<TextInputEditText>(R.id.textInputUnit)
        val buttonSave = findViewById<Button>(R.id.buttonSave)

        textInputName.setText(itemName)
        textInputAmount.setText(itemAmount)
        textInputUnit.setText(itemUnit)

        buttonSave.setOnClickListener {
            val newName = textInputName.text.toString()
            val newAmount = textInputAmount.text.toString()
            val newUnit = textInputUnit.text.toString()
            val resultIntent = Intent()

            resultIntent.putExtra("ITEM_NAME", newName)
            resultIntent.putExtra("ITEM_AMOUNT", newAmount)
            resultIntent.putExtra("ITEM_UNIT", newUnit)
            resultIntent.putExtra("ITEM_POSITION", itemPosition)

            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        }
    }
}