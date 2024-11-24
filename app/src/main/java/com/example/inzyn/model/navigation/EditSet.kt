package com.example.inzyn.model.navigation

import androidx.core.os.bundleOf
import androidx.navigation.NavController
import com.example.inzyn.R
import com.example.inzyn.model.AddSetType
import com.example.inzyn.model.Set

class EditSet(val set: Set) : Destination() {
    override fun navigate(controller: NavController) {
        controller.navigate(
            R.id.action_listFragment_to_addSetFragment,
            bundleOf("type" to AddSetType.Edit(set.id))
        )

    }
}