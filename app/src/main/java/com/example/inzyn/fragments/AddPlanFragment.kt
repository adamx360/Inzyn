package com.example.inzyn.fragments

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
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
    private lateinit var type: AddPlanType
    private lateinit var exerciseListAdapter: ExerciseListAdapter

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
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return FragmentAddPlanBinding.inflate(inflater, container, false).also {
            binding = it
            binding.viewModel = viewModel
            binding.lifecycleOwner = viewLifecycleOwner
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        with(viewModel) {
            init((type as? AddPlanType.Edit)?.id)
            navigation.observe(viewLifecycleOwner) {
                it.resolve(findNavController())
            }
        }

        exerciseListAdapter = ExerciseListAdapter(
            onItemClick = {},
            onItemLongClick = { position ->
                val selectedExercise: Exercise = exerciseListAdapter.exerciseList[position]
                AlertDialog.Builder(requireContext())
                    .setTitle(String.format(getString(R.string.delete_exercise)))
                    .setMessage(String.format(getString(R.string.do_you_want_to_delete)) + " " + selectedExercise.name + " " + String.format(getString(R.string.from_plan)) )
                    .setNegativeButton(String.format(getString(R.string.Delete))) { dialog, _ ->
                        viewModel.removeExerciseFromPlan(selectedExercise.id)
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
//                showExerciseStatisticsDialog(selectedExercise)
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

        setupButtons()
    }

    private fun navigateToAddSetFragment(exercise: Exercise) {
        val bundle = Bundle().apply {
            putInt("exerciseID", exercise.id)
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

    private fun setupButtons() {
        binding.button.setOnClickListener {
            saveFormData()
        }
    }

    private fun saveFormData() {
        val viewModel = ViewModelProvider(this)[AddPlanViewModel::class.java]
        viewModel.onSave()
    }
}