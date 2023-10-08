package com.example.todo_s

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
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

class LogIn : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.log_in)

        val login = findViewById<Button>(R.id.login_login)
        login.setOnClickListener {
            val username = findViewById<TextView>(R.id.editTextTextEmailAddress2).text.toString()
            val password = findViewById<TextView>(R.id.editTextTextPassword2).text.toString()
            loginUser(username, password)
        }

        val registration = findViewById<Button>(R.id.login_registration)
        registration.setOnClickListener {
            val intent = Intent(this, Registration::class.java)
            startActivity(intent)
            finish()
        }
    }

    // Check if User exists and the right password
    private fun loginUser(username: String, password: String) {

        Firebase.database.reference.child("users").child(username).get().addOnSuccessListener {
            Log.d("Login", "exist?")
            if (it.exists()) {

                if (it.child("password").value == password) {
                    val sharedPreferences =
                        getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
                    val editor = sharedPreferences.edit()
                    editor.putBoolean("isLoggedIn", true)
                    editor.putString("username", username)
                    editor.apply()
                    Log.d("Login", username)
                    Toast.makeText(
                        this@LogIn,
                        "Erfolgreich eingeloggt", Toast.LENGTH_LONG).show()
                    val intent = Intent(this@LogIn, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                else {
                    Toast.makeText(this@LogIn, "Falsche E-Mail oder Passwort", Toast.LENGTH_LONG).show()
                }

            }
            else {
                Toast.makeText(this@LogIn, "Falsche E-Mail oder Passwort", Toast.LENGTH_LONG).show()
            }
        }
    }
}