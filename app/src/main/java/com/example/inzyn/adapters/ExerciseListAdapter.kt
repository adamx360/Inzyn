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
    private var localId: Int = 0
        private set

    fun onBind(
        exerciseItem: Exercise,
        onItemClick: () -> Unit,
        onItemLongClick: () -> Unit,
        addSet: () -> Unit,
        stats: () -> Unit
    ) {
        with(binding) {
            val intId = exerciseItem.id.toIntOrNull() ?: 0
            localId = intId
            name.text = exerciseItem.name
            root.setOnClickListener { onItemClick() }
            root.setOnLongClickListener {
                onItemLongClick()
                true
            }
            addSetButton.setOnClickListener { addSet() }
            statsButton.setOnClickListener { stats() }
        }
    }
}

class ExerciseListAdapter(
    private val onItemClick: (Int) -> Unit,
    private val onItemLongClick: (Int) -> Unit,
    private val addSet: (Int) -> Unit,
    private val stats: (Int) -> Unit
) : RecyclerView.Adapter<ExerciseItem>() {
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
        val exercise = exerciseList[position]
        holder.onBind(
            exercise,
            onItemClick = { onItemClick(position) },
            onItemLongClick = { onItemLongClick(position) },
            addSet = { addSet(position) },
            stats = { stats(position) }
        )
    }
}
