package com.example.inzyn.fragments

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.inzyn.databinding.FragmentAddSetBinding
import com.example.inzyn.model.AddSetType
import com.example.inzyn.viewmodel.AddSetViewModel

private const val TYPE_KEY = "type"

class AddSetFragment : Fragment() {
    private lateinit var binding: FragmentAddSetBinding
    private val viewModel: AddSetViewModel by viewModels()
    private lateinit var type: AddSetType
    private var exerciseId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            type = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                it.getSerializable(TYPE_KEY, AddSetType::class.java)
            } else {
                @Suppress("DEPRECATION")
                it.getSerializable(TYPE_KEY) as? AddSetType
            } ?: AddSetType.New

            exerciseId = it.getString("exerciseID")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddSetBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.init((type as? AddSetType.Edit)?.id, exerciseId)
        viewModel.navigation.observe(viewLifecycleOwner) {
            it.resolve(findNavController())
        }

        setupButtons()
    }

    private fun setupButtons() {
        binding.button.setOnClickListener {
            saveFormData()
        }
    }

    private fun saveFormData() {
        viewModel.onSave()
    }
}