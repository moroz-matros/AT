package com.example.at.models

import com.google.gson.annotations.SerializedName

data class Training(
    @SerializedName("trainings")
    val trainings: List<TrainingEntity>
)