package ru.laink.city.models

import android.net.Uri
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.android.gms.maps.model.LatLng

@Entity(
    tableName = "requests"
)
data class Request(
    @PrimaryKey
    val id: String,
    val title: String,
    val date: String,
    val description: String,
    val latitude: Double,
    val longitude: Double,
    val authorId: String,
    val categoryId: Int,
    val uri: String?
)