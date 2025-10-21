package com.anastasia.laba2

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.*

class UpdateActivity : AppCompatActivity() {
    private lateinit var editDate: EditText
    private lateinit var editTime: EditText
    private lateinit var editInfo: EditText
    private lateinit var btnUpdate: Button
    private lateinit var btnCancel: Button
    private lateinit var xmlManager: XmlManager

    private var eventId: String = ""
    private var selectedDate: String = ""
    private var selectedTime: String = ""
    private val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
    private val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update)

        xmlManager = XmlManager(this)
        initViews()
        loadEventData()
        setupListeners()
    }

    private fun initViews() {
        editDate = findViewById(R.id.editDate)
        editTime = findViewById(R.id.editTime)
        editInfo = findViewById(R.id.editInfo)
        btnUpdate = findViewById(R.id.btnUpdate)
        btnCancel = findViewById(R.id.btnCancel)
    }

    private fun loadEventData() {
        eventId = intent.getStringExtra("event_id") ?: ""
        selectedDate = intent.getStringExtra("event_date") ?: ""
        selectedTime = intent.getStringExtra("event_time") ?: ""
        val info = intent.getStringExtra("event_info") ?: ""

        editDate.setText(selectedDate)
        editTime.setText(selectedTime)
        editInfo.setText(info)
    }

    private fun setupListeners() {
        editDate.setOnClickListener {
            showDatePicker()
        }

        editTime.setOnClickListener {
            showTimePicker()
        }

        btnUpdate.setOnClickListener {
            updateEvent()
        }

        btnCancel.setOnClickListener {
            finish()
        }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        try {
            calendar.time = dateFormat.parse(selectedDate) ?: Date()
        } catch (e: Exception) {
            calendar.time = Date()
        }

        DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)
                selectedDate = dateFormat.format(calendar.time)
                editDate.setText(selectedDate)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun showTimePicker() {
        val calendar = Calendar.getInstance()
        try {
            calendar.time = timeFormat.parse(selectedTime) ?: Date()
        } catch (e: Exception) {
            calendar.time = Date()
        }

        TimePickerDialog(
            this,
            { _, hourOfDay, minute ->
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                calendar.set(Calendar.MINUTE, minute)
                selectedTime = timeFormat.format(calendar.time)
                editTime.setText(selectedTime)
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        ).show()
    }

    private fun updateEvent() {
        val info = editInfo.text.toString().trim()

        if (info.isEmpty()) {
            Toast.makeText(this, "Введите описание события", Toast.LENGTH_SHORT).show()
            return
        }

        val event = Event(
            id = eventId,
            date = selectedDate,
            time = selectedTime,
            info = info
        )

        xmlManager.updateEvent(event)
        Toast.makeText(this, "Событие обновлено", Toast.LENGTH_SHORT).show()
        setResult(Activity.RESULT_OK)
        finish()
    }
}