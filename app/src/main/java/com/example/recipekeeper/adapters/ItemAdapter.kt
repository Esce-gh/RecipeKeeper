package com.example.recipekeeper.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.recipekeeper.R

class ItemAdapter(
    private val items: ArrayList<String>,
    private val onEditClick: ((String, Int) -> Unit)?
) : RecyclerView.Adapter<ItemAdapter.ItemViewHolder>() {
    constructor(ingredients: ArrayList<String>) : this(ingredients, null)

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
        if (onEditClick != null) {
            holder.buttonEdit.setOnClickListener {
                onEditClick.invoke(item, position)
            }
        } else {
            holder.buttonEdit.visibility = View.INVISIBLE
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun updateItems(newItems: ArrayList<String>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }
}