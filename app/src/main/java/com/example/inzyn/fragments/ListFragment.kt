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
import com.example.inzyn.adapters.ExerciseListAdapter
import com.example.inzyn.databinding.FragmentListBinding
import com.example.inzyn.viewmodel.ListViewModel
import com.example.inzyn.model.Exercise

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
            onItemClick = { position -> viewModel.onEditExercise(exerciseListAdapter.exerciseList[position])
            },
            onItemLongClick = { position ->
                val selectedExercise: Exercise = exerciseListAdapter.exerciseList[position]
                AlertDialog.Builder(requireContext())
                    .setTitle("Usuń przedmiot")
                    .setMessage("Czy napewno chcesz usunąć ${selectedExercise.name}?")
                    .setPositiveButton("Usuń") { dialog, _ ->
                        viewModel.onExerciseRemove(selectedExercise.id)
                        dialog.dismiss()
                    }
                    .setNegativeButton("Anuluj") { dialog, _ ->
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
    }

    private fun navigateToAddSetFragment(exercise: Exercise) {
        val bundle = Bundle().apply {
            putInt("exerciseID", exercise.id)
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
