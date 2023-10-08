package com.example.todo_s

import android.content.Context
import android.os.Bundle
import android.widget.Button
import androidx.activity.ComponentActivity
import android.widget.RadioGroup
import android.widget.Switch

class SortActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.sort)

        val sharedPreferences =
            getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val sortButton = findViewById<Button>(R.id.btnSort)
        val sortRadioGroup = findViewById<RadioGroup>(R.id.sortRadioGroup)

        findViewById<Switch>(R.id.switch1).isChecked = sharedPreferences.getBoolean("selectedCriteriaFilter", false)

        sortButton.setOnClickListener{
            val selectedRadioButtonId = sortRadioGroup.checkedRadioButtonId
            var selectedCriteria = ""

            when (selectedRadioButtonId) {
                R.id.radioDescription -> selectedCriteria = "description"
                R.id.radioDueDate -> selectedCriteria = "dueDate"
                R.id.radioCreationDate -> selectedCriteria = "creationDate"
                R.id.radioState -> selectedCriteria = "state"
                R.id.radioAssignedPerson -> selectedCriteria = "assignedPerson"
                R.id.radioAssigningPerson -> selectedCriteria = "assigningPerson"
            }

            val editor = sharedPreferences.edit()
            editor.putBoolean("selectedCriteriaFilter", findViewById<Switch>(R.id.switch1).isChecked)
            editor.putString("selectedCriteria", selectedCriteria)
            editor.apply()
            finish()
        }
    }
}
