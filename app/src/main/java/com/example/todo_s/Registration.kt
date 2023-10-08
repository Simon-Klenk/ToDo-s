package com.example.todo_s

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


class Registration : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.registration)

        val login = findViewById<Button>(R.id.registration_login)
        login.setOnClickListener {
            val intent = Intent(this, LogIn::class.java)
            startActivity(intent)
        }

        val registration = findViewById<Button>(R.id.registration_registration)
        registration.setOnClickListener {
            val username = findViewById<TextView>(R.id.editTextText).text.toString()
            val mail = findViewById<TextView>(R.id.editTextTextEmailAddress).text.toString()
            val password = findViewById<TextView>(R.id.editTextTextPassword).text.toString()
            createUser(username, mail, password)

            val intent = Intent(this, LogIn::class.java)
            startActivity(intent)
        }
    }

    // Create a new User and check bevor if the User always exist
    private fun createUser(username: String, mail: String, password: String){
        val database = Firebase.database.reference
        val databaseReference = FirebaseDatabase.getInstance().getReference("users")
        val user = User(username, mail, password)

        //Check whether user already exist
        databaseReference.orderByChild("username").equalTo(username).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    Toast.makeText(this@Registration, "Benutzer ist bereits vorhanden", Toast.LENGTH_LONG).show()
                } else {
                    database.child("users").child(username).setValue(user)
                    Toast.makeText(this@Registration, "Erfolgreich registriert", Toast.LENGTH_LONG).show()
                    val intent = Intent(this@Registration, LogIn::class.java)
                    startActivity(intent)
                    finish()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        })
    }
}