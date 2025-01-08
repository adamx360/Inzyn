package com.example.inzyn.viewmodel

import android.os.Bundle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import com.example.inzyn.data.ExerciseRepository
import com.example.inzyn.data.RepositoryLocator
import com.example.inzyn.model.Exercise
import com.example.inzyn.model.Plan
import com.example.inzyn.model.navigation.Destination
import com.example.inzyn.model.navigation.PopBack
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AddPlanViewModel : ViewModel() {
    private val exerciseRepository: ExerciseRepository = RepositoryLocator.exerciseRepository
    private val database: DatabaseReference = FirebaseDatabase.getInstance().reference

    private val _plan = MutableLiveData<Plan?>(null)
    val plan: LiveData<Plan?> get() = _plan

    private val _exercises = MutableLiveData<List<Exercise>>(emptyList())
    val exercises: LiveData<List<Exercise>> get() = _exercises

    val name = MutableLiveData("")

    private val _buttonText = MutableLiveData("Dodaj")
    val buttonText: LiveData<String> get() = _buttonText

    val navigation = MutableLiveData<Destination>()

    private var planRef: DatabaseReference? = null
    private var planListener: ValueEventListener? = null

    fun init(planId: String?) {
        if (planId.isNullOrEmpty()) {
            _plan.value = Plan(id = "", name = "", exercisesIDs = emptyList())
            name.value = ""
            _buttonText.value = "Dodaj"
        } else {
            _buttonText.value = "Zapisz"
            observePlan(planId)
        }
    }

    private fun observePlan(planId: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()
        planRef = database.child("users").child(userId).child("plans").child(planId)

        planListener = planRef?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val loadedPlan = snapshot.getValue(Plan::class.java)
                _plan.value = loadedPlan
                name.value = loadedPlan?.name.orEmpty()

                val ids = loadedPlan?.exercisesIDs ?: emptyList()
                loadExercises(ids)
            }

            override fun onCancelled(error: DatabaseError) {
                println("observePlan onCancelled: ${error.message}")
            }
        })
    }

    private fun loadExercises(exercisesIDs: List<String>) {
        viewModelScope.launch(Dispatchers.IO) {
            val userId = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()
            val all = exerciseRepository.getExerciseList(userId, database)
            val filtered = all.filter { it.id in exercisesIDs }
            _exercises.postValue(filtered)
        }
    }

    fun removeExercise(exerciseId: String) {
        val currentPlan = _plan.value ?: return
        val userId = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()
        if (currentPlan.id.isEmpty()) return

        val updatedList = currentPlan.exercisesIDs.toMutableList().apply {
            remove(exerciseId)
        }
        database.child("users")
            .child(userId)
            .child("plans")
            .child(currentPlan.id)
            .child("exercisesIDs")
            .setValue(updatedList)
    }

    fun onSave() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()
        val currentPlan = _plan.value ?: return
        val planName = name.value.orEmpty()

        if (currentPlan.id.isEmpty()) {
            val newKey = database.child("users")
                .child(userId)
                .child("plans")
                .push().key ?: return

            val newPlan = currentPlan.copy(id = newKey, name = planName)
            database.child("users")
                .child(userId)
                .child("plans")
                .child(newKey)
                .setValue(newPlan)
                .addOnCompleteListener {
                    navigation.postValue(PopBack())
                }
        } else {
            val updatedPlan = currentPlan.copy(name = planName)
            database.child("users")
                .child(userId)
                .child("plans")
                .child(updatedPlan.id)
                .setValue(updatedPlan)
                .addOnCompleteListener {
                    navigation.postValue(PopBack())
                }
        }
    }

    fun onDestinationChange(
        controller: NavController,
        destination: NavDestination,
        arguments: Bundle?
    ) {
    }

    override fun onCleared() {
        super.onCleared()
        planListener?.let { planRef?.removeEventListener(it) }
    }
}
