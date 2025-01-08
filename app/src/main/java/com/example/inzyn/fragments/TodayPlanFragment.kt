package com.example.inzyn.fragments

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
        binding = FragmentTodayPlanBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.init()
        viewModel.navigation.observe(viewLifecycleOwner) {
            it.resolve(findNavController())
        }

        exerciseListAdapter = ExerciseListAdapter(
            onItemClick = {},
            onItemLongClick = { position ->
                val selectedExercise: Exercise = exerciseListAdapter.exerciseList[position]
                AlertDialog.Builder(requireContext())
                    .setTitle(getString(R.string.delete_exercise))
                    .setMessage(
                        getString(R.string.do_you_want_to_delete) + " " +
                                selectedExercise.name +
                                getString(R.string.from_plan) + "?"
                    )
                    .setNegativeButton(getString(R.string.Delete)) { dialog, _ ->
                        viewModel.removeExerciseFromPlan(selectedExercise.id)
                        dialog.dismiss()
                    }
                    .setNeutralButton(getString(R.string.Cancel)) { dialog, _ ->
                        dialog.dismiss()
                    }
                    .show()
            },
            addSet = { position ->
                val exercise = exerciseListAdapter.exerciseList[position]
                navigateToAddSetFragment(exercise)
            },
            stats = {}
        )

        binding.exerciseList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = exerciseListAdapter
        }

        viewModel.exercises.observe(viewLifecycleOwner) { newList ->
            println("Loaded exercises: $newList")
            exerciseListAdapter.exerciseList = newList
        }
    }

    private fun navigateToAddSetFragment(exercise: Exercise) {
        val bundle = Bundle().apply {
            putString("exerciseID", exercise.id)
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
