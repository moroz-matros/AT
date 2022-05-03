package com.example.at.views.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.at.R
import com.example.at.databinding.ItemTrainingBinding
import com.example.at.models.Store
import com.example.at.models.TrainingEntity
import com.example.at.views.ui.MainActivity
import com.example.at.views.ui.TrainingOneFragment

class TrainingCardAdapter (private var trainings: List<TrainingEntity>,
    private var activity: MainActivity) :
    RecyclerView.Adapter<TrainingCardAdapter.TrainingCardViewHolder>() {


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrainingCardViewHolder {
            val binding = ItemTrainingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return TrainingCardViewHolder(binding, activity)
        }

        fun setData(trainings: List<TrainingEntity>) {
            this.trainings = trainings
            notifyDataSetChanged()
        }

        override fun onBindViewHolder(holder: TrainingCardViewHolder, position: Int) {
            holder.bind(trainings[position])
        }

        override fun getItemCount() = trainings.size

        class TrainingCardViewHolder(
            private val binding: ItemTrainingBinding,
            private var activity: MainActivity,
        ) : RecyclerView.ViewHolder(binding.root) {
            private var current = TrainingEntity(0,0,0,0,0,
                0,0,0,"")

            fun bind(tr: TrainingEntity) {
                current = tr
                val sdf = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
                val date = java.util.Date((tr.date * 1000).toLong())
                val text = sdf.format(date)
                Log.d("DDD", text)

                binding.date.text = "date " + text
                binding.ins.text = "in " + tr.hitsIn.toString() + "; "
                binding.outs.text = "out " + tr.hitsOut.toString()+ "; "
                binding.concentrationFaults.text = "concentration faults " + tr.concentrationFaults.toString()+ "; "

                binding.itemTraining.setOnClickListener {
                    Store.currentTrainingOne = current
                    val f = TrainingOneFragment()
                    activity.supportFragmentManager.beginTransaction()
                        .replace(R.id.container, f).addToBackStack("list")
                        .commit()
                }
            }
        }
}