package com.example.inzyn.data.db

import com.example.inzyn.model.Exercise
import com.example.inzyn.model.Plan
import com.example.inzyn.model.Set
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class GymDb {
    private val database: DatabaseReference = FirebaseDatabase.getInstance().reference
    private val userId: String? = FirebaseAuth.getInstance().currentUser?.uid

    private val sampleExercises = listOf(
        Exercise(id = "1", name = "BenchPress", description = "Description for BenchPress"),
        Exercise(id = "2", name = "Squat", description = "Description for Squat"),
        Exercise(id = "3", name = "Deadlift", description = "Description for Deadlift"),
        Exercise(id = "4", name = "PullUps", description = "Description for PullUps"),
        Exercise(id = "5", name = "Overhead Press", description = "Description for Overhead Press"),
        Exercise(id = "6", name = "Barbell Row", description = "Description for Barbell Row"),
        Exercise(id = "7", name = "Bicep Curls", description = "Description for Bicep Curls"),
        Exercise(
            id = "8",
            name = "Tricep Extensions",
            description = "Description for Tricep Extensions"
        ),
        Exercise(id = "9", name = "Lunges", description = "Description for Lunges"),
        Exercise(id = "10", name = "Plank", description = "Description for Plank"),
        Exercise(id = "11", name = "Dips", description = "Description for Dips")
    )

    private val samplePlans = listOf(
        Plan(
            id = "1",
            name = "Chest Day",
            exercisesIDs = listOf("1", "5", "11")  // BenchPress, Overhead Press, Dips
        ),
        Plan(
            id = "2",
            name = "Leg Day",
            exercisesIDs = listOf("2", "9")        // Squat, Lunges
        ),
        Plan(
            id = "3",
            name = "Back Day",
            exercisesIDs = listOf("3", "4", "6")   // Deadlift, PullUps, Barbell Row
        ),
        Plan(
            id = "4",
            name = "Core Day",
            exercisesIDs = listOf("10")            // Plank
        ),
        Plan(
            id = "5",
            name = "Arms Day",
            exercisesIDs = listOf("7", "8")        // Bicep Curls, Tricep Extensions
        ),
        Plan(id = "6", name = "Rest Day", exercisesIDs = emptyList()),
        Plan(id = "7", name = "Rest Day", exercisesIDs = emptyList())
    )

    private val sampleSets = listOf(
        // BenchPress
        Set(
            id = "1",
            exerciseID = "1",
            reps = 10,
            weight = 60.0,
            date = "2024-11-21",
            description = "Set 1",
            exerciseName = "BenchPress"
        ),
        Set(
            id = "2",
            exerciseID = "1",
            reps = 8,
            weight = 65.0,
            date = "2024-11-21",
            description = "Set 2",
            exerciseName = "BenchPress"
        ),
        // Squat
        Set(
            id = "3",
            exerciseID = "2",
            reps = 12,
            weight = 80.0,
            date = "2024-11-22",
            description = "Set 1",
            exerciseName = "Squat"
        ),
        Set(
            id = "4",
            exerciseID = "2",
            reps = 10,
            weight = 85.0,
            date = "2024-11-22",
            description = "Set 2",
            exerciseName = "Squat"
        ),
        // Deadlift
        Set(
            id = "5",
            exerciseID = "3",
            reps = 8,
            weight = 100.0,
            date = "2024-11-22",
            description = "Set 1",
            exerciseName = "Deadlift"
        ),
        Set(
            id = "6",
            exerciseID = "3",
            reps = 6,
            weight = 110.0,
            date = "2024-11-22",
            description = "Set 2",
            exerciseName = "Deadlift"
        ),
        // PullUps
        Set(
            id = "7",
            exerciseID = "4",
            reps = 12,
            weight = 0.0,
            date = "2024-11-23",
            description = "Bodyweight",
            exerciseName = "PullUps"
        ),
        Set(
            id = "8",
            exerciseID = "4",
            reps = 10,
            weight = 5.0,
            date = "2024-11-23",
            description = "Weighted",
            exerciseName = "PullUps"
        ),
        // Overhead Press
        Set(
            id = "9",
            exerciseID = "5",
            reps = 8,
            weight = 40.0,
            date = "2024-11-23",
            description = "Set 1",
            exerciseName = "Overhead Press"
        ),
        // Bicep Curls
        Set(
            id = "10",
            exerciseID = "7",
            reps = 10,
            weight = 15.0,
            date = "2024-11-24",
            description = "Set 1",
            exerciseName = "Bicep Curls"
        ),
        // Tricep Extensions
        Set(
            id = "11",
            exerciseID = "8",
            reps = 10,
            weight = 20.0,
            date = "2024-11-25",
            description = "Set 1",
            exerciseName = "Tricep Extensions"
        )
    )

    fun writeExercises() {
        if (userId == null) return
        sampleExercises.forEach { exercise ->
            val exerciseId = exercise.id
            database.child("users")
                .child(userId)
                .child("exercises")
                .child(exerciseId)
                .setValue(exercise)
        }
    }

    fun writePlans() {
        if (userId == null) return
        samplePlans.forEach { plan ->
            val planId = plan.id
            database.child("users")
                .child(userId)
                .child("plans")
                .child(planId)
                .setValue(plan)
        }
    }

}
