package ru.laink.city.models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Entity(
    tableName = "categories"
)
@Parcelize
data class Category(
    @PrimaryKey
    val id: Int,
    val title: String?
) : Parcelable