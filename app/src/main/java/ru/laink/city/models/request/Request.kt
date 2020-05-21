package ru.laink.city.models.request

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem
import ru.laink.city.util.Constants.Companion.MARKER_DONE
import ru.laink.city.util.Constants.Companion.MARKER_IN_DEVELOPING
import ru.laink.city.util.Constants.Companion.MARKER_REJECTED
import ru.laink.city.util.Constants.Companion.STATUS_DONE
import ru.laink.city.util.Constants.Companion.STATUS_IN_DEVELOPING
import ru.laink.city.util.Constants.Companion.STATUS_REJECTED

@Entity(
    tableName = "requests"
)
data class Request(
    @PrimaryKey
    val id: String,
    val titleRequest: String,
    val date: String,
    val description: String,
    val latitude: Double,
    val longitude: Double,
    val authorId: String,
    val categoryId: Int,
    val uri: String?,
    val type: Int
) : ClusterItem {

    override fun getPosition(): LatLng = LatLng(latitude, longitude)

    override fun getTitle(): String = titleRequest

    override fun getSnippet(): String =
        when (type) {
            STATUS_DONE -> {
                MARKER_DONE
            }
            STATUS_REJECTED -> {
                MARKER_REJECTED
            }
            STATUS_IN_DEVELOPING -> {
                MARKER_IN_DEVELOPING
            }
            else -> {
                ""
            }
        }
}