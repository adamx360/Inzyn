package com.example.inzyn.adapters

import androidx.recyclerview.widget.DiffUtil
import com.example.inzyn.model.Set

class SetDiffCallback(
    private val old: List<Set>,
    private val new: List<Set>
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
