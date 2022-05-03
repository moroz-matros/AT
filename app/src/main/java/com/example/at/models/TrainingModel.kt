package com.example.at.models

import com.jjoe64.graphview.series.DataPoint

class TrainingModel {
    var timeStart = 0.toLong()
    var duration = 0.toLong()

    var x: Int = 0
    var y: Int = 0
    var maxErrors: Int = 0

    var concentrationFaults: Int = 0

    var hitsIn: Int = 0
    var hitsOut: Int = 0
    var hits = mutableListOf<DataPoint>()

}