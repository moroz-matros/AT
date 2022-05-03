package com.example.at.views.ui

import android.media.MediaPlayer
import android.os.Bundle
import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

import android.util.Log
import android.widget.Toast
import com.example.at.R
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
    private var mediaPlayer: MediaPlayer? = null

    private var onPause = true
    private var flag = true

    private val mutexTraining = Mutex()
    private val mutexDraw = Mutex()

    private var scrollToEnd = false
    private var count = 0.0
    private var maxX = 60.0
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
        while (flag){
            if (!onPause) {
                if (Store.ArduinoMsg != null) {
                    Log.d("AAAA", "got msg")
                    var msg: SecondTypeMsg
                    mutexTraining.withLock {
                        msg = Store.parseSecondMsg(Store.ArduinoMsg!!)
                        Store.ArduinoMsg = null
                    }
                    count = (System.currentTimeMillis() / 1000 - Store.currentTraining.timeStart).toDouble()
                    if (count >= maxX){
                        scrollToEnd = true
                    }
                    var y = 1.0
                    if (msg.result == "out") {
                        y = -1.0
                        Store.currentTraining!!.hitsOut += 1
                        //binding!!.textViewOut.text = Store.currentTraining!!.hitsOut.toString()
                        (context as MainActivity).runOnUiThread {
                            binding!!.textViewOut.text = Store.currentTraining!!.hitsOut.toString()
                        }
                        faults += 1
                        if (faults >= Store.currentTraining!!.maxErrors){
                            Store.currentTraining!!.concentrationFaults += 1
                            faults = 0
                        }
                    } else {
                        Store.currentTraining!!.hitsIn += 1
                        (context as MainActivity).runOnUiThread {
                            binding!!.textViewIn.text = Store.currentTraining!!.hitsIn.toString()
                        }
                        faults = 0
                    }
                    if (faults >= Store.currentTraining.maxErrors) {
                        faults = 0
                        mediaPlayer!!.start()
                    }
                    Store.currentTraining!!.hits.add(DataPoint(count, y))
                    points!!.appendData(DataPoint(count, y), scrollToEnd, 1000)
                    (context as MainActivity).runOnUiThread {
                        binding!!.textViewTotal.text =
                            (Store.currentTraining!!.hitsOut + Store.currentTraining!!.hitsOut).toString()
                        binding!!.graph.addSeries(points)
                    }
                    //
                }
            }

        }
    }


    override fun onStart() {
        super.onStart()
        setUpBindings()
        mediaPlayer = MediaPlayer.create(context, R.raw.beep)

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
    }

    private fun setUpBindings(){
        binding!!.buttonStart.setOnClickListener {
            Toast.makeText(context, "Start init", Toast.LENGTH_SHORT).show()
            onPause = false
            Log.d("QQQQQ", System.currentTimeMillis().toString())
            Store.currentTraining.timeStart = System.currentTimeMillis() / 1000
            (activity as MainActivity).connectedThread!!.write("1")
            binding!!.buttonStart.visibility = View.GONE
            binding!!.buttonPause.visibility = View.VISIBLE
        }

        binding!!.buttonFinish.setOnClickListener {
            (activity as MainActivity).connectedThread!!.write("4")
        }

        binding!!.buttonPause.setOnClickListener {
            (activity as MainActivity).connectedThread!!.write("2")
            onPause = true
            binding!!.buttonPause.visibility = View.GONE
            binding!!.buttonResume.visibility = View.VISIBLE
        }
        binding!!.buttonResume.setOnClickListener {
            (activity as MainActivity).connectedThread!!.write("3")
            onPause = false
            binding!!.buttonPause.visibility = View.VISIBLE
            binding!!.buttonResume.visibility = View.GONE
        }



        binding!!.buttonFinish.setOnClickListener {
            (activity as MainActivity).connectedThread!!.write("4")
            flag = false
        }

        /*
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

         */
    }


}