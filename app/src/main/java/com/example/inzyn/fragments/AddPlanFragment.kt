package com.example.inzyn.fragments

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.inzyn.databinding.FragmentAddPlanBinding
import com.example.inzyn.model.AddPlanType
import com.example.inzyn.viewmodel.AddPlanViewModel

class AddPlanFragment : Fragment() {
    private lateinit var binding: FragmentAddPlanBinding
    private val viewModel: AddPlanViewModel by viewModels()
    private lateinit var type: AddPlanType
}