package com.example.inzyn.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.inzyn.R
import com.example.inzyn.data.ExerciseRepository
import com.example.inzyn.data.SetRepository.Companion.GENERATE_ID
import com.example.inzyn.data.RepositoryLocator
import com.example.inzyn.data.SetRepository
import com.example.inzyn.model.Set
import com.example.inzyn.model.navigation.Destination
import com.example.inzyn.model.navigation.PopBack
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Calendar
import java.util.Date
import java.util.Locale

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
    var exerciseIDs = 0

    fun init(id: Int?, exerciseID: Int?) {
        exerciseIDs = exerciseID?:0
        buttonText.value = R.string.add
        if (id != null) {
            viewModelScope.launch {
                try {
                    val set = repository.getSetById(id)
                    exerciseName.postValue(exerciseRepository.getExerciseById(exerciseIDs).name)
                    println("found")
                    edited = set
                    name.postValue(exerciseName.value)
                    description.postValue(set.description)
                    weight.postValue(set.weight.toString())
                    reps.postValue(set.reps.toString())
                    date.postValue(LocalDate.now().toString())
                    buttonText.postValue(R.string.save)
                } catch (e: NoSuchElementException) {
                    println("not found")
                    edited = null
                    name.postValue("notfound")
                    description.postValue("")
                    weight.postValue("0.0")
                    reps.postValue("0")
                    date.postValue(LocalDate.now().toString())
                    buttonText.postValue(R.string.add)
                }
            }
        } else {
            viewModelScope.launch {
                println("no id")
                edited = null
                exerciseName.postValue(exerciseRepository.getExerciseById(exerciseIDs).name)
                name.postValue(exerciseName.value)
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

        val toSave = edited?.copy(
            description = description,
            exerciseID = exerciseID,
            weight = weight,
            reps = reps,
            date = date
        ) ?: Set(
            id = GENERATE_ID,
            description = description,
            exerciseID = exerciseID,
            weight = weight,
            reps = reps,
            date = date
        )

        viewModelScope.launch {
            if (edited == null) {
                repository.add(toSave)
            } else {
                repository.set(toSave)
            }
            withContext(Dispatchers.Main) {
                navigation.value = PopBack()
            }
        }
    }

    private fun parseDate(dateString: String): Calendar {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = Calendar.getInstance()
        date.time = dateFormat.parse(dateString) ?: Date()
        return date
    }
}