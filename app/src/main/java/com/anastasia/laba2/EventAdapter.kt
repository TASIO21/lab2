package com.anastasia.laba2

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class EventAdapter(
    private var events: MutableList<Event>,
    private val onEventClick: (Event) -> Unit
) : RecyclerView.Adapter<EventAdapter.EventViewHolder>() {

    inner class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val checkBox: CheckBox = itemView.findViewById(R.id.checkBoxSelect)
        val textTime: TextView = itemView.findViewById(R.id.textTime)
        val textInfo: TextView = itemView.findViewById(R.id.textInfo)

        fun bind(event: Event) {
            checkBox.isChecked = event.isSelected
            textTime.text = event.time
            textInfo.text = event.info

            checkBox.setOnCheckedChangeListener { _, isChecked ->
                event.isSelected = isChecked
            }

            itemView.setOnClickListener {
                onEventClick(event)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_event, parent, false)
        return EventViewHolder(view)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        holder.bind(events[position])
    }

    override fun getItemCount(): Int = events.size

    fun updateEvents(newEvents: List<Event>) {
        events.clear()
        events.addAll(newEvents)
        notifyDataSetChanged()
    }

    fun getSelectedEvents(): List<Event> {
        return events.filter { it.isSelected }
    }
}