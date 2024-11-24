package com.example.inzyn.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.inzyn.R
import com.example.inzyn.data.PlanRepository
import com.example.inzyn.data.PlanRepository.Companion.GENERATE_ID
import com.example.inzyn.data.RepositoryLocator
import com.example.inzyn.model.Plan
import com.example.inzyn.model.navigation.Destination
import com.example.inzyn.model.navigation.PopBack
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddPlanViewModel : ViewModel(){
    private val repository: PlanRepository = RepositoryLocator.planRepository
    private var edited: Plan? = null

    val name = MutableLiveData("")
    val exercisesIDs = MutableLiveData("")
    val buttonText = MutableLiveData<Int>()
    val navigation = MutableLiveData<Destination>()
    val description = MutableLiveData("")

    fun init(id: Int?) {
        buttonText.value = R.string.add
        if (id != null) {
            viewModelScope.launch {
                try {
                    val plan = repository.getPlanById(id)
                    println("found")
                    edited = plan
                    name.postValue(plan.name)
                    buttonText.postValue(R.string.save)
                } catch (e: NoSuchElementException) {
                    println("not found")
                    edited = null
                    name.postValue("")
                    buttonText.postValue(R.string.add)
                }
            }
        } else {
            println("no id")
            edited = null
            name.postValue("")
            buttonText.postValue(R.string.add)
        }
    }

    fun onSave() {
        val name = name.value.orEmpty()
        val exercisesIDs = exercisesIDs.value.orEmpty()
        val toSave = edited?.copy(
            name = name,
            exercisesIDs = listOf()
        ) ?: Plan(
            id = GENERATE_ID,
            name = name,
            exercisesIDs = listOf()
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
}