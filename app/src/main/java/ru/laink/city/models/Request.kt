package ru.laink.city.models

data class Request(
    val title: String,
    val date: String,
    val description: String,
    val location: String,
    val author: User?,
    val category: Category
)