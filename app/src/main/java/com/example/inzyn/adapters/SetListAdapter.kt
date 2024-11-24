package com.example.inzyn.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.inzyn.databinding.SetItemBinding
import com.example.inzyn.model.Set

class SetItem(
    private val binding: SetItemBinding
) : RecyclerView.ViewHolder(binding.root) {
    var id: Int = 0
        private set
    var date: String = ""
        private set
    fun onBind(setItem: Set, onItemClick: () -> Unit, onItemLongClick: () -> Unit) {
        with(binding) {
            id = setItem.id
            exerciseName.text = setItem.exerciseName
            weight.text = setItem.weight.toString()
            reps.text = setItem.reps.toString()
            date = setItem.date

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

class SetListAdapter (
    private val onItemClick: (Int) -> Unit,
    private val onItemLongClick: (Int) -> Unit
) : RecyclerView.Adapter<SetItem>(){
    var setList: List<Set> = emptyList()
        set(value) {
            val diffs = DiffUtil.calculateDiff(SetDiffCallback(field, value))
            field = value
            diffs.dispatchUpdatesTo(this)
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SetItem {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = SetItemBinding.inflate(layoutInflater, parent, false)
        return SetItem(binding)
    }

    override fun getItemCount(): Int = setList.size

    override fun onBindViewHolder(holder: SetItem, position: Int) {
        holder.onBind(
            setList[position],
            onItemClick = { onItemClick(position) },
            onItemLongClick = { onItemLongClick(position) }
        )
    }
}