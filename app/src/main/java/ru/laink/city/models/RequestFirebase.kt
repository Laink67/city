package ru.laink.city.models

import ru.laink.city.util.Constants.Companion.SMOLENSK_LATITUDE
import ru.laink.city.util.Constants.Companion.SMOLENSK_LONGITUDE

data class RequestFirebase(
    val title: String? = "",
    val date: String? = "",
    val description: String? = "",
    val latitude: Double? = SMOLENSK_LATITUDE,
    val longitude: Double? = SMOLENSK_LONGITUDE,
    val authorId: String?,
    val categoryId: Int? = 0
) {
    constructor() : this("", "", "", SMOLENSK_LATITUDE, SMOLENSK_LONGITUDE, "", 0)
}