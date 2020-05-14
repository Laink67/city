package ru.laink.city.map

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import ru.laink.city.R
import ru.laink.city.util.Constants.Companion.REQUEST_LOCATION_PERMISSION
import timber.log.Timber

class Map(private val activity: Activity) : OnMapReadyCallback {

    private lateinit var googleMap: GoogleMap

    override fun onMapReady(p0: GoogleMap) {
        googleMap = p0

        val latitude = 37.422160
        val longitude = -122.084270
        val homeLatLng = LatLng(latitude, longitude)
        val zoomLevel = 15f

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(homeLatLng, zoomLevel))
        googleMap.addMarker(MarkerOptions().position(homeLatLng))

        // Установка долгого нажатия для добавления нового маркера
        setMapLongClick()
        // Установка собственного стиля карты
        setMapStyle()
        // Разрешение на доступ к собственному местоположению
        enableMyLocation()
    }

    private fun isPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            activity.applicationContext,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun enableMyLocation() {
        if (isPermissionGranted()) {
            googleMap.isMyLocationEnabled = true
        } else {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_LOCATION_PERMISSION
            )
        }
    }

    private fun setMapLongClick() {
        googleMap.setOnMapLongClickListener { latLng ->
            googleMap.addMarker(
                MarkerOptions()
                    .position(latLng)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
            )
        }
    }

    private fun setMapStyle() {
        try {
            val success = googleMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                    activity.applicationContext,
                    R.raw.map_style
                )
            )

            if (!success) {
                Timber.d("Style parsing failed.")
            }
        } catch (e: Exception) {
            Timber.e("Can't find style. Error: $e")
        }
    }
}