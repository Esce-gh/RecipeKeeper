package com.example.recipekeeper.repository.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.recipekeeper.repository.entities.ShoppingListEntity

@Dao
interface ShoppingListDao {
    @Insert
    suspend fun insert(item: ShoppingListEntity)

    @Update
    suspend fun update(item: ShoppingListEntity)

    @Delete
    suspend fun delete(item: ShoppingListEntity)

    @Query("SELECT * FROM shopping_list")
    suspend fun getAll(): List<ShoppingListEntity>

    @Query("DELETE FROM shopping_list")
    suspend fun deleteAll()
}
