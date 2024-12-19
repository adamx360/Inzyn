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
    private val repository: SetRepository = RepositoryLocator.setRepository
    private val exerciseRepository: ExerciseRepository = RepositoryLocator.exerciseRepository
    private var edited: Set? = null

    val name = MutableLiveData("")
    val buttonText = MutableLiveData<Int>()
    val navigation = MutableLiveData<Destination>()
    val description = MutableLiveData("")
    val weight = MutableLiveData<String>()
    val reps = MutableLiveData<String>()
    val date = MutableLiveData<String>()
    val exerciseName = MutableLiveData<String>()
    var exerciseIDs: String = ""
    val userId = FirebaseAuth.getInstance().currentUser?.uid


    fun init(id: String?, exerciseID: String?) {
        exerciseIDs = exerciseID ?: " "
        buttonText.value = R.string.add
        if (id != null) {
            viewModelScope.launch {
                try {
                    val set = repository.getSetById(userId.toString(), id)
                    exerciseName.postValue(set?.exerciseName)
                    println("Exercise name: $exerciseName")
                    println("found")
                    edited = set
                    name.postValue(set?.exerciseName)
                    description.postValue(set?.description)
                    weight.postValue(set?.weight.toString())
                    reps.postValue(set?.reps.toString())
                    date.postValue(LocalDate.now().toString())
                    buttonText.postValue(R.string.save)

                } catch (e: NoSuchElementException) {
                    println("not found")
                    edited = null
                    exerciseName.postValue("notfound1")
                    name.postValue("notfound")
                    description.postValue("")
                    weight.postValue("0.0")
                    reps.postValue("0")
                    date.postValue(LocalDate.now().toString())
                    buttonText.postValue(R.string.add)
                    exerciseName.postValue("")
                }
            }
        } else {
            viewModelScope.launch {
                println("no id")
                exerciseName.postValue(
                    exerciseRepository.getExerciseById(
                        userId.toString(),
                        exerciseIDs
                    )?.name
                )
                println("Exercise name: $exerciseName")
                description.postValue("")
                weight.postValue("0.0")
                reps.postValue("0")
                date.postValue(LocalDate.now().toString())
                buttonText.postValue(R.string.add)
            }
        }
    }

    fun onSave() {
        val description = description.value.orEmpty()
        val exerciseID = exerciseIDs
        val weight = weight.value.orEmpty().toDouble()
        val reps = reps.value.orEmpty().toInt()
        val date = date.value.orEmpty()
        val exerciseName = exerciseName.value.orEmpty()

        val toSave = edited?.copy(
            description = description,
            exerciseID = exerciseID,
            exerciseName = exerciseName,
            weight = weight,
            reps = reps,
            date = date
        ) ?: Set(
            id = "",
            description = description,
            exerciseID = exerciseID,
            exerciseName = exerciseName,
            weight = weight,
            reps = reps,
            date = date
        )

        viewModelScope.launch {
            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: throw Exception("")
            if (edited == null) {
                repository.add(userId, toSave)
            } else {
                repository.set(userId, toSave)
            }
            withContext(Dispatchers.Main) {
                navigation.value = PopBack()
            }
        }
    }
}