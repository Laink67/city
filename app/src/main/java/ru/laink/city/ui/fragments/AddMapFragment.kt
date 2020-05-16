package ru.laink.city.ui.fragments

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.map_fragment.*
import ru.laink.city.R
import ru.laink.city.map.Map
import ru.laink.city.util.Constants.Companion.REQUEST_LOCATION_PERMISSION
import ru.laink.city.util.Constants.Companion.SMOLENSK_LATITUDE
import ru.laink.city.util.Constants.Companion.SMOLENSK_LONGITUDE
import ru.laink.city.util.Constants.Companion.ZOOM_LEVEL
import timber.log.Timber
import java.util.*

class AddMapFragment : BaseFragment(), OnMapReadyCallback {

    private lateinit var geocoder: Geocoder
    private lateinit var dialog: MaterialAlertDialogBuilder
    private lateinit var googleMap: GoogleMap
    private val args: AddMapFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.map_fragment, container, false)
    }

    override fun onStart() {
        super.onStart()
        hideProgressBar(map_progress)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.addMarkerOnMapQuestion))
            .setNegativeButton(getString(R.string.no)) { _, _ -> }

        geocoder = Geocoder(requireContext(), Locale.getDefault())

        val mapFragment =
            childFragmentManager.findFragmentById(R.id.map_fragment) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(p0: GoogleMap) {
        googleMap = p0
//        googleMap.clear()

        val homeLatLng = LatLng(SMOLENSK_LATITUDE, SMOLENSK_LONGITUDE)
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(homeLatLng, ZOOM_LEVEL))

        // Установка долгого нажатия для добавления нового маркера
        setMapLongClick()
        // Установка собственного стиля карты
        setMapStyle()
        // Разрешение на доступ к собственному местоположению
        enableMyLocation()
    }

    private fun isPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun enableMyLocation() {
        if (isPermissionGranted()) {
            googleMap.isMyLocationEnabled = true
        } else {
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_LOCATION_PERMISSION
            )
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_LOCATION_PERMISSION) {
            googleMap.isMyLocationEnabled = true
        }
    }

    private fun setMapLongClick() {
        googleMap.setOnMapLongClickListener { latLng ->
            dialog
                .setMessage(getAddress(latLng))
                .setPositiveButton(getString(R.string.yes)) { _, _ ->
//                    // Добавление маркера
//                    googleMap.addMarker(
//                        MarkerOptions()
//                            .position(latLng)
//                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
//                    )

                    showProgressBar(map_progress)

                    val bundle = Bundle().apply {
                        putParcelable("category", args.category)
                        putParcelable("latLng", latLng)
                    }
                    findNavController().navigate(
                        R.id.action_map_to_add_messaage_dest,
                        bundle
                    )
                }.show()
        }
    }


    private fun getAddress(latLng: LatLng): String {
        return try {
            val addressList = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
            addressList[0].getAddressLine(0)
        } catch (e: java.lang.Exception) {
            getString(R.string.get_address_error)
        }
    }

    private fun setMapStyle() {
        try {
            val success = googleMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                    requireContext(),
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


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.contains(PackageManager.PERMISSION_GRANTED)) {
                enableMyLocation()
            }
        }
    }

}