package com.example.at.models

import android.provider.ContactsContract
import com.jjoe64.graphview.series.DataPoint
import java.time.Duration
import java.time.LocalDateTime

class TrainingModel {
    var timeStart = 0
    var duration = 0

    var x: Int = 0
    var y: Int = 0
    var maxErrors: Int = 0

    var concentrationFaults: Int = 0

    var hitsIn: Int = 0
    var hitsOut: Int = 0
    var hits = mutableListOf<DataPoint>()

}