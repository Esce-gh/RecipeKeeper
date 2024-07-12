package com.example.recipekeeper.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.recipekeeper.R
import com.example.recipekeeper.scraper.Ingredient

class ItemAdapter(
    private val ingredients: ArrayList<Ingredient>,
    private val onEditClick: ((Ingredient, Int) -> Unit)?
) : RecyclerView.Adapter<ItemAdapter.ItemViewHolder>() {
    constructor(ingredients: ArrayList<Ingredient>) : this(ingredients, null)

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
        val item = ingredients[position]
        val text = item.amount +
                (if (item.amount.isEmpty()) "" else " ") +
                item.unit +
                (if (item.unit.isEmpty()) "" else " ") +
                item.name
        holder.itemText.text = text
        if (onEditClick != null) {
            holder.buttonEdit.setOnClickListener {
                onEditClick.invoke(item, position)
            }
        } else {
            holder.buttonEdit.visibility = View.INVISIBLE
        }
    }

    override fun getItemCount(): Int {
        return ingredients.size
    }
}