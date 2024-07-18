package com.example.recipekeeper.utils

import android.content.Context
import com.example.recipekeeper.R
import com.example.recipekeeper.models.Recipe
import java.io.ObjectInputStream
import java.io.ObjectOutputStream

class FileManager {
    companion object {
        fun saveRecipe(context: Context, recipe: Recipe) {
            val fileName: String = context.getString(R.string.recipe_data_file)
            try {
                val append = context.getFileStreamPath(fileName).exists()
                val fileOutputStream = context.openFileOutput(fileName, Context.MODE_APPEND)
                val objectOutputStream = if (append) {
                    AppendObjectOutputStream(fileOutputStream)
                } else {
                    ObjectOutputStream(fileOutputStream)
                }
                objectOutputStream.writeObject(recipe)
                objectOutputStream.close()
                fileOutputStream.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        fun loadRecipes(context: Context): ArrayList<Recipe> {
            val fileName: String = context.getString(R.string.recipe_data_file)
            val recipes = arrayListOf<Recipe>()
            try {
                val fileInputStream = context.openFileInput(fileName)
                val objectInputStream = ObjectInputStream(fileInputStream)
                while (true) {
                    try {
                        val recipe = objectInputStream.readObject() as Recipe
                        recipes.add(recipe)
                    } catch (e: Exception) {
                        break // End of file reached
                    }
                }
                objectInputStream.close()
                fileInputStream.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return recipes
        }

        fun deleteRecipe(context: Context, recipeName: String) {
            val recipes = loadRecipes(context)
            val fileName: String = context.getString(R.string.recipe_data_file)
            recipes.removeAll() { it.name == recipeName }

            try {
                val fileOutputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE)
                val objectOutputStream = ObjectOutputStream(fileOutputStream)
                objectOutputStream.close()
                fileOutputStream.close()

                val fileOutputStreamAppend = context.openFileOutput(fileName, Context.MODE_APPEND)
                val objectOutputStreamAppend = AppendObjectOutputStream(fileOutputStreamAppend)
                for (recipe in recipes) {
                    try {
                        objectOutputStreamAppend.writeObject(recipe)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                fileOutputStreamAppend.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}