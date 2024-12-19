package com.example.inzyn.viewmodel

import android.os.Bundle
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import com.example.inzyn.R
import com.example.inzyn.data.RepositoryLocator
import com.example.inzyn.model.Set
import com.example.inzyn.model.navigation.AddSet
import com.example.inzyn.model.navigation.Destination
import com.example.inzyn.model.navigation.EditSet
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CalendarViewModel : ViewModel() {
    private val setRepository = RepositoryLocator.setRepository
    val sets: MutableLiveData<List<Set>> = MutableLiveData(emptyList())
    val navigation = MutableLiveData<Destination>()
    val selectedDate: MutableLiveData<String> = MutableLiveData("")
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: throw Exception("")

    init {
        this.loadSets()
    }

    private fun loadSets() {
        viewModelScope.launch(Dispatchers.IO) {
            val allSets = setRepository.getSetList(userId)
            val filteredSets = selectedDate.value?.let { date ->
                if (date.isNotEmpty()) {
                    allSets.filter { it.date == date }
                } else {
                    allSets
                }
            } ?: allSets
            sets.postValue(filteredSets)
        }
    }

    fun onDateSelected(date: String) {
        selectedDate.value = date
        loadSets()
    }

//    fun insertSet(set: Set) {
//        viewModelScope.launch {
//            setRepository.add(set)
//            loadSets()
//        }
//    }
//
//    fun updateSet(set: Set) {
//        viewModelScope.launch {
//            setRepository.set(set)
//            loadSets()
//        }
//    }

    fun onAddSet() {
        navigation.value = AddSet()
    }

    fun onEditSet(set: Set) {
        navigation.value = EditSet(set)
    }

    fun onSetRemove(id: String) {
        viewModelScope.launch {
            setRepository.removeById(userId, id)
            loadSets()
        }
    }

    fun onDestinationChange(
        controller: NavController,
        destination: NavDestination,
        arguments: Bundle?
    ) {
        if (destination.id == R.id.calendarFragment) {
            this.loadSets()
        }
    }
}