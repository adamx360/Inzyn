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
import com.example.inzyn.model.navigation.Destination
import com.example.inzyn.model.navigation.EditSet
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CalendarViewModel : ViewModel() {
    private val setRepository = RepositoryLocator.setRepository
    val sets: MutableLiveData<List<Set>> = MutableLiveData(emptyList())
    val navigation = MutableLiveData<Destination>()
    private val selectedDate: MutableLiveData<String> = MutableLiveData("")
    private val userId = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()
    private val database = FirebaseDatabase.getInstance().reference

    init {
        loadSets()
    }

    private fun loadSets() {
        viewModelScope.launch(Dispatchers.IO) {
            val allSets = setRepository.getSetList(userId, database)
            val dateFilter = selectedDate.value.orEmpty()

            val filtered = if (dateFilter.isNotEmpty()) {
                allSets.filter { it.date == dateFilter }
            } else {
                allSets
            }
            sets.postValue(filtered)
        }
    }

    fun onDateSelected(date: String) {
        selectedDate.value = date
        loadSets()
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
            loadSets()
        }
    }
}