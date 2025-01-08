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
import com.example.inzyn.databinding.FragmentAddPlanBinding
import com.example.inzyn.model.AddPlanType
import com.example.inzyn.model.Exercise
import com.example.inzyn.viewmodel.AddPlanViewModel

private const val TYPE_KEY = "type"

class AddPlanFragment : Fragment() {

    private lateinit var binding: FragmentAddPlanBinding
    private val viewModel: AddPlanViewModel by viewModels()

    private lateinit var exerciseListAdapter: ExerciseListAdapter
    private var type: AddPlanType = AddPlanType.New

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            type = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                it.getSerializable(TYPE_KEY, AddPlanType::class.java)
            } else {
                @Suppress("DEPRECATION")
                it.getSerializable(TYPE_KEY) as? AddPlanType
            } ?: AddPlanType.New
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddPlanBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.init((type as? AddPlanType.Edit)?.id)

        exerciseListAdapter = ExerciseListAdapter(
            onItemClick = {},
            onItemLongClick = { position ->
                val selectedExercise: Exercise = exerciseListAdapter.exerciseList[position]
                confirmDeleteExercise(selectedExercise)
            },
            addSet = { position ->
                val selectedExercise = exerciseListAdapter.exerciseList[position]
                navigateToAddSetFragment(selectedExercise)
            },
            stats = {}
        )

        binding.exerciseList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = exerciseListAdapter
        }

        viewModel.exercises.observe(viewLifecycleOwner) { newList ->
            exerciseListAdapter.exerciseList = newList
        }
        viewModel.navigation.observe(viewLifecycleOwner) {
            it.resolve(findNavController())
        }
    }

    private fun confirmDeleteExercise(exercise: Exercise) {
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.delete_exercise))
            .setMessage(
                getString(R.string.do_you_want_to_delete) + " " +
                        exercise.name + " " +
                        getString(R.string.from_plan)
            )
            .setNegativeButton(getString(R.string.Delete)) { dialog, _ ->
                viewModel.removeExercise(exercise.id)
                dialog.dismiss()
            }
            .setNeutralButton(getString(R.string.Cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun navigateToAddSetFragment(exercise: Exercise) {
        val bundle = Bundle().apply {
            putString("exerciseID", exercise.id)
        }
        findNavController().navigate(R.id.action_addPlanFragment_to_addSetFragment, bundle)
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
