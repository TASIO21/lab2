package com.anastasia.laba2

data class Event(
    val id: String,
    val date: String,
    val time: String,
    val info: String,
    var isSelected: Boolean = false
)