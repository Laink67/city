package ru.laink.city.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.net.toUri
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.snackbar.Snackbar
import com.google.maps.android.clustering.ClusterManager
import kotlinx.android.synthetic.main.map_bottom_sheet.*
import kotlinx.android.synthetic.main.map_fragment.*
import kotlinx.android.synthetic.main.request_item_preview.view.*
import ru.laink.city.R
import ru.laink.city.db.RequestDatabase
import ru.laink.city.firebase.FirebaseRequestRepoImpl
import ru.laink.city.map.PlaceRenderer
import ru.laink.city.models.Request
import ru.laink.city.ui.RequestViewModelProviderFactory
import ru.laink.city.ui.viewmodels.RequestsViewModel
import ru.laink.city.util.Constants.Companion.TYPE_DONE
import ru.laink.city.util.Constants.Companion.TYPE_IN_DEVELOPING
import ru.laink.city.util.Constants.Companion.TYPE_REJECTED
import ru.laink.city.util.Resource
import timber.log.Timber
import java.util.*

class MapFragment : AddMapFragment() {

    private lateinit var requestsViewModel: RequestsViewModel
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.map_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bottomSheetBehavior = BottomSheetBehavior.from(map_bottom_sheet)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        val db = RequestDatabase(requireContext())
        val firebaseRequestRepoImpl = FirebaseRequestRepoImpl(db)
        val viewModelProviderFactory = RequestViewModelProviderFactory(firebaseRequestRepoImpl)
        requestsViewModel =
            ViewModelProvider(this, viewModelProviderFactory).get(RequestsViewModel::class.java)

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
            type_text_bottom.text = getString(R.string.statusIs, request.snippet.toUpperCase(Locale.ROOT))
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

}