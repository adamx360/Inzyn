package com.example.inzyn.fragments

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.inzyn.databinding.FragmentAddExerciseBinding
import com.example.inzyn.model.AddExerciseType
import com.example.inzyn.viewmodel.AddExerciseViewModel

private const val TYPE_KEY = "type"

class AddExerciseFragment : Fragment() {
    private lateinit var binding: FragmentAddExerciseBinding
    private val viewModel by viewModels<AddExerciseViewModel>()
    private lateinit var type: AddExerciseType


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            type = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                it.getSerializable(TYPE_KEY, AddExerciseType::class.java)
            } else {
                @Suppress("DEPRECATION")
                it.getSerializable(TYPE_KEY) as? AddExerciseType
            } ?: AddExerciseType.New
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return FragmentAddExerciseBinding.inflate(inflater, container, false).also {
            binding = it
            binding.viewModel = viewModel
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        with(viewModel) {
            init((type as? AddExerciseType.Edit)?.id)
            navigation.observe(viewLifecycleOwner) {
                it.resolve(findNavController())
            }
        }

        setupButtons()
    }

    private fun setupButtons() {
        binding.button.setOnClickListener {
            saveFormData()
        }
    }

    private fun saveFormData() {
        val viewModel = ViewModelProvider(this)[AddExerciseViewModel::class.java]
        viewModel.onSave()
    }

}