package com.example.inzyn.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.inzyn.R
import com.example.inzyn.databinding.IntervalClocksBinding
import com.example.inzyn.viewmodel.ClockViewModel

class ClockFragment : Fragment() {
    private lateinit var binding: IntervalClocksBinding
    private val viewModel: ClockViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = IntervalClocksBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        viewModel.remainingTime.observe(viewLifecycleOwner) { time ->
            binding.timerDisplay.text = time
        }

        viewModel.isRunning.observe(viewLifecycleOwner) { isRunning ->
            if (isRunning) {
                Toast.makeText(requireContext(), R.string.timer_finished, Toast.LENGTH_SHORT).show()
                enableButtons(true)
            }
        }

        binding.stopButton.setOnClickListener {
            viewModel.stopTimer()
        }
        binding.plusSecButton.setOnClickListener {
            viewModel.addTime(10)
        }
        binding.minusSecButton.setOnClickListener {
            viewModel.subtractTime(10)
        }
        binding.plusMinButton.setOnClickListener {
            viewModel.addTime(60)
        }
        binding.minusMinButton.setOnClickListener {
            viewModel.subtractTime(60)
        }
        binding.stratButton.setOnClickListener {
            viewModel.startTimer()
            enableButtons(false)
        }
        binding.resetButton.setOnClickListener {
            viewModel.resetTimer()
            enableButtons(true)
        }
    }

    private fun enableButtons(isEnabled: Boolean) {
        binding.plusSecButton.isEnabled = isEnabled
        binding.minusSecButton.isEnabled = isEnabled
        binding.plusMinButton.isEnabled = isEnabled
        binding.minusMinButton.isEnabled = isEnabled
    }
}
