package com.example.inzyn.fragments

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.inzyn.R
import com.example.inzyn.adapters.ExerciseListAdapter
import com.example.inzyn.databinding.FragmentTodayPlanBinding
import com.example.inzyn.model.Exercise
import com.example.inzyn.viewmodel.TodayPlanViewModel

class TodayPlanFragment : Fragment() {
    private lateinit var binding: FragmentTodayPlanBinding
    private val viewModel: TodayPlanViewModel by viewModels()
    private lateinit var exerciseListAdapter: ExerciseListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return FragmentTodayPlanBinding.inflate(inflater, container, false).also {
            binding = it
            binding.viewModel = viewModel
            binding.lifecycleOwner = viewLifecycleOwner
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        with(viewModel) {
            init()
            navigation.observe(viewLifecycleOwner) {
                it.resolve(findNavController())
            }
        }

        exerciseListAdapter = ExerciseListAdapter(
            onItemClick = {},
            onItemLongClick = { position ->
                val selectedExercise: Exercise = exerciseListAdapter.exerciseList[position]
                AlertDialog.Builder(requireContext())
                    .setTitle("Usuń ćwiczenie")
                    .setMessage("Chcesz usunąć ${selectedExercise.name} z planu?")
                    .setNegativeButton("Usuń") { dialog, _ ->
                        viewModel.removeExerciseFromPlan(selectedExercise.id)
                        dialog.dismiss()
                    }
                    .setNeutralButton("Anuluj") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .show()
            },
            addSet = { position ->
                val exerciseId = exerciseListAdapter.exerciseList[position]
                navigateToAddSetFragment(exerciseId)
            }
        )

        binding.exerciseList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = exerciseListAdapter
        }

        viewModel.exercises.observe(viewLifecycleOwner) {
            println("Loaded gyms: $it") // Debugowanie danych
            exerciseListAdapter.exerciseList = it
        }

        viewModel.navigation.observe(viewLifecycleOwner) {
            it.resolve(findNavController())
        }
    }

    private fun navigateToAddSetFragment(exercise: Exercise) {
        val bundle = Bundle().apply {
            putInt("exerciseID", exercise.id)
        }
        findNavController().navigate(R.id.action_todayPlanFragment_to_addSetFragment, bundle)
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