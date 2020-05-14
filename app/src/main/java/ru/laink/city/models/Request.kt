package ru.laink.city.models

import com.google.android.gms.maps.model.LatLng

data class Request(
    val title: String,
    val date: String,
    val description: String,
    val latLng: LatLng,
    val author: User?,
    val category: Category
)