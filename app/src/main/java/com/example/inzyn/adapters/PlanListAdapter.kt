package com.example.inzyn.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.inzyn.databinding.PlanItemBinding
import com.example.inzyn.model.Plan

class PlanItem(
    private val binding: PlanItemBinding
) : RecyclerView.ViewHolder(binding.root) {
    private var localId: Int = 0
        private set

    fun onBind(
        planItem: Plan,
        onItemClick: () -> Unit
    ) {
        with(binding) {
            localId = planItem.id.toIntOrNull() ?: 0
            planName.text = planItem.name
            root.setOnClickListener { onItemClick() }
        }
    }
}

class PlanListAdapter(
    private val onItemClick: (Int) -> Unit
) : RecyclerView.Adapter<PlanItem>() {
    var planList: List<Plan> = emptyList()
        set(value) {
            val diffs = DiffUtil.calculateDiff(PlanDiffCallback(field, value))
            field = value
            diffs.dispatchUpdatesTo(this)
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlanItem {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = PlanItemBinding.inflate(layoutInflater, parent, false)
        return PlanItem(binding)
    }

    override fun getItemCount(): Int = planList.size

    override fun onBindViewHolder(holder: PlanItem, position: Int) {
        holder.onBind(
            planList[position],
            onItemClick = { onItemClick(position) }
        )
    }
}