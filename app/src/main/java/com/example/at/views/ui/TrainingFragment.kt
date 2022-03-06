package com.example.at.views.ui

import android.os.Bundle
import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

import android.util.Log
import com.example.at.databinding.FragmentTrainingBinding
import com.example.at.models.AppDatabase
import com.example.at.models.SecondTypeMsg
import com.example.at.models.Store
import com.example.at.models.TrainingEntity

import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.PointsGraphSeries
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.*
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers


class TrainingFragment : Fragment() {
    private var _binding: FragmentTrainingBinding? = null
    private val binding get() = _binding

    private val onPause = false

    private val mutexTraining = Mutex()
    private val mutexDraw = Mutex()

    private var scrollToEnd = false
    private var count = 0.0
    private var maxX = 30.0
    private var faults = 0


    private var points: PointsGraphSeries<DataPoint>? =  PointsGraphSeries<DataPoint>()




        override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTrainingBinding.inflate(inflater, container, false)

        return binding!!.root
    }

    private suspend fun observeMessages(){
        while (!onPause){
            if (Store.trainingMsg != null) {
                Log.d("AAAA", "got msg")
                var msg: SecondTypeMsg
                mutexTraining.withLock {
                    msg = Store.parseSecondMsg(Store.trainingMsg!!)
                    Store.trainingMsg = null
                }
                if (count >= maxX){
                    scrollToEnd = true
                }
                var y = 1.0
                if (msg.result == "out") {
                    y = -1.0
                    Store.currentTraining!!.hitsOut += 1
                    faults += 1
                    if (faults >= Store.currentTraining!!.maxErrors){
                        Store.currentTraining!!.concentrationFaults += 1
                        faults = 0
                    }
                } else {
                    Store.currentTraining!!.hitsIn += 1
                    faults = 0
                }
                Store.currentTraining!!.hits.add(DataPoint(count, y))
                points!!.appendData(DataPoint(count, y), scrollToEnd, 1000)
                binding!!.graph.addSeries(points)
                count +=10
            }
        }
    }

    private suspend fun sendIn(){
        mutexTraining.withLock {
            Store.trainingMsg = "in 10 10 888"
        }
    }

    private suspend fun sendOut(){
        mutexTraining.withLock {
            Store.trainingMsg = "out 10 10 888"
        }
    }

    private fun finishTraining(){

    }

    override fun onStart() {
        super.onStart()
        setUpBindings()

        GlobalScope.launch {
            observeMessages()
        }


        Store.currentTraining!!.maxErrors = 3
        points!!.size = 10f
        binding!!.graph.addSeries(points)
        binding!!.graph.viewport.isXAxisBoundsManual = true
        binding!!.graph.viewport.isScalable = true

        binding!!.graph.viewport.setMinX(0.0)
        binding!!.graph.viewport.setMaxX(maxX)

        binding!!.graph.viewport.isYAxisBoundsManual = true
        binding!!.graph.viewport.setMaxY(1.0)
        binding!!.graph.viewport.setMinY(-1.0)




        //(activity as MainActivity?)!!.connectedThread!!.write("1")
    }

    private fun setUpBindings(){
        /*
        binding!!.buttonSend.setOnClickListener {
            (activity as MainActivity).connectedThread!!.write("1")
        }

         */
        /*
        binding!!.buttonFinish.setOnClickListener {
            (activity as MainActivity).connectedThread!!.write("0")
        }

         */
        binding!!.buttonSend.setOnClickListener {
            GlobalScope.launch {
                sendIn()
            }
        }

        binding!!.buttonFinish.setOnClickListener {
            GlobalScope.launch {
                sendOut()
            }
        }

        binding!!.buttonPause.setOnClickListener {
            Store.currentTraining!!.x = 10
            Store.currentTraining!!.y = 10

            Store.currentTraining!!.maxErrors = 3
            val gson = Gson()
            val json = gson.toJson(Store.currentTraining!!.hits)
            Log.d("QQQQ", json)


            val tr = TrainingEntity(
                0, 0, Store.currentTraining!!.hitsIn,
                Store.currentTraining!!.hitsOut, Store.currentTraining!!.concentrationFaults,
                Store.currentTraining!!.x, Store.currentTraining!!.y,
                Store.currentTraining!!.maxErrors, json
            )
            CoroutineScope(Dispatchers.IO).launch {
                AppDatabase.getInstance(context)?.trainDao?.insert(tr)
            }

        }
    }


}