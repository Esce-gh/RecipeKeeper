package com.example.recipekeeper.adapters

import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.recipekeeper.R
import com.example.recipekeeper.scraper.IngredientsGroup
import java.util.Collections

class ItemAdapterEdit(
    groups: ArrayList<IngredientsGroup>,
    private val onEditClick: ((Int) -> Unit)
) : ItemAdapter(groups) {

    private val selectedItems = HashSet<Int>()

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is GroupViewHolder) {
            val group = groups[getGroupIndex(position)]
            holder.groupName.text = group.name
            if (group.name == "") {
                holder.groupName.visibility = View.GONE
                holder.itemView.layoutParams = RecyclerView.LayoutParams(0,0)
            } else {
                holder.groupName.visibility = View.VISIBLE
                holder.itemView.layoutParams = RecyclerView.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT)
            }
            holder.buttonEdit.setOnClickListener {
                onEditClick.invoke(position)
            }
        } else if (holder is IngredientViewHolder) {
            val ingredientIndex = getIngredientIndex(position)
            val ingredient = groups[getGroupIndex(position)].ingredients[ingredientIndex]
            holder.itemText.text = ingredient
            holder.buttonEdit.setOnClickListener {
                onEditClick.invoke(position)
            }
        }

        holder.itemView.setOnClickListener {
            if (selectedItems.contains(position)) {
                selectedItems.remove(position)
                holder.itemView.setBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.colorSecondary)) // Default color
            } else {
                selectedItems.add(position)
                holder.itemView.setBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.colorPrimary)) // Highlight color
            }
        }

        if (selectedItems.contains(position)) {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.colorPrimary)) // Highlight color
        } else {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.colorSecondary)) // Default color
        }
    }

    fun onItemMove(fromPosition: Int, toPosition: Int) {
        if (toPosition == 0) { // prevent moving outside the default group
            return
        }

        val type = getItemViewType(fromPosition)
        val toType = getItemViewType(toPosition)

        val fromGroupIndex = getGroupIndex(fromPosition)
        val toGroupIndex = getGroupIndex(toPosition)
        if (fromGroupIndex == -1 || toGroupIndex == -1) {
            return
        }

        if (type == VIEW_TYPE_GROUP && toType == VIEW_TYPE_GROUP) {
            if (fromPosition < toPosition) {
                updateSelectedGroups(fromPosition, toPosition)
            } else {
                updateSelectedGroups(toPosition, fromPosition)
            }
            Collections.swap(groups, fromGroupIndex, toGroupIndex)
            notifyDataSetChanged()
        } else if (type != VIEW_TYPE_GROUP) {
            val fromGroup = groups[fromGroupIndex]
            var toGroup = groups[toGroupIndex]

            val fromIngredient = getIngredientIndex(fromPosition)
            var toIngredient = getIngredientIndex(toPosition)

            if (toIngredient == -1 && fromPosition > toPosition) {
                toGroup = groups[toGroupIndex - 1]
                toIngredient = toGroup.ingredients.size
            } else if (toIngredient == -1 && fromPosition < toPosition) {
                toIngredient = 0
            }

            if (fromGroup == toGroup) { // Move within the same group
                Collections.swap(fromGroup.ingredients, fromIngredient, toIngredient)
            } else {
                val ingredient = fromGroup.ingredients.removeAt(fromIngredient)
                toGroup.ingredients.add(toIngredient, ingredient)
            }
            updateSelectedIngredients(fromPosition, toPosition)
            notifyItemMoved(fromPosition, toPosition)
            notifyItemChanged(fromPosition)
            notifyItemChanged(toPosition)
        }
    }

    private fun updateSelectedGroups(lowerIndex: Int, higherIndex: Int) {
        val updatedSelectedItems = HashSet<Int>()

        val lowerGroupSize = groups[getGroupIndex(lowerIndex)].ingredients.size
        val higherGroupSize = groups[getGroupIndex(higherIndex)].ingredients.size
        val endOfLowerGroup = lowerGroupSize + lowerIndex
        val endOfHigherGroup = higherGroupSize + higherIndex

        for (item in selectedItems) {
            if (item < lowerIndex || item > endOfHigherGroup) {
                updatedSelectedItems.add(item)
            } else if (item in lowerIndex..endOfLowerGroup) {
                updatedSelectedItems.add(item + higherGroupSize + 1)
            } else if (item in higherIndex..endOfHigherGroup) {
                updatedSelectedItems.add(item - lowerGroupSize - 1)
            }
        }

        selectedItems.clear()
        selectedItems.addAll(updatedSelectedItems)
    }

    private fun updateSelectedIngredients(fromPosition: Int, toPosition: Int) {
        val updatedSelectedItems = HashSet<Int>()
        selectedItems.forEach { position ->
            when (position) {
                fromPosition -> updatedSelectedItems.add(toPosition)
                in (fromPosition + 1)..toPosition -> updatedSelectedItems.add(position - 1)
                in toPosition..<fromPosition -> updatedSelectedItems.add(position + 1)
                else -> updatedSelectedItems.add(position)
            }
        }
        selectedItems.clear()
        selectedItems.addAll(updatedSelectedItems)
    }


    fun getSelectedItems(): List<Int> {
        return selectedItems.toList()
    }

    fun deselectItems() {
        selectedItems.clear()
        notifyDataSetChanged()
    }
}