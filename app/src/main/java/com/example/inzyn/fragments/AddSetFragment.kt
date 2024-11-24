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
import com.example.inzyn.databinding.FragmentAddSetBinding
import com.example.inzyn.model.AddSetType
import com.example.inzyn.viewmodel.AddSetViewModel

private const val TYPE_KEY = "type"

class AddSetFragment : Fragment() {
    private lateinit var binding: FragmentAddSetBinding
    private val viewModel: AddSetViewModel by viewModels()
    private lateinit var type: AddSetType
    private var exerciseId: Int? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            type = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                it.getSerializable(TYPE_KEY, AddSetType::class.java)
            } else {
                @Suppress("DEPRECATION")
                it.getSerializable(TYPE_KEY) as? AddSetType
            } ?: AddSetType.New
            exerciseId = it.getInt("exerciseID")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return FragmentAddSetBinding.inflate(inflater, container, false).also {
            binding = it
            binding.viewModel = viewModel
            binding.lifecycleOwner = viewLifecycleOwner
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        with(viewModel) {
            init((type as? AddSetType.Edit)?.id, exerciseId)
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
        val viewModel = ViewModelProvider(this)[AddSetViewModel::class.java]
        viewModel.onSave()
    }

}