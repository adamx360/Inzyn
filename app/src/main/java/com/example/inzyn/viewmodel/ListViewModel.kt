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
    private val repository = RepositoryLocator.exerciseRepository
    private val setRepository = RepositoryLocator.setRepository
    private val planRepository = RepositoryLocator.planRepository
    val exercises: MutableLiveData<List<Exercise>> = MutableLiveData(emptyList())
    val navigation = MutableLiveData<Destination>()
    val userId = FirebaseAuth.getInstance().currentUser?.uid
    val database = FirebaseDatabase.getInstance().reference

    init {
        this.loadExercises()
    }

    private fun loadExercises() {
        viewModelScope.launch(Dispatchers.IO) {
            exercises.postValue(repository.getExerciseList(userId.toString(),database ))
            setRepository.getSetList(userId.toString(),database)
        }
    }

    fun onAddExercise() {
        navigation.value = AddExercise()
    }

    fun onEditExercise(exercise: Exercise) {
        navigation.value = EditExercise(exercise)
    }

    fun onExerciseRemove(id: String) {
        viewModelScope.launch {
            val userId = FirebaseAuth.getInstance().currentUser?.uid
            repository.removeById(userId.toString(), id)
            loadExercises()
        }
    }

    fun onDestinationChange(
        controller: NavController,
        destination: NavDestination,
        arguments: Bundle?
    ) {
        if (destination.id == R.id.listFragment) {
            this.loadExercises()
        }
    }

    fun addExerciseToPlan(exerciseId: String, planDayId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val userId = FirebaseAuth.getInstance().currentUser?.uid
            val plan = planRepository.getPlanById(userId.toString(), planDayId)
            val updatedPlan = plan?.copy(exercisesIDs = plan.exercisesIDs + exerciseId)
            if (updatedPlan != null) {
                planRepository.set(userId.toString(), updatedPlan)
            }
            println("Exercise $exerciseId added to plan $planDayId")


        }

    }


    suspend fun getSetsForExercise(exerciseId: String): List<Set> {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        return RepositoryLocator.setRepository.getSetList(userId.toString(),database)
            .filter { it.exerciseID == exerciseId }
    }
}

