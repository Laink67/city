package ru.laink.city.models.vote

data class VoteFirebase(
    val title: String,
    val description: String,
    val answer: Map<String, Int>
) {
    constructor() : this("title", "description", emptyMap())
}