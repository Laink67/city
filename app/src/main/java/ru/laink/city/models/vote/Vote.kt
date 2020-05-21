package ru.laink.city.models.vote

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Vote(
    val id: String,
    val title: String,
    val description: String,
    val answer: Map<String, Int>
) : Parcelable {
}