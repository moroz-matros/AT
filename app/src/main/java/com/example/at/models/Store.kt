package com.example.at.models

class Store {
    companion object {
        var currentSettings: TrainingSettingsModel? = null
        var currentTraining = TrainingModel()
        var ArduinoMsg: String? = null

        var trainingMsg: String? = null

        fun parseZeroMsg(msg: String): ZeroTypeMsg {
            return ZeroTypeMsg(msg.substring(2).toInt())
        }

        fun parseSecondMsg(msg: String): SecondTypeMsg {
            val words = msg.split(" ")
            val answer = SecondTypeMsg()
            answer.result = words[0]
            answer.x = words[1].toInt()
            answer.y = words[2].toInt()
            answer.power = words[3].toInt()
            return answer
        }

    }
}
