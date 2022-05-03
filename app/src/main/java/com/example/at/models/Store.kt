package com.example.at.models

import android.util.Log
import kotlinx.coroutines.sync.Mutex

class Store {
    companion object {
        var arduinoMsgLock = Mutex()
        var currentSettings: TrainingSettingsModel? = null
        var currentTraining = TrainingModel()
        var currentTrainingOne = TrainingEntity(0,0,0,0,0,
        0,0,0,"")

        var ArduinoMsg: String? = null

        var trainingMsg: String? = null

        fun parseZeroMsg(msg: String): ZeroTypeMsg {
            val zero = ZeroTypeMsg()
            // we have msg 1000\n
            Log.d("num", msg.substring(2, msg.length-1))
            zero.noise = msg.substring(2, msg.length-1).toInt()
            return zero
        }

        fun parseSecondMsg(msg: String): SecondTypeMsg {
            val words = msg.split(" ")
            val answer = SecondTypeMsg()
            answer.result = words[1]
            answer.x = words[2].toInt()
            answer.y = words[3].substring(0,words[3].length-1).toInt()
            Log.d("answ y", answer.y.toString())
            return answer
        }

    }
}
