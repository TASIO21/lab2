package com.anastasia.laba2

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CalendarView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var calendarView: CalendarView
    private lateinit var btnAdd: Button
    private lateinit var btnRemove: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var eventAdapter: EventAdapter
    private lateinit var xmlManager: XmlManager

    private var selectedDate: String = ""
    private val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        xmlManager = XmlManager(this)
        initViews()
        setupListeners()

        val calendar = Calendar.getInstance()
        selectedDate = dateFormat.format(calendar.time)
        loadEventsForDate(selectedDate)
    }

    private fun initViews() {
        calendarView = findViewById(R.id.calendarView)
        btnAdd = findViewById(R.id.btnAdd)
        btnRemove = findViewById(R.id.btnRemove)
        recyclerView = findViewById(R.id.recyclerViewEvents)

        recyclerView.layoutManager = LinearLayoutManager(this)
        eventAdapter = EventAdapter(mutableListOf()) { event ->
            openUpdateActivity(event)
        }
        recyclerView.adapter = eventAdapter
    }

    private fun setupListeners() {
        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val calendar = Calendar.getInstance()
            calendar.set(year, month, dayOfMonth)
            selectedDate = dateFormat.format(calendar.time)
            loadEventsForDate(selectedDate)
        }

        btnAdd.setOnClickListener {
            val intent = Intent(this, AddActivity::class.java)
            intent.putExtra("selected_date", selectedDate)
            startActivityForResult(intent, ADD_EVENT_REQUEST)
        }

        btnRemove.setOnClickListener {
            removeSelectedEvents()
        }
    }

    private fun loadEventsForDate(date: String) {
        val events = xmlManager.getEventsForDate(date)
        eventAdapter.updateEvents(events)
    }

    private fun openUpdateActivity(event: Event) {
        val intent = Intent(this, UpdateActivity::class.java)
        intent.putExtra("event_id", event.id)
        intent.putExtra("event_date", event.date)
        intent.putExtra("event_time", event.time)
        intent.putExtra("event_info", event.info)
        startActivityForResult(intent, UPDATE_EVENT_REQUEST)
    }

    private fun removeSelectedEvents() {
        val selectedEvents = eventAdapter.getSelectedEvents()
        if (selectedEvents.isEmpty()) {
            Toast.makeText(this, "Выберите события для удаления", Toast.LENGTH_SHORT).show()
            return
        }

        AlertDialog.Builder(this)
            .setTitle("Удаление событий")
            .setMessage("Удалить выбранные события (${selectedEvents.size})?")
            .setPositiveButton("Да") { _, _ ->
                selectedEvents.forEach { event ->
                    xmlManager.deleteEvent(event.id)
                }
                loadEventsForDate(selectedDate)
                Toast.makeText(this, "События удалены", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            loadEventsForDate(selectedDate)
        }
    }

    companion object {
        const val ADD_EVENT_REQUEST = 1
        const val UPDATE_EVENT_REQUEST = 2
    }
}