package com.example.inzyn.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.inzyn.R
import com.example.inzyn.adapters.GymListAdapter
import com.example.inzyn.databinding.FragmentPlanBinding
import com.example.inzyn.viewmodel.PlanViewModel
import com.example.inzyn.model.Gym

class PlanFragment : Fragment() {
    private lateinit var binding: FragmentPlanBinding
    private lateinit var gymListAdapter: GymListAdapter
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

        gymListAdapter = GymListAdapter(
            onItemClick = { position -> gymListAdapter.gymList[position]
            },
            onItemLongClick = { position ->
                val selectedExercise: Gym = gymListAdapter.gymList[position]
                AlertDialog.Builder(requireContext())
                    .setTitle("Usuń przedmiot")
                    .setMessage("Czy napewno chcesz usunąć ${selectedExercise.name}?")
                    .setPositiveButton("Usuń") { dialog, _ ->
                        viewModel.onGymRemove(selectedExercise.id)
                        dialog.dismiss()
                    }
                    .setNegativeButton("Anuluj") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .show()
            }
        )

        binding.exerciseList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = gymListAdapter
        }

        viewModel.gyms.observe(viewLifecycleOwner) {
            println("Loaded gyms: $it") // Debugowanie danych
            gymListAdapter.gymList = it
        }

        binding.buttonFirst.setOnClickListener {
            findNavController().navigate(R.id.action_planFragment_to_calendarFragment)
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
