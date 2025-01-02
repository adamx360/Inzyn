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
        return FragmentListBinding.inflate(inflater, container, false).also {
            binding = it
            binding.viewModel = viewModel
            binding.lifecycleOwner = viewLifecycleOwner
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        exerciseListAdapter = ExerciseListAdapter(
            onItemClick = { position ->
                viewModel.onEditExercise(exerciseListAdapter.exerciseList[position])
            },
            onItemLongClick = { position ->
                val selectedExercise: Exercise = exerciseListAdapter.exerciseList[position]
                AlertDialog.Builder(requireContext())
                    .setTitle(String.format(getString(R.string.edit_exercise)))
                    .setMessage(String.format(getString(R.string.add_to_plan_or_delete)) + " " + selectedExercise.name + "?")
                    .setPositiveButton(String.format(getString(R.string.Add))) { dialog, _ ->
                        showDaySelectionDialog(selectedExercise.id)
                        dialog.dismiss()
                    }
                    .setNegativeButton(String.format(getString(R.string.Delete))) { dialog, _ ->
                        viewModel.onExerciseRemove(selectedExercise.id)
                        dialog.dismiss()
                    }
                    .setNeutralButton(String.format(getString(R.string.Cancel))) { dialog, _ ->
                        dialog.dismiss()
                    }
                    .show()
            },
            addSet = { position ->
                val exerciseId = exerciseListAdapter.exerciseList[position]
                navigateToAddSetFragment(exerciseId)
            },
            stats = { position ->
                val selectedExercise: Exercise = exerciseListAdapter.exerciseList[position]
                showExerciseStatisticsDialog(selectedExercise)
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

        binding.buttonFirst.setOnClickListener {
            findNavController().navigate(R.id.action_listFragment_to_calendarFragment)
        }

        binding.addExercise.setOnClickListener {
            viewModel.onAddExercise()
        }

        viewModel.navigation.observe(viewLifecycleOwner) {
            it.resolve(findNavController())
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
    }

    private fun showExerciseStatisticsDialog(exercise: Exercise) {
        lifecycleScope.launch(Dispatchers.IO) {
            val sets = viewModel.getSetsForExercise(exercise.id)
            val totalSets = sets.size
            val totalVolume = sets.sumOf { it.weight * it.reps }
            val averageVolume = if (totalSets > 0) totalVolume / totalSets else 0.0

            val messageStats =
                String.format(getString(R.string.Stats_for_exercise) + " " + exercise.name)
            val messageTotalSets =
                String.format(getString(R.string.Total_sets) + " " + totalSets)
            val messageSetsTotalVolume =
                String.format(getString(R.string.Total_vol) + " " + totalVolume)
            val messageAvgVolume =
                String.format(getString(R.string.Avg_vol) + " " + averageVolume)


//            val statisticsMessage = """
//            Statystyki dla ćwiczenia: ${exercise.name}
//            Łączna liczba serii: $totalSets
//            Łączna objętość: %.2f kg
//            Średnia objętość na serię: %.2f kg
//        """.trimIndent().format(totalVolume, averageVolume)

            withContext(Dispatchers.Main) {
                AlertDialog.Builder(requireContext())
                    .setTitle(String.format(getString(R.string.Exercise_stats)))
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


    private fun showDaySelectionDialog(exerciseId: String) {
        AlertDialog.Builder(requireContext())
            .setTitle(String.format(getString(R.string.Add_to_plan)))
            .setItems(
                arrayOf(
                    String.format(getString(R.string.Monday)),
                    String.format(getString(R.string.Tuesday)),
                    String.format(getString(R.string.Wednesday)),
                    String.format(getString(R.string.Thursday)),
                    String.format(getString(R.string.Friday)),
                    String.format(getString(R.string.Saturday)),
                    String.format(getString(R.string.Sunday))
                )
            ) { dialog, which ->
                val planDayId = which + 1
                viewModel.addExerciseToPlan(exerciseId, planDayId.toString())
                dialog.dismiss()
            }
            .setNegativeButton(String.format(getString(R.string.Cancel))) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
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
