package com.example.todo_s

import ToDosAdapter
import android.util.Log
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class Swipe(private val adapter: ToDosAdapter) : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        TODO("Not yet implemented")
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        Log.d("Swipe", "Geswippt")
        val position = viewHolder.adapterPosition
        val element = adapter.getItemAtPosition(position)


        if (direction == ItemTouchHelper.LEFT){
            val description = element.description
            val dueDate = element.dueDate
            val creationDate = element.creationDate
            val state = !element.state
            val assignedPerson = element.assignedPerson
            val assigningPerson = element.assigningPerson

            val toDo = ToDo(description, dueDate, creationDate, state, assignedPerson, assigningPerson)

            val database = Firebase.database.reference
            database.child("users").child(assignedPerson).child("toDos").child(description).setValue(toDo)
            if (assignedPerson != assigningPerson) {
                database.child("users").child(assigningPerson).child("toDos").child(description)
                    .setValue(toDo)
            }
        }
        else{
            var databaseReference =
                FirebaseDatabase.getInstance().getReference("users").child(element.assigningPerson).child("toDos").child(element.description)
            databaseReference.removeValue()
            if (element.assignedPerson != element.assigningPerson){
                var databaseReference =
                    FirebaseDatabase.getInstance().getReference("users").child(element.assignedPerson).child("toDos").child(element.description)
                databaseReference.removeValue()
            }
        }




    }

}