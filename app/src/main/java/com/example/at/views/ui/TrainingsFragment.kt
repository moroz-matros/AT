package com.example.at.views.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.at.databinding.FragmentTrainingsBinding
import com.example.at.models.AppDatabase
import com.example.at.models.Training
import com.example.at.views.adapters.TrainingCardAdapter

class TrainingsFragment : Fragment() {
    private var _binding: FragmentTrainingsBinding? = null

    private var adapter: TrainingCardAdapter? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTrainingsBinding.inflate(inflater, container, false)
        binding.rvTraining.layoutManager = LinearLayoutManager(this.context)
        adapter = TrainingCardAdapter(listOf(), activity as MainActivity)
        binding.rvTraining.adapter = adapter

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        var training = Training(context?.let { AppDatabase.getInstance(this.context)?.trainDao?.getAll()}!!)
        setUpBasic(training)
    }

    private fun setUpBasic(training: Training) {
        adapter!!.setData(training.trainings)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}