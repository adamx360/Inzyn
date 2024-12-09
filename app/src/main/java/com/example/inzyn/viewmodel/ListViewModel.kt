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
import com.example.inzyn.model.db.SetEntity
import com.example.inzyn.model.navigation.AddExercise
import com.example.inzyn.model.navigation.AddSet
import com.example.inzyn.model.navigation.Destination
import com.example.inzyn.model.navigation.EditExercise
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ListViewModel : ViewModel() {
    private val repository = RepositoryLocator.exerciseRepository
    private val setRepository = RepositoryLocator.setRepository
    private val planRepository = RepositoryLocator.planRepository
    val exercises: MutableLiveData<List<Exercise>> = MutableLiveData(emptyList())
    val navigation = MutableLiveData<Destination>()

    init {
        this.loadExercises()
    }

    private fun loadExercises() {
        viewModelScope.launch(Dispatchers.IO) {
            exercises.postValue(repository.getExerciseList())
            setRepository.getSetList()
        }
    }

    fun insertExercise(exercise: Exercise) {
        viewModelScope.launch {
            repository.add(exercise)
            loadExercises()
        }
    }

    fun updateExercise(exercise: Exercise) {
        viewModelScope.launch {
            repository.set(exercise)
            loadExercises()
        }
    }

    fun onAddExercise() {
        navigation.value = AddExercise()
    }

    fun onEditExercise(exercise: Exercise) {
        navigation.value = EditExercise(exercise)
    }

    fun onExerciseRemove(id: Int) {
        viewModelScope.launch {
            repository.removeById(id)
            loadExercises()
        }
    }

    fun onDestinationChange(controller: NavController, destination: NavDestination, arguments: Bundle?) {
        if (destination.id == R.id.listFragment) {
            this.loadExercises()
        }
    }

    fun addExerciseToPlan(exerciseId: Int, planDayId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val plan = planRepository.getPlanById(planDayId)
                val updatedPlan = plan.copy(exercisesIDs = plan.exercisesIDs + exerciseId)
                planRepository.set(updatedPlan)
                println("Exercise $exerciseId added to plan $planDayId")
            } catch (e: NoSuchElementException) {
                println("Plan with ID $planDayId not found")
            }
        }
    }

    suspend fun getSetsForExercise(exerciseId: Int): List<Set> {
        return RepositoryLocator.setRepository.getSetList().filter { it.exerciseID == exerciseId }
    }
}
