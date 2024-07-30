package com.example.recipekeeper.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.recipekeeper.R
import com.example.recipekeeper.scraper.IngredientsGroup

abstract class ItemAdapter(
    protected var groups: ArrayList<IngredientsGroup>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val VIEW_TYPE_GROUP = 0
        const val VIEW_TYPE_INGREDIENT = 1
    }

    class GroupViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val groupName: TextView = itemView.findViewById(R.id.textViewGroupName)
        val buttonEdit: ImageButton = itemView.findViewById(R.id.buttonEdit)
    }

    class IngredientViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemText: TextView = itemView.findViewById(R.id.textViewIngredient)
        val buttonEdit: ImageButton = itemView.findViewById(R.id.buttonEdit)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_GROUP) {
            GroupViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.item_group, parent, false)
            )
        } else {
            IngredientViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.item_ingredient, parent, false)
            )
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (isGroupHeader(position)) VIEW_TYPE_GROUP else VIEW_TYPE_INGREDIENT
    }

    private fun isGroupHeader(position: Int): Boolean {
        var count = 0
        for (group in groups) {
            if (position == count) return true
            count += 1 + group.ingredients.size
        }
        return false
    }

    override fun getItemCount(): Int {
        var count = 0
        for (group in groups) {
            count += 1 + group.ingredients.size
        }
        return count
    }

    fun getGroupIndex(position: Int): Int {
        var itemIndex = position
        for (i in groups.indices) {
            if (itemIndex == 0) return i
            itemIndex--
            if (itemIndex < groups[i].ingredients.size) return i
            itemIndex -= groups[i].ingredients.size
        }
        return -1
    }

    fun getIngredientIndex(position: Int): Int {
        var count = 0
        for (group in groups) {
            count += 1
            if (position < count + group.ingredients.size) return position - count
            count += group.ingredients.size
        }
        return -1
    }

    fun updateItems(newGroups: ArrayList<IngredientsGroup>) {
        groups = newGroups
        notifyDataSetChanged()
    }
}