package com.example.recipekeeper.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.recipekeeper.R

class ItemAdapter(
    private var items: ArrayList<String>,
    private val onEditClick: ((Int) -> Unit)?
) : RecyclerView.Adapter<ItemAdapter.ItemViewHolder>() {
    constructor(ingredients: ArrayList<String>) : this(ingredients, null)

    private val selectedItems = HashSet<Int>()

    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemText: TextView = itemView.findViewById(R.id.textViewIngredient)
        val buttonEdit: Button = itemView.findViewById(R.id.buttonEdit)
    }

    // TODO: long texts won't display properly
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_ingredient, parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = items[position]
        holder.itemText.text = item
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

        if (onEditClick != null) {
            holder.buttonEdit.setOnClickListener {
                onEditClick.invoke(position)
            }
        } else {
            holder.buttonEdit.visibility = View.INVISIBLE
        }

        if (holder.itemView.isSelected) {
            holder.itemView.setBackgroundColor(Color.LTGRAY) // Highlight color
        } else {
            holder.itemView.setBackgroundColor(Color.parseColor("#FEF7FF")) // Default color
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun updateItems(newItems: ArrayList<String>) {
        items = newItems
        notifyDataSetChanged()
    }

    fun getSelectedItems(): List<Int> {
        return selectedItems.toList()
    }

    fun deselectItems() {
        selectedItems.clear()
        notifyDataSetChanged()
    }
}