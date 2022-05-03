package com.example.at.views.ui

import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.at.databinding.FragmentTrainingBinding
import com.example.at.databinding.FragmentTrainingOneBinding
import com.example.at.models.AppDatabase
import com.example.at.models.SecondTypeMsg
import com.example.at.models.Store
import com.example.at.models.TrainingEntity
import com.google.gson.Gson
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.PointsGraphSeries
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.*
import kotlin.reflect.typeOf

class TrainingOneFragment : Fragment() {
    private var _binding: FragmentTrainingOneBinding? = null
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
        _binding = FragmentTrainingOneBinding.inflate(inflater, container, false)

        return binding!!.root
    }

    fun <T> stringToArray(s: String?, clazz: Class<Array<T>>?): Array<T> {
        val arr = Gson().fromJson(s, clazz)!!
        return arr //or return Arrays.asList(new Gson().fromJson(s, clazz)); for a one-liner
    }

    override fun onStart() {
        super.onStart()

        binding!!.graph.viewport.isXAxisBoundsManual = true
        binding!!.graph.viewport.isScalable = true

        binding!!.graph.viewport.setMinX(0.0)
        binding!!.graph.viewport.setMaxX(maxX)

        binding!!.graph.viewport.isYAxisBoundsManual = true
        binding!!.graph.viewport.setMaxY(1.0)
        binding!!.graph.viewport.setMinY(-1.0)


        points!!.size = 10f

        val array = stringToArray(Store.currentTrainingOne.hits, Array<DataPoint>::class.java)

        for (elem in array){
            points!!.appendData(elem, false, 1000)
        }

        binding!!.graph.addSeries(points)

        binding!!.textTotalHits.text = (Store.currentTrainingOne.hitsIn + Store.currentTrainingOne.hitsOut).toString()
        binding!!.textHitsIn.text = Store.currentTrainingOne.hitsIn.toString()
        binding!!.textHitsOut.text = Store.currentTrainingOne.hitsOut.toString()

        binding!!.buttonDelete.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                AppDatabase.getInstance(context)?.trainDao?.delete(Store.currentTrainingOne.id)
            }
            Toast.makeText(context, "Deleted successfully", Toast.LENGTH_SHORT).show()
            requireActivity().supportFragmentManager.popBackStack();

        }
    }






}