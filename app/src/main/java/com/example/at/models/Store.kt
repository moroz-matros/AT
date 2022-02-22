package com.example.at.models

class Store {
    companion object {
        var currentSettings: TrainingSettingsModel? = null
        var currentTraining: TrainingModel? = null
        var ArduinoMsg: String? = null

        fun parseZeroMsg(msg: String): ZeroTypeMsg {
            return ZeroTypeMsg(msg.substring(2).toInt())
        }
    }
}
