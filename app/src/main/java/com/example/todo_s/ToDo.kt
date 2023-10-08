package com.example.todo_s

import android.util.Log

class ToDo(val description: String, val dueDate: String, val creationDate: String, var state: Boolean, val assignedPerson: String, val assigningPerson: String) {

    fun print (){
        Log.d("ToDo", "$description $dueDate $creationDate $state $assignedPerson $assigningPerson")
    }
}