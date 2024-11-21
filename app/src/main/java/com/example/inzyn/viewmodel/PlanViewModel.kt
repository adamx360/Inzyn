package com.example.inzyn.viewmodel

import android.os.Bundle
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import com.example.inzyn.R
import com.example.inzyn.data.RepositoryLocator
import com.example.inzyn.model.Gym
import com.example.inzyn.model.navigation.Destination
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PlanViewModel : ViewModel() {
    private val repository = RepositoryLocator.gymRepository
    val gyms: MutableLiveData<List<Gym>> = MutableLiveData(emptyList())
    val navigation = MutableLiveData<Destination>()

    init {
        this.loadGyms()
    }

    fun loadGyms() {
        viewModelScope.launch(Dispatchers.IO) {
            gyms.postValue(repository.getGymList())
        }
    }

    fun updateGym(gym: Gym) {
        // Implementacja aktualizacji Gym
    }

    fun onGymRemove(id: Int) {
        viewModelScope.launch {
            repository.removeById(id)
            loadGyms()
        }
    }

    fun onDestinationChange(controller: NavController, destination: NavDestination, arguments: Bundle?) {
        if (destination.id == R.id.listFragment) {
            this.loadGyms()
        }
    }
}
