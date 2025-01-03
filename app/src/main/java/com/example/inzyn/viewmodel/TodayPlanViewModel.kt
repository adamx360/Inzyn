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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.lang.reflect.InvocationTargetException
import java.time.LocalDateTime

class TodayPlanViewModel : ViewModel() {
    private val repository: PlanRepository = RepositoryLocator.planRepository
    private val exerciseRepository: ExerciseRepository = RepositoryLocator.exerciseRepository
    val exercises: MutableLiveData<List<Exercise>> = MutableLiveData(emptyList())

    val name = MutableLiveData("")
    val exercisesIDs = MutableLiveData("")
    val buttonText = MutableLiveData<Int>()
    val navigation = MutableLiveData<Destination>()
    val description = MutableLiveData("")
    val id: Int = findTodaysID()
    val userId = FirebaseAuth.getInstance().currentUser?.uid
    val database = FirebaseDatabase.getInstance().reference


    private fun findTodaysID(): Int {

        return LocalDateTime.now().dayOfWeek.value

    }



    fun init() {
        viewModelScope.launch {
            try {
                val plan = repository.getPlanById(userId.toString(), id.toString())
                println("found")
                name.postValue(plan?.name)
                buttonText.postValue(R.string.save)
                println(plan?.exercisesIDs?.plus(" exercisesIDs found1"))
                exercisesIDs.postValue(plan?.exercisesIDs?.joinToString(","))

                // Dodaj obserwatora dla exercisesIDs
                exercisesIDs.observeForever { ids ->
                    if (!ids.isNullOrEmpty()) {
                        println("exercisesIDs updated: $ids")
                        loadExercises()
                        exercisesIDs.removeObserver { } // Usuń obserwatora po pierwszym wywołaniu
                    }
                }
            } catch (e: NoSuchElementException) {
                println("not found")
                name.postValue("")
                buttonText.postValue(R.string.add)
                exercisesIDs.postValue("")
                loadExercises()
            }
        }
    }

    private fun loadExercises() {
        viewModelScope.launch(Dispatchers.IO) {
            val userId = FirebaseAuth.getInstance().currentUser?.uid
            val allExercises = exerciseRepository.getExerciseList(userId.toString(),database)

            // Debug: Sprawdź exercisesIDs przed filtrowaniem
            println("exercisesIDs before filtering: ${exercisesIDs.value}")

            // Pobierz listę exercisesIDs i przekształć ją w listę Int
            val selectedIds = exercisesIDs.value
                ?.split(",")?.joinToString(",") { it.trim() }
                ?: " "

            // Debug: Sprawdź przekształcone selectedIds
            println("Selected IDs for filtering: $selectedIds")

            // Filtruj ćwiczenia na podstawie exercisesIDs
            val filteredExercises = allExercises.filter { it.id in selectedIds }

            // Debug: Sprawdź przefiltrowane ćwiczenia
            println("Filtered exercises: $filteredExercises")

            // Zaktualizuj wartość `exercises`
            exercises.postValue(filteredExercises)
        }
    }

    fun onDestinationChange(
        controller: NavController,
        destination: NavDestination,
        arguments: Bundle?
    ) {
        if (destination.id == R.id.addPlanFragment) {
            this.loadExercises()
        }
    }

    override fun onCleared() {
        super.onCleared()
        exercisesIDs.removeObserver { loadExercises() }
    }

    fun removeExerciseFromPlan(exerciseId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val currentIds = exercisesIDs.value
                ?.split(",")
                ?.map { it.trim() }
                ?.toMutableList()
                ?: mutableListOf()

            if (currentIds.contains(exerciseId)) {
                currentIds.remove(exerciseId)
                exercisesIDs.postValue(currentIds.joinToString(","))
            }
            println("Exercise deleted $exerciseId ")

            loadExercises()

        }
    }
}
