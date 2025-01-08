package com.example.inzyn.viewmodel

import android.os.Bundle
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import com.example.inzyn.R
import com.example.inzyn.data.RepositoryLocator
import com.example.inzyn.model.Exercise
import com.example.inzyn.model.Set
import com.example.inzyn.model.navigation.AddExercise
import com.example.inzyn.model.navigation.Destination
import com.example.inzyn.model.navigation.EditExercise
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ListViewModel : ViewModel() {
    private val exerciseRepository = RepositoryLocator.exerciseRepository
    private val setRepository = RepositoryLocator.setRepository
    private val planRepository = RepositoryLocator.planRepository
    val exercises: MutableLiveData<List<Exercise>> = MutableLiveData(emptyList())
    val navigation = MutableLiveData<Destination>()
    private val userId = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()
    private val database = FirebaseDatabase.getInstance().reference

    init {
        loadExercises()
    }

    private fun loadExercises() {
        viewModelScope.launch(Dispatchers.IO) {
            val exList = exerciseRepository.getExerciseList(userId, database)
            exercises.postValue(exList)
        }
    }

    fun onAddExercise() {
        navigation.value = AddExercise()
    }

    fun onEditExercise(exercise: Exercise) {
        navigation.value = EditExercise(exercise)
    }

    fun onExerciseRemove(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            exerciseRepository.removeById(userId, id)
            loadExercises()
        }
    }

    fun addExerciseToPlan(exerciseId: String, planDayId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val plan = planRepository.getPlanById(userId, planDayId)
            val updatedPlan = plan?.copy(exercisesIDs = plan.exercisesIDs + exerciseId)
            if (updatedPlan != null) {
                planRepository.set(userId, updatedPlan)
            }
            println("Exercise $exerciseId added to plan $planDayId")
        }
    }

    suspend fun getSetsForExercise(exerciseId: String): List<Set> {
        return setRepository.getSetList(userId, database)
            .filter { it.exerciseID == exerciseId }
    }

    fun onDestinationChange(
        controller: NavController,
        destination: NavDestination,
        arguments: Bundle?
    ) {
        if (destination.id == R.id.listFragment) {
            loadExercises()
        }
    }
}