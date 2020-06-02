package ru.laink.city.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.net.toUri
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.snackbar.Snackbar
import com.google.maps.android.clustering.ClusterManager
import kotlinx.android.synthetic.main.map_bottom_sheet.*
import kotlinx.android.synthetic.main.map_fragment.*
import ru.laink.city.R
import ru.laink.city.db.RequestDatabase
import ru.laink.city.firebase.FirebaseRequestRepoImpl
import ru.laink.city.map.PlaceRenderer
import ru.laink.city.models.request.Request
import ru.laink.city.ui.factory.RequestViewModelProviderFactory
import ru.laink.city.ui.fragments.addRequest.AddMapFragment
import ru.laink.city.ui.viewmodels.RequestsViewModel
import ru.laink.city.util.Constants.Companion.STATUS_ALL
import ru.laink.city.util.Constants.Companion.STATUS_DONE
import ru.laink.city.util.Constants.Companion.STATUS_IN_DEVELOPING
import ru.laink.city.util.Constants.Companion.STATUS_REJECTED
import ru.laink.city.util.Resource
import java.util.*

class MapFragment : AddMapFragment() {

    private lateinit var requestsViewModel: RequestsViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.map_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        constraint_markers.alpha = 1.0f

        val markerTypeImages =
            mapOf(
                STATUS_DONE to image_green,
                STATUS_IN_DEVELOPING to image_yellow,
                STATUS_REJECTED to image_red,
                STATUS_ALL to image_all
            )

        val db = RequestDatabase(requireContext())
        val firebaseRequestRepoImpl = FirebaseRequestRepoImpl(db)
        val viewModelProviderFactory =
            RequestViewModelProviderFactory(
                firebaseRequestRepoImpl,
                requireContext()
            )
        requestsViewModel =
            ViewModelProviders.of(this, viewModelProviderFactory).get(RequestsViewModel::class.java)

        requestsViewModel.getAllRequests()

        requestsViewModel.resultRequest.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Resource.Loading -> {
                    showProgressBar(map_progress)
                }
                is Resource.Success -> {
                    hideProgressBar(map_progress)
                    addClusteredMarkers(response.data!!)
                }
                is Resource.Error -> {
                    hideProgressBar(map_progress)
                    Snackbar.make(requireView(), response.message.toString(), 2000).show()
                }
            }
        })

        clickOnMarkerType(markerTypeImages)
    }

    private fun addClusteredMarkers(list: List<Request>) {
        // Create the ClusterManager class and set the custom renderer
        val clusterManager = ClusterManager<Request>(requireContext(), googleMap)
        clusterManager.renderer =
            PlaceRenderer(
                requireContext(),
                googleMap,
                clusterManager
            )

        // Add the places to the ClusterManager
        clusterManager.addItems(list)
        clusterManager.cluster()

        // Set ClusterManager as the OnCameraIdleListener so that it
        // can re-cluster when zooming in and out
        googleMap?.setOnCameraIdleListener {
            clusterManager.onCameraIdle()
        }

        clusterManager.setOnClusterItemClickListener { request ->
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED

            title_text_bottom.text = request.title
            date_text_bottom.text = request.date
            type_text_bottom.text =
                getString(R.string.statusIs, request.snippet.toUpperCase(Locale.ROOT))
            description_text_bottom.text = request.description
            address_text_bottom.text = getAddress(request.position)

            if (request.uri != null) {
                Glide.with(this)
                    .load(request.uri.toUri())
                    .into(image_bottom)
            } else {
                image_bottom.setImageResource(R.drawable.ic_camera)
            }

            true
        }
    }


    private fun clickOnMarkerType(map: Map<Int, ImageView>) {

        for ((status, image) in map) {
            image.setOnClickListener {
                setOpaque(it)
                setTranslucent(map.minus(status))

                googleMap?.clear()
                requestsViewModel.getByStatus(status)
            }
        }
    }

    // Очистка карты и сделать изображение непрозрачным
    private fun setOpaque(image: View) {
        image.alpha = 1.0F
    }

    // Сделать остальные картинки полупрозрачными
    private fun setTranslucent(images: Map<Int, ImageView>) {
        for ((_, image) in images) {
            image.alpha = 0.4f
        }
    }
}