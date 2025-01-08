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
    private var localId: Int = 0
        private set
    private var date: String = ""
        private set

    fun onBind(
        setItem: Set,
        onItemClick: () -> Unit,
        onItemLongClick: () -> Unit
    ) {
        with(binding) {
            val intId = setItem.id.toIntOrNull() ?: 0
            localId = intId
            date = setItem.date

            exerciseName.text = setItem.exerciseName
            weight.text = setItem.weight.toString()
            reps.text = setItem.reps.toString()

            root.setOnClickListener { onItemClick() }
            root.setOnLongClickListener {
                onItemLongClick()
                true
            }
        }
    }
}

class SetListAdapter(
    private val onItemClick: (Int) -> Unit,
    private val onItemLongClick: (Int) -> Unit
) : RecyclerView.Adapter<SetItem>() {
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
        val item = setList[position]
        holder.onBind(
            item,
            onItemClick = { onItemClick(position) },
            onItemLongClick = { onItemLongClick(position) }
        )
    }
}
