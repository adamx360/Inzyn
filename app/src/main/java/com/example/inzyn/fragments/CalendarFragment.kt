package com.example.inzyn.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.inzyn.R
import com.example.inzyn.adapters.SetListAdapter
import com.example.inzyn.databinding.FragmentCalendarBinding
import com.example.inzyn.model.Set
import com.example.inzyn.viewmodel.CalendarViewModel

class CalendarFragment : Fragment() {
    private lateinit var binding: FragmentCalendarBinding
    private lateinit var setListAdapter: SetListAdapter
    private val viewModel: CalendarViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCalendarBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding.buttonSecond.setOnClickListener {
            findNavController().navigate(R.id.action_calendarFragment_to_listFragment)
        }

        setListAdapter = SetListAdapter(
            onItemClick = { position ->
                viewModel.onEditSet(setListAdapter.setList[position])
            },
            onItemLongClick = { position ->
                val selectedSet: Set = setListAdapter.setList[position]
                AlertDialog.Builder(requireContext())
                    .setTitle(getString(R.string.delete_item))
                    .setMessage(
                        getString(R.string.sure_to_delete_series) + " " +
                                selectedSet.exerciseName + "?"
                    )
                    .setPositiveButton(getString(R.string.Delete)) { dialog, _ ->
                        viewModel.onSetRemove(selectedSet.id)
                        dialog.dismiss()
                    }
                    .setNegativeButton(getString(R.string.Cancel)) { dialog, _ ->
                        dialog.dismiss()
                    }
                    .show()
            }
        )

        binding.setList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = setListAdapter
        }

        viewModel.sets.observe(viewLifecycleOwner) {
            println("Loaded sets: $it")
            setListAdapter.setList = it
        }
        viewModel.navigation.observe(viewLifecycleOwner) {
            it.resolve(findNavController())
        }

        binding.calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val selectedDate = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth)
            viewModel.onDateSelected(selectedDate)
        }
    }

    override fun onStart() {
        super.onStart()
        findNavController().addOnDestinationChangedListener(viewModel::onDestinationChange)
    }

    override fun onStop() {
        findNavController().removeOnDestinationChangedListener(viewModel::onDestinationChange)
        super.onStop()
    }
}