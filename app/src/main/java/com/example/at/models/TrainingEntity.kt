package com.example.at.models

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
class TrainingEntity(
    var date: Long,
    var duration: Long,
    var hitsIn: Int,
    var hitsOut: Int,
    var concentrationFaults: Int,
    var x: Int,
    var y: Int,
    var maxErrors: Int,
    var hits: String
) {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}