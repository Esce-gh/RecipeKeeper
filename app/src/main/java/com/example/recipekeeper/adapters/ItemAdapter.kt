package com.example.recipekeeper.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.recipekeeper.R
import com.example.recipekeeper.scraper.IngredientsGroup
import java.util.Collections

class ItemAdapter(
    private var groups: ArrayList<IngredientsGroup>,
    private val onEditClick: ((Int) -> Unit)?
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    constructor(groups: ArrayList<IngredientsGroup>) : this(groups, null)

    companion object {
        private const val VIEW_TYPE_GROUP = 0
        private const val VIEW_TYPE_INGREDIENT = 1
    }

    private val selectedItems = HashSet<Int>()

    class GroupViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val groupName: TextView = itemView.findViewById(R.id.textViewGroupName)
        val buttonEdit: ImageButton = itemView.findViewById(R.id.buttonEdit)
    }

    class IngredientViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemText: TextView = itemView.findViewById(R.id.textViewIngredient)
        val buttonEdit: ImageButton = itemView.findViewById(R.id.buttonEdit)
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

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is GroupViewHolder) {
            val group = groups[getGroupIndex(position)]
            holder.groupName.text = group.name
            if (group.name == "") {
                holder.groupName.visibility = View.GONE
                holder.itemView.layoutParams = RecyclerView.LayoutParams(0,0)
            } else {
                holder.groupName.visibility = View.VISIBLE
                holder.itemView.layoutParams = RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT)
            }
            if (onEditClick != null) {
                holder.buttonEdit.setOnClickListener {
                    onEditClick.invoke(position)
                }
            } else {
                holder.buttonEdit.visibility = View.INVISIBLE
            }
        } else if (holder is IngredientViewHolder) {
            val ingredientIndex = getIngredientIndex(position)
            val ingredient = groups[getGroupIndex(position)].ingredients[ingredientIndex]
            holder.itemText.text = ingredient
            if (onEditClick != null) {
                holder.buttonEdit.setOnClickListener {
                    onEditClick.invoke(position)
                }
            } else {
                holder.buttonEdit.visibility = View.INVISIBLE
            }
        }
        holder.itemView.isSelected = selectedItems.contains(position)

        holder.itemView.setOnClickListener {
            if (selectedItems.contains(position)) {
                selectedItems.remove(position)
                holder.itemView.setBackgroundColor(Color.parseColor("#FEF7FF")) // Default color TODO: fix
            } else {
                selectedItems.add(position)
                holder.itemView.setBackgroundColor(Color.LTGRAY) // Highlight color
            }
        }

        if (holder.itemView.isSelected) {
            holder.itemView.setBackgroundColor(Color.LTGRAY) // Highlight color
        } else {
            holder.itemView.setBackgroundColor(Color.parseColor("#FEF7FF")) // Default color
        }
    }

    override fun getItemCount(): Int {
        var count = 0
        for (group in groups) {
            count += 1 + group.ingredients.size
        }
        return count
    }

    private fun getGroupIndex(position: Int): Int {
        var itemIndex = position
        for (i in groups.indices) {
            if (itemIndex == 0) return i
            itemIndex--
            if (itemIndex < groups[i].ingredients.size) return i
            itemIndex -= groups[i].ingredients.size
        }
        return -1
    }

    private fun getIngredientIndex(position: Int): Int {
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
            when {
                position == fromPosition -> updatedSelectedItems.add(toPosition)
                position > fromPosition && position <= toPosition -> updatedSelectedItems.add(position - 1)
                position < fromPosition && position >= toPosition -> updatedSelectedItems.add(position + 1)
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