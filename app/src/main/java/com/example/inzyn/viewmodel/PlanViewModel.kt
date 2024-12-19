package com.example.inzyn.viewmodel

import android.os.Bundle
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import com.example.inzyn.R
import com.example.inzyn.data.RepositoryLocator
import com.example.inzyn.model.Plan
import com.example.inzyn.model.navigation.Destination
import com.example.inzyn.model.navigation.EditPlan
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PlanViewModel : ViewModel() {
    private val repository = RepositoryLocator.planRepository
    val plans: MutableLiveData<List<Plan>> = MutableLiveData(emptyList())
    val navigation = MutableLiveData<Destination>()

    init {
        this.loadPlans()
    }

    private fun loadPlans() {
        viewModelScope.launch(Dispatchers.IO) {
            val userId = FirebaseAuth.getInstance().currentUser?.uid
            plans.postValue(repository.getPlanList(userId.toString()))
            repository.getPlanList(userId.toString())
        }
    }

    fun onEditPlan(plan: Plan) {
        navigation.value = EditPlan(plan)
    }


    fun onDestinationChange(
        controller: NavController,
        destination: NavDestination,
        arguments: Bundle?
    ) {
        if (destination.id == R.id.planFragment) {
            this.loadPlans()
        }
    }
}