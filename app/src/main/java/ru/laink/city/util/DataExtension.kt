package ru.laink.city.util

import android.net.Uri
import com.google.android.gms.maps.model.LatLng
import ru.laink.city.models.Request
import ru.laink.city.models.RequestFirebase
import ru.laink.city.util.Constants.Companion.SMOLENSK_LATITUDE
import ru.laink.city.util.Constants.Companion.SMOLENSK_LONGITUDE

internal fun firebaseRequestToRequest(
    id: String,
    requestFirebase: RequestFirebase,
    uri: Uri?
): Request =
    Request(
        id,
        requestFirebase.title ?: "",
        requestFirebase.date ?: "",
        requestFirebase.description ?: "",
        requestFirebase.latitude ?: SMOLENSK_LATITUDE,
        requestFirebase.longitude ?: SMOLENSK_LONGITUDE,
        requestFirebase.authorId ?: "",
        requestFirebase.categoryId ?: 0,
        uri?.toString()
    )
