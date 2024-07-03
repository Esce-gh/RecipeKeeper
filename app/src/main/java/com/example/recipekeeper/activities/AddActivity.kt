package com.example.recipekeeper.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.recipekeeper.R
import com.example.recipekeeper.scraper.Scraper
import com.google.android.material.textfield.TextInputEditText
import java.util.concurrent.Executors

class AddActivity : AppCompatActivity() {

    private val myExecutor = Executors.newSingleThreadExecutor()
    private val myHandler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add)

        val buttonNavigate: Button = findViewById(R.id.buttonAdd)
        val textInputURL: TextInputEditText = findViewById(R.id.textInputURL)

        buttonNavigate.setOnClickListener {
            myExecutor.execute {
                val scraper = Scraper(textInputURL.text.toString())
                val data = scraper.ingredientsList
                myHandler.post {
                    val intent = Intent(this, AddConfirmationActivity::class.java).apply {
                        putExtra("DATA", data)
                    }
                    startActivity(intent)
                }
            }

        }
    }
}