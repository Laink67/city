package ru.laink.city.models.request

import ru.laink.city.util.Constants.Companion.SMOLENSK_LATITUDE
import ru.laink.city.util.Constants.Companion.SMOLENSK_LONGITUDE
import ru.laink.city.util.Constants.Companion.STATUS_IN_DEVELOPING

data class RequestFirebase(
    val title: String? = "",
    val date: String? = "",
    val description: String? = "",
    val latitude: Double? = SMOLENSK_LATITUDE,
    val longitude: Double? = SMOLENSK_LONGITUDE,
    val authorId: String?,
    val categoryId: Int? = 0,
    var type: Int? = STATUS_IN_DEVELOPING   // -1 - отклонено, 0 - в разработке, 1 - сделано
) {
    constructor() : this("", "", "", SMOLENSK_LATITUDE, SMOLENSK_LONGITUDE, "", 0, STATUS_IN_DEVELOPING)
}