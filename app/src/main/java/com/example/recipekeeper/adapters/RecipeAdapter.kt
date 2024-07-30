package com.example.recipekeeper.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.recipekeeper.R
import com.example.recipekeeper.repository.entities.RecipeEntity

class RecipeAdapter(
    private val listener: (RecipeEntity) -> Unit,
    private val onSelectionChanged: () -> Unit
) : ListAdapter<RecipeEntity, RecipeAdapter.RecipeViewHolder>(RecipeDiffCallback()) {

    private val selectedItems = HashSet<Int>()

    inner class RecipeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textViewName: TextView = view.findViewById(R.id.textViewRecipeName)
        val itemContainer: View = view.findViewById(R.id.itemContainer)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        return RecipeViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_recipe, parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        val recipe = getItem(position)
        holder.textViewName.text = recipe.name
        holder.itemView.setOnClickListener { listener(recipe) }

        // Set background based on selection state
        if (selectedItems.contains(recipe.id)) {
            holder.itemContainer.setBackgroundColor(Color.LTGRAY) // Highlight color
        } else {
            holder.itemContainer.setBackgroundColor(Color.TRANSPARENT) // Default color
        }

        holder.itemView.setOnClickListener {
            if (selectedItems.isNotEmpty()) {
                toggleSelection(recipe)
            } else {
                listener(recipe)
            }
        }

        // Handle long click for selection
        holder.itemView.setOnLongClickListener {
            toggleSelection(recipe)
            true
        }
    }

    private fun toggleSelection(recipe: RecipeEntity) {
        if (selectedItems.contains(recipe.id)) {
            selectedItems.remove(recipe.id)
        } else {
            selectedItems.add(recipe.id)
        }
        notifyItemChanged(currentList.indexOf(recipe))
        onSelectionChanged()
    }

    fun getSelectedRecipes(): List<RecipeEntity> {
        return currentList.filter { selectedItems.contains(it.id) }
    }

    fun clearSelection() {
        selectedItems.clear()
        notifyDataSetChanged()
    }

    class RecipeDiffCallback : DiffUtil.ItemCallback<RecipeEntity>() {
        override fun areItemsTheSame(oldItem: RecipeEntity, newItem: RecipeEntity): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: RecipeEntity, newItem: RecipeEntity): Boolean {
            return oldItem == newItem
        }
    }
}