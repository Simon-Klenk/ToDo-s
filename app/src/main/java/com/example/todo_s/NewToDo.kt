package com.example.todo_s

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

// Creats a new To Do on an spcified User
class NewToDo : ComponentActivity() {
    private lateinit var dueDateChoose: EditText
    private val dateFormatter = SimpleDateFormat("dd.MM.yyyy", Locale.GERMANY)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.new_to_do)

        val create = findViewById<Button>(R.id.newToDo_create)
        create.setOnClickListener{
            // if Button generate To Do clicked
            checkUsername()
        }

        val cancel = findViewById<Button>(R.id.newToDo_cancel)
        cancel.setOnClickListener{
            val intent = Intent(this@NewToDo, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        val dueDateChoose = findViewById<EditText>(R.id.newToDo_dueDate)
        dueDateChoose.setOnClickListener{
            showDateDialog()
        }
    }

    private fun showDateDialog(){
        val dueDateChoose = findViewById<EditText>(R.id.newToDo_dueDate)
        val calendar = Calendar.getInstance(Locale.GERMANY)
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { view, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = Calendar.getInstance(Locale.GERMANY)
                selectedDate.set(selectedYear, selectedMonth, selectedDay)
                val formattedDate = dateFormatter.format(selectedDate.time)
                dueDateChoose.setText(formattedDate)
            },
            year,
            month,
            day
        )

        datePickerDialog.show()

    }

    private fun checkUsername(){
        // check if Username empty or fill
        var username = findViewById<EditText>(R.id.newToDo_person).text.toString()
        // If Username has no caps
        if (username == ""){
            val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
            username = sharedPreferences.getString("username", "").toString()
            createNewToDo(username, username)
        }
        // If Username has Caps, check if it exist in Database
        else {
            Firebase.database.reference.child("users").child(username).get().addOnSuccessListener {
                if (it.exists()) {
                    val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
                    val assigningPerson = sharedPreferences.getString("username", "").toString()
                    createNewToDo(assigningPerson, username)
                } else {
                    Toast.makeText(this@NewToDo, "Username existiert nicht", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }

    @SuppressLint("SimpleDateFormat")
    private fun createNewToDo(assignedPerson: String, assigningPerson: String){
        // Create new ToDo
        val dateFormat = SimpleDateFormat("dd.MM.yyyy")

        val description = findViewById<EditText>(R.id.newToDo_description).text.toString()
        val dueDate = findViewById<EditText>(R.id.newToDo_dueDate).text.toString()
        val creationDate = dateFormat.format(Date())

        val toDo = ToDo(description, dueDate, creationDate, false, assignedPerson, assigningPerson)
        Log.d("User", toDo.assignedPerson + " + " + toDo.assigningPerson + " + " + toDo.description + " + " + toDo.creationDate + " + " + toDo.dueDate + " + " + toDo.state)

        val database = Firebase.database.reference
        database.child("users").child(assignedPerson).child("toDos").child(description).setValue(toDo)
        if (assignedPerson != assigningPerson) {
            database.child("users").child(assigningPerson).child("toDos").child(description)
                .setValue(toDo)
        }

        Toast.makeText(this@NewToDo, "Erfolgreich erstellt", Toast.LENGTH_LONG).show()
        val intent = Intent(this@NewToDo, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}