package com.example.inzyn.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.inzyn.R
import com.example.inzyn.data.ExerciseRepository
import com.example.inzyn.data.RepositoryLocator
import com.example.inzyn.data.SetRepository
import com.example.inzyn.model.Set
import com.example.inzyn.model.navigation.Destination
import com.example.inzyn.model.navigation.PopBack
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate

class AddSetViewModel : ViewModel() {
    private val setRepository: SetRepository = RepositoryLocator.setRepository
    private val exerciseRepository: ExerciseRepository = RepositoryLocator.exerciseRepository
    private var edited: Set? = null

    val name = MutableLiveData("")
    val buttonText = MutableLiveData<Int>()
    val navigation = MutableLiveData<Destination>()
    val description = MutableLiveData("")
    val weight = MutableLiveData<String>()
    val reps = MutableLiveData<String>()
    private val date = MutableLiveData<String>()
    val exerciseName = MutableLiveData<String>()
    private var exerciseId: String = ""

    fun init(setId: String?, exerciseID: String?) {
        exerciseId = exerciseID.orEmpty()
        buttonText.value = R.string.add

        if (setId != null) {
            viewModelScope.launch {
                val userId = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()
                try {
                    val setItem = setRepository.getSetById(userId, setId)
                    println("found set: $setItem")

                    edited = setItem
                    exerciseName.postValue(setItem?.exerciseName.orEmpty())
                    name.postValue(setItem?.exerciseName.orEmpty())
                    description.postValue(setItem?.description.orEmpty())
                    weight.postValue(setItem?.weight?.toString().orEmpty())
                    reps.postValue(setItem?.reps?.toString().orEmpty())
                    date.postValue(setItem?.date ?: LocalDate.now().toString())
                    buttonText.postValue(R.string.save)

                } catch (e: NoSuchElementException) {
                    println("not found")
                    resetFieldsForNewSet()
                }
            }
        } else {
            viewModelScope.launch {
                val userId = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()
                val exName = exerciseRepository.getExerciseById(userId, exerciseId)?.name.orEmpty()
                exerciseName.postValue(exName)
                println("Exercise name: $exName")

                resetFieldsForNewSet()
            }
        }
    }

    private fun resetFieldsForNewSet() {
        edited = null
        name.postValue("")
        description.postValue("")
        weight.postValue("0.0")
        reps.postValue("0")
        date.postValue(LocalDate.now().toString())
        buttonText.postValue(R.string.add)
    }

    fun onSave() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()
        val newDescription = description.value.orEmpty()
        val newExerciseId = exerciseId
        val newWeight = weight.value.orEmpty().toDoubleOrNull() ?: 0.0
        val newReps = reps.value.orEmpty().toIntOrNull() ?: 0
        val newDate = date.value.orEmpty()
        val newExerciseName = exerciseName.value.orEmpty()

        val toSave = edited?.copy(
            description = newDescription,
            exerciseID = newExerciseId,
            exerciseName = newExerciseName,
            weight = newWeight,
            reps = newReps,
            date = newDate
        ) ?: Set(
            id = "",
            description = newDescription,
            exerciseID = newExerciseId,
            exerciseName = newExerciseName,
            weight = newWeight,
            reps = newReps,
            date = newDate
        )

        viewModelScope.launch {
            if (edited == null) {
                setRepository.add(userId, toSave)
            } else {
                setRepository.set(userId, toSave)
            }
            withContext(Dispatchers.Main) {
                navigation.value = PopBack()
            }
        }
    }
}