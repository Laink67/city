package ru.laink.city.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "requests"
)
data class RequestRoom(
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null,
    val title: String,
    val date: String,
    val description: String,
    val location: String,
    val authorId: Int,
    val categoryId: Int
)