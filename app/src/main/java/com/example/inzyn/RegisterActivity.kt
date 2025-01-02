package com.example.inzyn

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.inzyn.data.db.GymDb
import com.example.inzyn.databinding.ActivityRegisterBinding
import com.example.inzyn.model.User
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private val gymDb = GymDb()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth
        database = FirebaseDatabase.getInstance().reference

        binding.registerButton.setOnClickListener {
            signUpUser()

        }
    }

    private fun signUpUser() {
        val email = binding.email.text.toString()
        val password = binding.password.text.toString()


        if (email.isBlank() || password.isBlank()) {
            Toast.makeText(this, "Email or password is blank", Toast.LENGTH_SHORT).show()
        }
        if (password.length < 6) {
            Toast.makeText(this, "Password has to be at least 6 characters", Toast.LENGTH_SHORT)
                .show()
        }

        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this) {
            if (it.isSuccessful) {
                Toast.makeText(this, "Register completed", Toast.LENGTH_SHORT).show()

                fun writeNewUser(userId: String, email: String) {
                    val user = User(userId, email)

                    database.child("users").child(userId).setValue(user)

                }
                val uid = auth.currentUser?.uid


                writeNewUser(uid.toString(), email)



                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }

    }


}
