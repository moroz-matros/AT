package com.example.at.views.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.at.databinding.FragmentTrainingSettingsBinding
import com.jjoe64.graphview.series.LineGraphSeries

import android.R
import android.provider.ContactsContract
import com.example.at.databinding.FragmentTrainingBinding

import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.series.DataPoint


class TrainingFragment : Fragment() {
    private var _binding: FragmentTrainingBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTrainingBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        setUpBindings()
        val graph = binding.graph
        val series: LineGraphSeries<DataPoint> = LineGraphSeries(
            arrayOf(

                DataPoint((26365504).toDouble(),  (142).toDouble()),
                DataPoint((26366056).toDouble(),  (808).toDouble()),
                DataPoint((26625784).toDouble(),  (148).toDouble()),
                DataPoint((26626552).toDouble(),  (221).toDouble()),
                DataPoint((26627328).toDouble(),  (391).toDouble()),
                DataPoint((26628100).toDouble(),  (315).toDouble()),
                DataPoint((26628880).toDouble(),  (142).toDouble()),

                DataPoint((65121928).toDouble(),  (322).toDouble()),
                DataPoint((65122704).toDouble(),  (610).toDouble()),
                DataPoint((65123472).toDouble(),  (188).toDouble()),
                DataPoint((65415224).toDouble(),  (215).toDouble()),
                DataPoint((65415992).toDouble(),  (686).toDouble()),

            )
        )

        graph.addSeries(series)
    }

    private fun setUpBindings(){
        binding.buttonSend.setOnClickListener {
            (activity as MainActivity).connectedThread!!.write("1")
        }
        binding.buttonFinish.setOnClickListener {
            (activity as MainActivity).connectedThread!!.write("0")
        }
    }


}