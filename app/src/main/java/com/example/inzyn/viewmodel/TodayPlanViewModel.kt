package com.example.inzyn.viewmodel

import android.os.Bundle
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import com.example.inzyn.R
import com.example.inzyn.data.ExerciseRepository
import com.example.inzyn.data.PlanRepository
import com.example.inzyn.data.RepositoryLocator
import com.example.inzyn.model.Exercise
import com.example.inzyn.model.navigation.Destination
import com.example.inzyn.model.navigation.PopBack
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class TodayPlanViewModel : ViewModel() {
    private val repository: PlanRepository = RepositoryLocator.planRepository
    private val exerciseRepository: ExerciseRepository = RepositoryLocator.exerciseRepository
    val exercises = MutableLiveData<List<Exercise>>(emptyList())
    val name = MutableLiveData("")
    private val exercisesIDs = MutableLiveData("")
    val buttonText = MutableLiveData<Int>()
    val navigation = MutableLiveData<Destination>()
    val description = MutableLiveData("")
    private val planId: String = LocalDateTime.now().dayOfWeek.value.toString()

    private val userId = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()
    private val database: DatabaseReference = FirebaseDatabase.getInstance().reference

    fun init() {
        viewModelScope.launch {
            try {
                val plan = repository.getPlanById(userId, planId)
                if (plan != null) {
                    println("Found plan for day $planId")
                    name.postValue(plan.name)
                    exercisesIDs.postValue(plan.exercisesIDs.joinToString(","))
                    buttonText.postValue(R.string.save)
                } else {
                    println("Plan not found for day $planId")
                    name.postValue("")
                    exercisesIDs.postValue("")
                    buttonText.postValue(R.string.add)
                }
                loadExercises()
            } catch (e: NoSuchElementException) {
                println("No plan found for day $planId (NoSuchElementException)")
                name.postValue("")
                exercisesIDs.postValue("")
                buttonText.postValue(R.string.add)
                loadExercises()
            }
        }
    }

    private fun loadExercises() {
        viewModelScope.launch(Dispatchers.IO) {
            val allExercises = exerciseRepository.getExerciseList(userId, database)
            val selectedIds = exercisesIDs.value
                ?.split(",")
                ?.map { it.trim() }
                ?.filter { it.isNotBlank() }
                ?: emptyList()

            println("Selected exercise IDs: $selectedIds")
            val filtered = allExercises.filter { it.id in selectedIds }
            println("Filtered exercises: $filtered")
            exercises.postValue(filtered)
        }
    }

    fun removeExerciseFromPlan(exerciseId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val currentIds = exercisesIDs.value
                ?.split(",")
                ?.map { it.trim() }
                ?.filter { it.isNotBlank() }
                ?.toMutableList()
                ?: mutableListOf()

            if (currentIds.contains(exerciseId)) {
                currentIds.remove(exerciseId)
                exercisesIDs.postValue(currentIds.joinToString(","))

                database.child("users")
                    .child(userId)
                    .child("plans")
                    .child(planId)
                    .child("exercisesIDs")
                    .setValue(currentIds)
                    .addOnSuccessListener {
                        println("Updated plan in Firebase – removed $exerciseId")
                    }
                    .addOnFailureListener {
                        println("Error removing $exerciseId from plan: ${it.message}")
                    }
            }
            loadExercises()
        }
    }

    fun onDestinationChange(
        controller: NavController,
        destination: NavDestination,
        arguments: Bundle?
    ) {
        if (destination.id == R.id.todayPlanFragment) {
            loadExercises()
        }
    }

    fun onSave() {
        navigation.postValue(PopBack())
    }
}
