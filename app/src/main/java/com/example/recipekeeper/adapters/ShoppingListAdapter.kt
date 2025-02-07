package com.example.recipekeeper.adapters

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.recipekeeper.R
import com.example.recipekeeper.repository.entities.ShoppingListEntity

class ShoppingListAdapter(
    private var items: ArrayList<ShoppingListEntity>,
    private val onEditClick: ((Int) -> Unit)
) : RecyclerView.Adapter<ShoppingListAdapter.ShoppingListViewHolder>() {
    private val selectedItems = HashSet<Int>()

    inner class ShoppingListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewIngredient: TextView = itemView.findViewById(R.id.textViewIngredient)
        val buttonEdit: ImageButton = itemView.findViewById(R.id.buttonEdit)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShoppingListViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_ingredient, parent, false)
        return ShoppingListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ShoppingListViewHolder, position: Int) {
        val item = items[position]
        holder.textViewIngredient.text = item.ingredient
        holder.buttonEdit.visibility = View.VISIBLE
        holder.buttonEdit.setOnClickListener {
            onEditClick.invoke(position)
        }

        holder.itemView.setOnClickListener {
            if (selectedItems.contains(item.id)) {
                selectedItems.remove(item.id)
                holder.textViewIngredient.paintFlags =
                    holder.textViewIngredient.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                holder.textViewIngredient.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.colorTextLight))
            } else {
                selectedItems.add(item.id)
                holder.textViewIngredient.paintFlags =
                    holder.textViewIngredient.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                holder.textViewIngredient.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.colorTextDark))
            }
        }
        if (!selectedItems.contains(item.id)) {
            holder.textViewIngredient.paintFlags =
                holder.textViewIngredient.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            holder.textViewIngredient.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.colorTextLight))
        } else {
            holder.textViewIngredient.paintFlags =
                holder.textViewIngredient.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            holder.textViewIngredient.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.colorTextDark))
        }
    }

    fun getSelected(): List<ShoppingListEntity> {
        return items.filter { selectedItems.contains(it.id) }
    }

    fun clearSelection() {
        selectedItems.clear()
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = items.size

    fun updateItems(newItems: List<ShoppingListEntity>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }
}