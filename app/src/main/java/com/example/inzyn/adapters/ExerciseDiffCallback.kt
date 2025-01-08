package com.example.inzyn.adapters

import androidx.recyclerview.widget.DiffUtil
import com.example.inzyn.model.Exercise

class ExerciseDiffCallback(
    private val old: List<Exercise>,
    private val new: List<Exercise>
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int = old.size
    override fun getNewListSize(): Int = new.size
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = old[oldItemPosition]
        val newItem = new[newItemPosition]
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = old[oldItemPosition]
        val newItem = new[newItemPosition]
        return oldItem == newItem
    }
}
