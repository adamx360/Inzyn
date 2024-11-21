package com.example.inzyn.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.inzyn.databinding.ExerciseItemBinding
import com.example.inzyn.model.Exercise

class ExerciseItem(
    private val binding: ExerciseItemBinding
) : RecyclerView.ViewHolder(binding.root) {
    var id: Int = 0
        private set

    fun onBind(exerciseItem: Exercise, onItemClick: () -> Unit, onItemLongClick: () -> Unit) {
        with(binding) {
            id = exerciseItem.id
            name.text = exerciseItem.name
            root.setOnClickListener {
                onItemClick()
            }
            root.setOnLongClickListener {
                onItemLongClick()
                return@setOnLongClickListener true
            }
        }
    }
}

class ExerciseListAdapter (
    private val onItemClick: (Int) -> Unit,
    private val onItemLongClick: (Int) -> Unit
) : RecyclerView.Adapter<ExerciseItem>(){
    var exerciseList: List<Exercise> = emptyList()
        set(value) {
            val diffs = DiffUtil.calculateDiff(ExerciseDiffCallback(field, value))
            field = value
            diffs.dispatchUpdatesTo(this)
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseItem {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ExerciseItemBinding.inflate(layoutInflater, parent, false)
        return ExerciseItem(binding)
    }

    override fun getItemCount(): Int = exerciseList.size

    override fun onBindViewHolder(holder: ExerciseItem, position: Int) {
        holder.onBind(
            exerciseList[position],
            onItemClick = { onItemClick(position) },
            onItemLongClick = { onItemLongClick(position) })
    }
}