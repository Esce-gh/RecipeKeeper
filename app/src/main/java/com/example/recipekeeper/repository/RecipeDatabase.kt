package com.example.recipekeeper.repository

import android.content.Context
import androidx.core.content.ContextCompat.getString
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.recipekeeper.R
import com.example.recipekeeper.repository.converters.Converters
import com.example.recipekeeper.repository.dao.RecipeDao
import com.example.recipekeeper.repository.dao.ShoppingListDao
import com.example.recipekeeper.repository.entities.RecipeEntity
import com.example.recipekeeper.repository.entities.ShoppingListEntity

@Database(entities = [RecipeEntity::class, ShoppingListEntity::class], version = 2)
@TypeConverters(Converters::class)
abstract class RecipeDatabase : RoomDatabase() {
    abstract fun recipeDao(): RecipeDao
    abstract fun shoppingListDao(): ShoppingListDao

    companion object {
        @Volatile
        private var INSTANCE: RecipeDatabase? = null

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("CREATE TABLE `shopping_list` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `ingredient` TEXT NOT NULL)")
            }
        }

        fun getDatabase(context: Context): RecipeDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    RecipeDatabase::class.java,
                    getString(context, R.string.database)
                )
                    .addMigrations(MIGRATION_1_2)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}