package com.example.recipekeeper.adapters

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.recipekeeper.fragments.DetailsEditFragment
import com.example.recipekeeper.fragments.IngredientsEditFragment
import com.example.recipekeeper.fragments.InstructionsEditFragment
import com.example.recipekeeper.fragments.NotesEditFragment

class EditPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int = 4  // Number of fragments
    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> DetailsEditFragment()
            1 -> IngredientsEditFragment()
            2 -> InstructionsEditFragment()
            3 -> NotesEditFragment()
            else -> throw IllegalArgumentException("Invalid position: $position")
        }
    }
}