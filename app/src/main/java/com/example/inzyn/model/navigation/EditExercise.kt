package com.example.inzyn.model.navigation

import androidx.core.os.bundleOf
import androidx.navigation.NavController
import com.example.inzyn.R
import com.example.inzyn.model.AddExerciseType
import com.example.inzyn.model.Exercise

class EditExercise(val exercise: Exercise) : Destination() {
    override fun navigate(controller: NavController) {
        controller.navigate(
            R.id.action_listFragment_to_addExerciseFragment,
            bundleOf("type" to AddExerciseType.Edit(exercise.id))
        )
    }
}