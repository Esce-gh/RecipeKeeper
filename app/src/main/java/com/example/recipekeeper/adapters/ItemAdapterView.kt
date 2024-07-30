package com.example.recipekeeper.adapters

import android.graphics.Color
import android.graphics.Paint
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.recipekeeper.scraper.IngredientsGroup

class ItemAdapterView(
    groups: ArrayList<IngredientsGroup>,
    private val onSelectionChanged: () -> Unit
) : ItemAdapter(groups) {
    private val selectedItems = HashSet<Int>()
    private val shoppingListItems = HashSet<Int>()

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is GroupViewHolder) {
            val group = groups[getGroupIndex(position)]
            holder.groupName.text = group.name
            if (group.name == "") {
                holder.groupName.visibility = View.GONE
                holder.itemView.layoutParams = RecyclerView.LayoutParams(0, 0)
            } else {
                holder.groupName.visibility = View.VISIBLE
                holder.itemView.layoutParams = RecyclerView.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            }
            holder.buttonEdit.visibility = View.INVISIBLE
        } else if (holder is IngredientViewHolder) {
            val ingredientIndex = getIngredientIndex(position)
            val ingredient = groups[getGroupIndex(position)].ingredients[ingredientIndex]
            holder.itemText.text = ingredient
            holder.buttonEdit.visibility = View.INVISIBLE
        }

        holder.itemView.setOnClickListener {
            if (shoppingListItems.isEmpty()) {
                handleSelection(holder, position)
            } else {
                handleShoppingCart(holder, position)
            }
        }

        holder.itemView.setOnLongClickListener {
            handleShoppingCart(holder, position)
            true
        }

        if (holder is IngredientViewHolder) {
            if (selectedItems.contains(position)) {
                holder.itemText.paintFlags =
                    holder.itemText.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                holder.itemText.setTextColor(Color.LTGRAY)
            } else {
                holder.itemText.paintFlags =
                    holder.itemText.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                holder.itemText.setTextColor(Color.BLACK)
            }
        }


        if (holder.itemView.isSelected) {
            holder.itemView.setBackgroundColor(Color.LTGRAY) // Highlight color
        } else {
            holder.itemView.setBackgroundColor(Color.parseColor("#FEF7FF")) // Default color
        }
    }

    private fun handleSelection(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is IngredientViewHolder) {
            if (selectedItems.contains(position)) {
                selectedItems.remove(position)
                holder.itemText.paintFlags =
                    holder.itemText.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                holder.itemText.setTextColor(Color.BLACK)
            } else {
                selectedItems.add(position)
                holder.itemText.paintFlags =
                    holder.itemText.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                holder.itemText.setTextColor(Color.LTGRAY)
            }
        } else if (holder is GroupViewHolder) {
            if (selectedItems.contains(position)) {
                selectedItems.remove(position)
                holder.groupName.paintFlags =
                    holder.groupName.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                holder.groupName.setTextColor(Color.BLACK)
            } else {
                selectedItems.add(position)
                holder.groupName.paintFlags =
                    holder.groupName.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                holder.groupName.setTextColor(Color.LTGRAY)
            }
        }
    }

    private fun handleShoppingCart(holder: RecyclerView.ViewHolder, position: Int) {
        if (shoppingListItems.contains(position)) {
            shoppingListItems.remove(position)
            holder.itemView.setBackgroundColor(Color.parseColor("#FEF7FF")) // TODO: fix color
        } else if (holder.itemViewType == VIEW_TYPE_INGREDIENT) {
            shoppingListItems.add(position)
            holder.itemView.setBackgroundColor(Color.GRAY)
        }
        onSelectionChanged()
    }

    fun getShoppingListIndexes(): HashSet<Int> {
        return shoppingListItems
    }

    fun getShoppingList(): ArrayList<String> {
        val list = ArrayList<String>()
        shoppingListItems.forEach { i ->
            list.add(groups[getGroupIndex(i)].ingredients[getIngredientIndex(i)])
        }
        return list
    }

    fun clearShoppingSelection() {
        shoppingListItems.clear()
        notifyDataSetChanged()
    }
}