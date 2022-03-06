package com.example.at.models

import androidx.room.*

@Dao
interface TrainingDao {
    @Query("SELECT * FROM TrainingEntity")
    fun getAll(): List<TrainingEntity>?

    @Query("SELECT * FROM TrainingEntity WHERE id = :id")
    fun getById(id: Long): TrainingEntity?

    @Insert
    fun insert(training: TrainingEntity?)

    @Query("DELETE FROM TrainingEntity WHERE id = :id")
    fun delete(id: Long)
}