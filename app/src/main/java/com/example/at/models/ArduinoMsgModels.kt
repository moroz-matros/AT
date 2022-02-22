package com.example.at.models

data class ZeroTypeMsg(
    var noise: Int = 0
)

class TrainingSettingsModel(var row: Int = 0, var col: Int = 0, var errors: Int = 0) {
    // convertToString prepares variable to be sent to arduino.
    // Format is "0 row col", where 0 is the number of "set" command.
    fun convertToString(): String {
        return "0 $row $col"
    }
}