package com.example.at.models

import android.provider.ContactsContract
import com.jjoe64.graphview.series.DataPoint

class TrainingModel {
    var isLine: Boolean = false
    var hitsTotal: UInt = 0u
    var hitsIn: UInt = 0u
    var hitsOut: UInt = 0u
    var hits: List<DataPoint> = ArrayList()
}