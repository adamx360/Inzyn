package com.example.inzyn.model.navigation

import androidx.core.os.bundleOf
import androidx.navigation.NavController
import com.example.inzyn.R
import com.example.inzyn.model.AddPlanType

class AddPlan : Destination() {
    override fun navigate(controller: NavController) {
        controller.navigate(
            R.id.action_planFragment_to_addPlanFragment,
            bundleOf("type" to AddPlanType.New)
        )
    }
}