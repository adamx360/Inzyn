package com.example.inzyn.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.inzyn.databinding.IntervalClocksBinding
import com.example.inzyn.viewmodel.ClockViewModel

class ClockFragment : Fragment() {
    private lateinit var binding: IntervalClocksBinding
    private val viewModel: ClockViewModel by viewModels()
    private var minutes: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return IntervalClocksBinding.inflate(inflater, container, false).also {
            binding = it
            binding.viewModel = viewModel
            binding.lifecycleOwner = viewLifecycleOwner
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fun updateTimeDisplay() {
            val timerTextView = binding.timerDisplay
            timerTextView.text = String.format("%02d:%02d", minutes / 60, minutes % 60)

        }

        updateTimeDisplay()

        binding.plusButton.setOnClickListener {
            minutes += 1
            updateTimeDisplay()
        }

        binding.minusButton.setOnClickListener {
            if (minutes > 0) {
                minutes -= 1
                updateTimeDisplay()
            }
        }

        binding.stratButton.setOnClickListener {
            viewModel.startTimer(minutes)
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

