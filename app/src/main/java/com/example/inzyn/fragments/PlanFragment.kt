package com.example.inzyn.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.inzyn.adapters.PlanListAdapter
import com.example.inzyn.databinding.FragmentPlanBinding
import com.example.inzyn.viewmodel.PlanViewModel

class PlanFragment : Fragment() {
    private lateinit var binding: FragmentPlanBinding
    private lateinit var planListAdapter: PlanListAdapter
    private val viewModel: PlanViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return FragmentPlanBinding.inflate(inflater, container, false).also {
            binding = it
            binding.viewModel = viewModel
            binding.lifecycleOwner = viewLifecycleOwner
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        planListAdapter = PlanListAdapter(
            onItemClick = { position ->
                viewModel.onEditPlan(planListAdapter.planList[position])
            }
        )

        binding.planList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = planListAdapter
        }

        viewModel.plans.observe(viewLifecycleOwner) {
            println("Loaded plans: $it") // Debugowanie danych
            planListAdapter.planList = it
        }

        viewModel.navigation.observe(viewLifecycleOwner) {
            it.resolve(findNavController())
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