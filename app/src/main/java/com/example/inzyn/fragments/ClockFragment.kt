package com.example.inzyn.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
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


        viewModel.remainingTime.observe(viewLifecycleOwner){ time ->
            binding.timerDisplay.text = time

        }

        viewModel.isRunning.observe(viewLifecycleOwner){ isRunning ->
            if(isRunning){
                Toast.makeText(requireContext(), "Timer Finished", Toast.LENGTH_SHORT).show()
            }

        }

        binding.plusButton.setOnClickListener {
            viewModel.addTime(10)
        }

        binding.minusButton.setOnClickListener {
            viewModel.subtractTime(10)
        }

        binding.stratButton.setOnClickListener {
            viewModel.startTimer()
        }

    }
}

