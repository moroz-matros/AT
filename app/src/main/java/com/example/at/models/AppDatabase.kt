package com.example.at.models

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [TrainingEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract val trainDao: TrainingDao?

    companion object {
        private var instance: AppDatabase? = null
        @Synchronized
        fun getInstance(context: Context?): AppDatabase? {
            if (instance == null) {
                instance = create(context)
            }
            return instance
        }

        private fun create(context: Context?): AppDatabase {
            return Room.databaseBuilder(
                context!!,
                AppDatabase::class.java,
                "training"
            ).allowMainThreadQueries()
                .build()
        }
    }
}