package com.example.recipekeeper.utils

import android.app.Activity
import android.content.Context
import android.content.Intent

class Redirect {
    companion object {
        fun redirect(context: Context, activityClass: Class<out Activity>) {
            val intent = Intent(context, activityClass)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            context.startActivity(intent)
            if (context is Activity) {
                context.finish()
            }
        }
    }
}