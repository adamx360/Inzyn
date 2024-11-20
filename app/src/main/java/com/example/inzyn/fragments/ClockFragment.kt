package com.example.inzyn.fragments

import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.inzyn.databinding.IntervalClocksBinding
import com.example.inzyn.viewmodel.PlanViewModel

class ClockFragment : Fragment() {
    private var minutes: Int = 0
    val timerTextView = binding.timerDisplay

    private lateinit var binding: IntervalClocksBinding
    private val viewModel: PlanViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return IntervalClocksBinding.inflate(inflater, container, false).also {
            binding = it
            binding.viewModel = viewModel
            binding.lifecycleOwner = viewLifecycleOwner
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        fun updateTimeDisplay() {

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
            if (minutes > 0) {
                val totalMilliseconds = minutes * 60 * 1000L

                val timer = object : CountDownTimer(totalMilliseconds, 1000) {
                    override fun onTick(millisUntilFinished: Long) {
                        val remainingMinutes = (millisUntilFinished / 1000) / 60
                        val remainingSeconds = (millisUntilFinished / 1000) % 60
                        timerTextView.text =
                            String.format("%02d:%02d", remainingMinutes, remainingSeconds)

                    }

                    override fun onFinish() {
                        timerTextView.text = "00:00"
                        binding.stratButton.isEnabled = true
                    }
                }
                binding.stratButton.isEnabled = false
                timer.start()
            }
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