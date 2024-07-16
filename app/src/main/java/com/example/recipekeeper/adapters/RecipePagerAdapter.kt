package com.example.recipekeeper.adapters

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.recipekeeper.fragments.IngredientsFragment
import com.example.recipekeeper.fragments.InstructionsFragment
import com.example.recipekeeper.fragments.NotesFragment
import com.example.recipekeeper.scraper.Ingredient

class RecipePagerAdapter(fragmentActivity: FragmentActivity, val items: ArrayList<Ingredient>) : FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int = 3
    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> IngredientsFragment(items)
            1 -> InstructionsFragment()
            2 -> NotesFragment()
            else -> throw IllegalArgumentException("Invalid position: $position")
        }
    }
}