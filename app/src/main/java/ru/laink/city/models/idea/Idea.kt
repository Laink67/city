package ru.laink.city.models.idea

import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.laink.city.util.Constants
import ru.laink.city.util.Constants.Companion.STATUS_IN_DEVELOPING
import java.util.*

@Entity(
    tableName = "ideas"
)
data class Idea(
    @PrimaryKey(autoGenerate = true)
    val id: Long? = null,
    val title: String,
    val description: String,
    val status: Int,
    var userId:String? = null
) {
    constructor(title: String, description: String) : this(
        title = title,
        description = description,
        status = STATUS_IN_DEVELOPING
    )
}