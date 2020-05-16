package ru.laink.city.ui.fragments

import android.location.Geocoder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.categories_fragment.*
import kotlinx.android.synthetic.main.own_requests_fragment.*
import ru.laink.city.R
import ru.laink.city.adapters.CategoryAdapter
import ru.laink.city.adapters.RequestsAdapter
import ru.laink.city.db.RequestDatabase
import ru.laink.city.firebase.FirebaseRequestRepoImpl
import ru.laink.city.ui.RequestViewModelProviderFactory
import ru.laink.city.ui.viewmodels.CategoryViewModel
import ru.laink.city.ui.viewmodels.RequestsViewModel
import ru.laink.city.util.Resource

class OwnRequestsFragment : BaseFragment() {

    private lateinit var requestsViewModel: RequestsViewModel
    private lateinit var requestAdapter: RequestsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.own_requests_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val geocoder = Geocoder(requireContext())
        val db = RequestDatabase(requireContext())
        val firebaseRequestRepoImpl = FirebaseRequestRepoImpl(db)
        val viewModelProviderFactory = RequestViewModelProviderFactory(firebaseRequestRepoImpl)
        requestsViewModel =
            ViewModelProvider(this, viewModelProviderFactory).get(RequestsViewModel::class.java)

        setColorLoading()

        // При свайпе вниз загрузка и получение заявок
        swipe_own_request_layout.setOnRefreshListener {
            requestsViewModel.getAndInsert()
        }

        setUpRecyclerView(geocoder)

        // Обновление заявок
        requestsViewModel.localOwnRequests.observe(viewLifecycleOwner, Observer { list ->
            requestAdapter.differ.submitList(list)
        })

        observeLoadingAndErrors()
    }

    private fun observeLoadingAndErrors() {
        requestsViewModel.resultOwnRequest.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Resource.Loading -> {
                    swipe_own_request_layout.isRefreshing = true
                }
                is Resource.Success -> {
                    swipe_own_request_layout.isRefreshing = false
//                    requestAdapter.differ.submitList(response.data)
                }
                is Resource.Error -> {
                    swipe_own_request_layout.isRefreshing = false
                    Snackbar.make(requireView(), "Error: ${response.message}", 2000).show()
                }
            }
        })
    }

    private fun setUpRecyclerView(geocoder: Geocoder) {
        requestAdapter = RequestsAdapter(geocoder)

        rv_own_requests.apply {
            adapter = requestAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }

    private fun setColorLoading() {
        swipe_own_request_layout.setColorSchemeColors(
            resources.getColor(R.color.colorPrimary, null),
            resources.getColor(R.color.colorRed, null),
            resources.getColor(R.color.pastelPink, null),
            resources.getColor(R.color.colorPrimaryDark, null)
        )
    }


}