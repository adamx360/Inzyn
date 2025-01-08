package com.example.inzyn

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.inzyn.databinding.ActivityRegisterBinding
import com.example.inzyn.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        binding.registerButton.setOnClickListener {
            signUpUser()
        }
    }

    private fun signUpUser() {
        val email = binding.email.text.toString().trim()
        val password = binding.password.text.toString().trim()

        if (email.isBlank() || password.isBlank()) {
            Toast.makeText(this, "Email or password is blank", Toast.LENGTH_SHORT).show()
            return
        }
        if (password.length < 6) {
            Toast.makeText(this, "Password must have at least 6 characters", Toast.LENGTH_SHORT)
                .show()
            return
        }

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Register completed", Toast.LENGTH_SHORT).show()

                    val uid = auth.currentUser?.uid.orEmpty()
                    writeNewUser(uid, email)

                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                } else {
                }
            }
    }

    private fun writeNewUser(userId: String, email: String) {
        val user = User(userId, email)
        database.child("users").child(userId).setValue(user)
    }
}