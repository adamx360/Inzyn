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
import com.example.inzyn.model.Plan
import com.example.inzyn.model.navigation.Destination
import com.example.inzyn.model.navigation.PopBack
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddPlanViewModel : ViewModel() {
    private val repository: PlanRepository = RepositoryLocator.planRepository
    private val exerciseRepository: ExerciseRepository = RepositoryLocator.exerciseRepository
    val exercises: MutableLiveData<List<Exercise>> = MutableLiveData(emptyList())
    private var edited: Plan? = null

    val name = MutableLiveData("")
    val exercisesIDs = MutableLiveData<String>()
    val buttonText = MutableLiveData<Int>()
    val navigation = MutableLiveData<Destination>()
    val description = MutableLiveData("")

    fun init(id: String?) {
        buttonText.value = R.string.add
        if (id != null) {
            viewModelScope.launch {
                try {
                    val userId = FirebaseAuth.getInstance().currentUser?.uid
                    val plan = repository.getPlanById(userId.toString(), id)
                    println("found")
                    edited = plan
                    name.postValue(plan?.name)
                    buttonText.postValue(R.string.save)
                    println(plan?.exercisesIDs?.plus(" exercisesIDs found1"))
                    exercisesIDs.postValue(plan?.exercisesIDs.toString())

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
                    edited = null
                    name.postValue("")
                    buttonText.postValue(R.string.add)
                    exercisesIDs.postValue("")
                    loadExercises()
                }
            }
        } else {
            println("no id")
            edited = null
            name.postValue("")
            buttonText.postValue(R.string.add)
            exercisesIDs.postValue("")
            loadExercises()
        }
    }

    fun onSave() {
        val name = name.value.orEmpty()
//        val exercisesIDsList = exercisesIDs.value.orEmpty().split(",").mapNotNull { it.trim().toIntOrNull() }
        val exercisesIDsList = exercisesIDs.value.orEmpty().split(",").map { it.trim() }
        val toSave = edited?.copy(
            name = name,
            exercisesIDs = exercisesIDsList
        ) ?: Plan(
            id = "",
            name = name,
            exercisesIDs = exercisesIDsList
        )

        viewModelScope.launch {
            val userId = FirebaseAuth.getInstance().currentUser?.uid
            if (edited == null) {
                repository.add(userId.toString(), toSave)
            } else {
                repository.set(userId.toString(), toSave)
            }
            withContext(Dispatchers.Main) {
                navigation.value = PopBack()
            }
        }
    }

    private fun loadExercises() {
        viewModelScope.launch(Dispatchers.IO) {
            val userId = FirebaseAuth.getInstance().currentUser?.uid
            val allExercises = exerciseRepository.getExerciseList(userId.toString())

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
        val currentIds = exercisesIDs.value
            ?.split(",")
            ?.map { it.trim() }
            ?.toMutableList()
            ?: mutableListOf()

        if (currentIds.contains(exerciseId)) {
            currentIds.remove(exerciseId)
            exercisesIDs.postValue(currentIds.joinToString(","))
        }
        loadExercises()

    }
}
