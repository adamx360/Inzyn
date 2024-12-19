package com.example.inzyn.adapters

import androidx.recyclerview.widget.DiffUtil
import com.example.inzyn.model.Plan

class PlanDiffCallback(
    private val old: List<Plan>,
    private val new: List<Plan>
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int = old.size
    override fun getNewListSize(): Int = new.size
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        old[oldItemPosition] === new[newItemPosition]

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        old[oldItemPosition] == new[newItemPosition]
}