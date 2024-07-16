package com.example.recipekeeper.utils

import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.recipekeeper.R

class ToolbarUtil {
    companion object {
        fun InitializeToolbar(activity: AppCompatActivity, toolbar: Toolbar, title: String, showHomeAsUp: Boolean = true) {
            activity.setSupportActionBar(toolbar)
            activity.supportActionBar?.apply {
                setDisplayHomeAsUpEnabled(showHomeAsUp)
                setHomeAsUpIndicator(R.drawable.ic_arrow_back)
                setTitle(title)
            }
            toolbar.setNavigationOnClickListener {
                activity.finish()
            }
        }
    }
}