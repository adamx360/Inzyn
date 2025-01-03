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
        return IntervalClocksBinding.inflate(inflater, container, false).also {
            binding = it
            binding.viewModel = viewModel
            binding.lifecycleOwner = viewLifecycleOwner
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.remainingTime.observe(viewLifecycleOwner) { time ->
            binding.timerDisplay.text = time
        }

        viewModel.isRunning.observe(viewLifecycleOwner) { isRunning ->
            if (isRunning) {
                Toast.makeText(requireContext(), R.string.timer_finished, Toast.LENGTH_SHORT).show()
//                context?.let { viewModel.playSound(it) }
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

    fun enableButtons(isWorking: Boolean) {
        binding.plusSecButton.isEnabled = isWorking
        binding.minusSecButton.isEnabled = isWorking
        binding.minusMinButton.isEnabled = isWorking
        binding.plusMinButton.isEnabled = isWorking
    }

}

