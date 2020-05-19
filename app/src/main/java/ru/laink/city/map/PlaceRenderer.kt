package ru.laink.city.map

import android.content.Context
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.view.DefaultClusterRenderer
import ru.laink.city.R
import ru.laink.city.models.Request
import ru.laink.city.util.Constants

class PlaceRenderer(
    private val context: Context,
    map: GoogleMap?,
    clusterManager: ClusterManager<Request>
) : DefaultClusterRenderer<Request>(context, map, clusterManager) {

    // вызывается до отображения кластера на карте. Здесь вы можете предоставить настройки через MarkerOptions-
    // в этом случае он устанавливает заголовок, положение и значок маркера.
    override fun onBeforeClusterItemRendered(item: Request?, markerOptions: MarkerOptions) {
        val color =
            when (item?.type) {
                Constants.TYPE_IN_DEVELOPING -> {
                    BitmapDescriptorFactory.HUE_BLUE
                }
                Constants.TYPE_DONE -> {
                    BitmapDescriptorFactory.HUE_GREEN
                }
                Constants.TYPE_REJECTED -> {
                    BitmapDescriptorFactory.HUE_RED
                }
                else -> {
                    BitmapDescriptorFactory.HUE_ORANGE
                }
            }
        markerOptions.title(item?.title)
            .position(item!!.position)
            .icon(BitmapDescriptorFactory.defaultMarker(color))

    }

    //вызывается сразу после отображения маркера на карте. Здесь вы можете получить доступ к созданному Marker объекту -
    // в этом случае он устанавливает свойство тега маркера.
    override fun onClusterItemRendered(clusterItem: Request?, marker: Marker?) {
        marker?.tag = clusterItem
    }

    override fun getColor(clusterSize: Int): Int {
        return context.resources.getColor(R.color.colorPrimary, null)
    }
}