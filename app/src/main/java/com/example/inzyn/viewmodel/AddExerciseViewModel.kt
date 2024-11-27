package com.example.inzyn.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.inzyn.R
import com.example.inzyn.data.ExerciseRepository
import com.example.inzyn.data.ExerciseRepository.Companion.GENERATE_ID
import com.example.inzyn.data.RepositoryLocator
import com.example.inzyn.model.Exercise
import com.example.inzyn.model.navigation.Destination
import com.example.inzyn.model.navigation.PopBack
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class AddExerciseViewModel : ViewModel() {
    private val repository: ExerciseRepository = RepositoryLocator.exerciseRepository
    private var edited: Exercise? = null

    val name = MutableLiveData("")
    val buttonText = MutableLiveData<Int>()
    val navigation = MutableLiveData<Destination>()
    val description = MutableLiveData("")

    fun init(id: Int?) {
        buttonText.value = R.string.add
        if (id != null) {
            viewModelScope.launch {
                try {
                    val exercise = repository.getExerciseById(id)
                    println("found")
                    edited = exercise
                    name.postValue(exercise?.name)
                    description.postValue(exercise?.description)
                    buttonText.postValue(R.string.save)
                } catch (e: NoSuchElementException) {
                    println("not found")
                    edited = null
                    name.postValue("")
                    description.postValue("")
                    buttonText.postValue(R.string.add)
                }
            }
        } else {
            println("no id")
            edited = null
            name.postValue("")
            description.postValue("")
            buttonText.postValue(R.string.add)
        }
    }

    fun onSave() {
        val name = name.value.orEmpty()
        val description = description.value.orEmpty()
        val toSave = edited?.copy(
            name = name,
            description = description
        ) ?: Exercise(
            id = GENERATE_ID,
            name = name,
            description = description
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