package com.example.savor.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [FavoriteEntity::class, ReviewEntity::class],
    version = 1,
    exportSchema = false
)
abstract class SavorDatabase : RoomDatabase() {
    abstract fun favoriteDao(): FavoriteDao
    abstract fun reviewDao(): ReviewDao

    companion object {
        @Volatile
        private var INSTANCE: SavorDatabase? = null

        fun getInstance(context: Context): SavorDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    SavorDatabase::class.java,
                    "savor.db"
                ).build().also { INSTANCE = it }
            }
    }
}