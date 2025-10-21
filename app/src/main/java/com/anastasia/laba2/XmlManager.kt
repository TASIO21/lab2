package com.anastasia.laba2

import android.content.Context
import org.w3c.dom.Document
import org.w3c.dom.Element
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult

class XmlManager(private val context: Context) {
    private val fileName = "events.xml"
    private val file: File = File(context.filesDir, fileName)

    init {
        if (!file.exists()) {
            createEmptyXml()
        }
    }

    private fun createEmptyXml() {
        val docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
        val doc = docBuilder.newDocument()
        val rootElement = doc.createElement("events")
        doc.appendChild(rootElement)
        saveDocument(doc)
    }

    private fun loadDocument(): Document {
        val docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
        return if (file.exists() && file.length() > 0) {
            docBuilder.parse(file)
        } else {
            val doc = docBuilder.newDocument()
            val rootElement = doc.createElement("events")
            doc.appendChild(rootElement)
            doc
        }
    }

    private fun saveDocument(doc: Document) {
        val transformer = TransformerFactory.newInstance().newTransformer()
        transformer.setOutputProperty(javax.xml.transform.OutputKeys.INDENT, "yes")
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4")
        val source = DOMSource(doc)
        val result = StreamResult(file)
        transformer.transform(source, result)
    }

    fun addEvent(event: Event) {
        val doc = loadDocument()
        val root = doc.documentElement

        val eventElement = doc.createElement("event")
        eventElement.setAttribute("id", event.id)

        val dateElement = doc.createElement("date")
        dateElement.textContent = event.date
        eventElement.appendChild(dateElement)

        val timeElement = doc.createElement("time")
        timeElement.textContent = event.time
        eventElement.appendChild(timeElement)

        val infoElement = doc.createElement("info")
        infoElement.textContent = event.info
        eventElement.appendChild(infoElement)

        root.appendChild(eventElement)
        saveDocument(doc)
    }

    fun updateEvent(event: Event) {
        val doc = loadDocument()
        val root = doc.documentElement
        val eventNodes = root.getElementsByTagName("event")

        for (i in 0 until eventNodes.length) {
            val eventNode = eventNodes.item(i) as Element
            if (eventNode.getAttribute("id") == event.id) {
                eventNode.getElementsByTagName("date").item(0).textContent = event.date
                eventNode.getElementsByTagName("time").item(0).textContent = event.time
                eventNode.getElementsByTagName("info").item(0).textContent = event.info
                break
            }
        }

        saveDocument(doc)
    }

    fun deleteEvent(eventId: String) {
        val doc = loadDocument()
        val root = doc.documentElement
        val eventNodes = root.getElementsByTagName("event")

        for (i in 0 until eventNodes.length) {
            val eventNode = eventNodes.item(i) as Element
            if (eventNode.getAttribute("id") == eventId) {
                root.removeChild(eventNode)
                break
            }
        }

        saveDocument(doc)
    }

    fun getEventsForDate(date: String): List<Event> {
        val events = mutableListOf<Event>()
        val doc = loadDocument()
        val eventNodes = doc.getElementsByTagName("event")

        for (i in 0 until eventNodes.length) {
            val eventNode = eventNodes.item(i) as Element
            val eventDate = eventNode.getElementsByTagName("date").item(0).textContent

            if (eventDate == date) {
                val id = eventNode.getAttribute("id")
                val time = eventNode.getElementsByTagName("time").item(0).textContent
                val info = eventNode.getElementsByTagName("info").item(0).textContent

                events.add(Event(id, eventDate, time, info))
            }
        }

        return events.sortedBy { it.time }
    }
}