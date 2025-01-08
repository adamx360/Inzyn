package com.example.inzyn.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.inzyn.R
import com.example.inzyn.adapters.ExerciseListAdapter
import com.example.inzyn.databinding.FragmentListBinding
import com.example.inzyn.model.Exercise
import com.example.inzyn.viewmodel.ListViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ListFragment : Fragment() {
    private lateinit var binding: FragmentListBinding
    private lateinit var exerciseListAdapter: ExerciseListAdapter
    private val viewModel: ListViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentListBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        exerciseListAdapter = ExerciseListAdapter(
            onItemClick = { position ->
                viewModel.onEditExercise(exerciseListAdapter.exerciseList[position])
            },
            onItemLongClick = { position ->
                val selectedExercise: Exercise = exerciseListAdapter.exerciseList[position]
                AlertDialog.Builder(requireContext())
                    .setTitle(getString(R.string.edit_exercise))
                    .setMessage(
                        getString(R.string.add_to_plan_or_delete) + " " + selectedExercise.name + "?"
                    )
                    .setPositiveButton(getString(R.string.Add)) { dialog, _ ->
                        showDaySelectionDialog(selectedExercise.id)
                        dialog.dismiss()
                    }
                    .setNegativeButton(getString(R.string.Delete)) { dialog, _ ->
                        viewModel.onExerciseRemove(selectedExercise.id)
                        dialog.dismiss()
                    }
                    .setNeutralButton(getString(R.string.Cancel)) { dialog, _ ->
                        dialog.dismiss()
                    }
                    .show()
            },
            addSet = { position ->
                val exerciseId = exerciseListAdapter.exerciseList[position]
                navigateToAddSetFragment(exerciseId)
            },
            stats = { position ->
                val selectedExercise = exerciseListAdapter.exerciseList[position]
                showExerciseStatisticsDialog(selectedExercise)
            }
        )

        binding.exerciseList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = exerciseListAdapter
        }

        viewModel.exercises.observe(viewLifecycleOwner) {
            println("Loaded gyms: $it")
            exerciseListAdapter.exerciseList = it
        }

        binding.buttonFirst.setOnClickListener {
            findNavController().navigate(R.id.action_listFragment_to_calendarFragment)
        }
        binding.addExercise.setOnClickListener {
            viewModel.onAddExercise()
        }
        binding.floatingClock.setOnClickListener {
            findNavController().navigate(R.id.action_listFragment_to_clockFragment)
        }
        binding.planNavButton.setOnClickListener {
            findNavController().navigate(R.id.action_listFragment_to_planFragment)
        }
        binding.todayNavButton.setOnClickListener {
            findNavController().navigate(R.id.action_listFragment_to_todayPlanFragment)
        }
        viewModel.navigation.observe(viewLifecycleOwner) {
            it.resolve(findNavController())
        }
    }

    private fun showDaySelectionDialog(exerciseId: String) {
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.Add_to_plan))
            .setItems(
                arrayOf(
                    getString(R.string.Monday),
                    getString(R.string.Tuesday),
                    getString(R.string.Wednesday),
                    getString(R.string.Thursday),
                    getString(R.string.Friday),
                    getString(R.string.Saturday),
                    getString(R.string.Sunday)
                )
            ) { dialog, which ->
                val planDayId = which + 1
                viewModel.addExerciseToPlan(exerciseId, planDayId.toString())
                dialog.dismiss()
            }
            .setNegativeButton(getString(R.string.Cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun showExerciseStatisticsDialog(exercise: Exercise) {
        lifecycleScope.launch(Dispatchers.IO) {
            val sets = viewModel.getSetsForExercise(exercise.id)
            val totalSets = sets.size
            val totalVolume = sets.sumOf { it.weight * it.reps }
            val averageVolume = if (totalSets > 0) totalVolume / totalSets else 0.0

            val messageStats = getString(R.string.Stats_for_exercise) + " " + exercise.name
            val messageTotalSets = getString(R.string.Total_sets) + " " + totalSets
            val messageSetsTotalVolume = getString(R.string.Total_vol) + " " + totalVolume
            val messageAvgVolume = getString(R.string.Avg_vol) + " " + averageVolume

            withContext(Dispatchers.Main) {
                AlertDialog.Builder(requireContext())
                    .setTitle(getString(R.string.Exercise_stats))
                    .setMessage(
                        messageStats + "\n" +
                                messageTotalSets + "\n" +
                                messageSetsTotalVolume + "\n" +
                                messageAvgVolume
                    )
                    .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
                    .show()
            }
        }
    }

    private fun navigateToAddSetFragment(exercise: Exercise) {
        val bundle = Bundle().apply {
            putString("exerciseID", exercise.id)
        }
        findNavController().navigate(R.id.action_listFragment_to_addSetFragment, bundle)
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