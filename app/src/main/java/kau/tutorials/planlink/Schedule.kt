package kau.tutorials.planlink

data class Schedule(
    val id: String,
    val title: String,
    val date: String,
    val startMinute: Int,
    val endMinute: Int
)
