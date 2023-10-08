package com.example.todo_s

import ToDosAdapter
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainActivity : ComponentActivity() {
    private var toDos = mutableSetOf<ToDo>()
    private var filterToDos = mutableListOf<ToDo>()
    private var databaseReference: DatabaseReference? = null
    private var adapter: ToDosAdapter? = null
    private var sharedPreferences: SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

        initializeView()
        checkIsLoginIn()
        startLoadData()
    }

    override fun onResume() {
        super.onResume()
        Log.d("Main: onResume", "")
        filterAndSortData()
    }

    // Initialite the View and recyclerView Adapter
    private fun initializeView() {
        setContentView(R.layout.activity_main)

        val recyclerView = findViewById<RecyclerView>(R.id.ToDos)
        adapter = ToDosAdapter(filterToDos)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        val newToDo = findViewById<Button>(R.id.buttonNewToDo)
        newToDo.setOnClickListener {
            val intent = Intent(this, NewToDo::class.java)
            startActivity(intent)
        }

        val logout = findViewById<Button>(R.id.logout)
        logout.setOnClickListener {
            val editor = sharedPreferences?.edit()
            if (editor != null) {
                editor.remove("isLoggedIn")
                editor.remove("username")
                editor.apply()
            }

            val intent = Intent(this, LogIn::class.java)
            startActivity(intent)
            finish()
        }

        val sort = findViewById<Button>(R.id.sort)
        sort.setOnClickListener {
            val intent = Intent(this, SortActivity::class.java)
            startActivity(intent)
        }
    }

    //Check if user is Login in
    private fun checkIsLoginIn() {
        if (!sharedPreferences?.getBoolean("isLoggedIn", false)!!) {
            val intent = Intent(this, LogIn::class.java)
            startActivity(intent)
            finish()
        }
    }

    // Start Load Data from Database
    private fun startLoadData() {

        val username = sharedPreferences?.getString("username", "").toString()
        Log.d("Main: StartLoadData", "Starte laden der Daten von Benutzer: $username")
        databaseReference =
            FirebaseDatabase.getInstance().getReference("users").child(username).child("toDos")

        // If Data changed
        databaseReference!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                Log.d("Main: StartLoadData", "Datenbank hat Änderung gemeldet")
                addDataToSet(dataSnapshot)
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })

    }

    //Add the Data to the Set toDos
    private fun addDataToSet(dataSnapshot: DataSnapshot){
        if (dataSnapshot.exists()) {
            // first remove old data from list
            toDos = mutableSetOf()

            // then add all elements from taskSnapshot to toDos
            for (taskSnapshot in dataSnapshot.children) {
                val description = taskSnapshot.child("description").value.toString()
                val dueDate = taskSnapshot.child("dueDate").value.toString()
                val creationDate = taskSnapshot.child("creationDate").value.toString()
                val state = taskSnapshot.child("state").value as Boolean
                val assigningPerson = taskSnapshot.child("assigningPerson").value.toString()
                val assignedPerson = taskSnapshot.child("assignedPerson").value.toString()

                val toDo = ToDo(
                    description,
                    dueDate,
                    creationDate,
                    state,
                    assignedPerson,
                    assigningPerson
                )
                toDos.add(toDo)
            }

            // Data load finish -> filter and Sort Data
            filterAndSortData()
        } else {
            Toast.makeText(
                this@MainActivity,
                "Daten laden fehlgeschlagen -> Snapshot existiert nicht",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    // Filter and Sort the Data
    @SuppressLint("NotifyDataSetChanged")
    private fun filterAndSortData(){
        var tmp = toDos.toMutableList()
        when (sharedPreferences?.getString("selectedCriteria", "")) {
            "description" -> tmp = toDos.sortedBy { it.description }.toMutableList()
            "dueDate" -> tmp = toDos.sortedBy { it.dueDate }.toMutableList()
            "creationDate" -> tmp = toDos.sortedBy { it.creationDate }.toMutableList()
            "state" -> tmp = toDos.sortedByDescending { it.state }.toMutableList()
            "assignedPerson" -> tmp = toDos.sortedBy { it.assignedPerson }.toMutableList()
            "assigningPerson" -> tmp = toDos.sortedBy { it.assigningPerson }.toMutableList()
            else -> {
            }
        }

        var tmp2: MutableList<ToDo>
        var tmp3 = mutableListOf<ToDo>()

       if (!sharedPreferences?.getBoolean("selectedCriteriaFilter", false)!!) {
            tmp2 = tmp.filter { it.state }.toMutableList()
        }
        else{
            tmp2 = tmp
       }

        for (elements in filterToDos){
            tmp3.add(elements)
        }

        for (elements in tmp3){
            filterToDos.remove(elements)
        }

        for (elements in tmp2){
            filterToDos.add(elements)
        }

        Log.d("Main: showData", "Zeige Daten mit Sortierung: " + sharedPreferences!!.getString("selectedCriteria", "") + " und Filter: " + sharedPreferences!!.getBoolean("selectedCriteriaFilter", false))

        for (elements in filterToDos){
            elements.print()
        }

        //Show the Data in a recyclerView -> Update the adapter for RecyclerView
        val recyclerView = findViewById<RecyclerView>(R.id.ToDos)
        adapter = ToDosAdapter(filterToDos)
        recyclerView.adapter = adapter
        adapter?.notifyDataSetChanged()
        val itemTouchHelper = ItemTouchHelper(Swipe(adapter!!))
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

}