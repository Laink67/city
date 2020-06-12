package ru.laink.city.ui.fragments

import android.location.Geocoder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.own_requests_fragment.*
import ru.laink.city.R
import ru.laink.city.adapters.RequestsAdapter
import ru.laink.city.db.RequestDatabase
import ru.laink.city.firebase.FirebaseRequestRepoImpl
import ru.laink.city.ui.factory.RequestViewModelProviderFactory
import ru.laink.city.ui.viewmodels.RequestsViewModel

class OwnRequestsFragment : Fragment() {

    private lateinit var requestsViewModel: RequestsViewModel
    private lateinit var requestAdapter: RequestsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.own_requests_fragment, container, false)
    }

    override fun onStart() {
        super.onStart()
        swipe_own_request_layout.isRefreshing = true
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val db = RequestDatabase(requireContext())
        val firebaseRequestRepoImpl = FirebaseRequestRepoImpl(db)
        val viewModelProviderFactory =
            RequestViewModelProviderFactory(
                firebaseRequestRepoImpl,
                requireContext()
            )
        requestsViewModel =
            ViewModelProviders.of(this, viewModelProviderFactory).get(RequestsViewModel::class.java)

        val geocoder = Geocoder(requireContext())
        setColorLoading()

        // При свайпе вниз загрузка и получение заявок
        swipe_own_request_layout.setOnRefreshListener {
            requestsViewModel.getAndInsert()
        }

        setUpRecyclerView(geocoder)

        // Обновление заявок
        requestsViewModel.localOwnRequests.observe(viewLifecycleOwner, Observer { list ->
            requestAdapter.differ.submitList(list)
            swipe_own_request_layout.isRefreshing = false
        })
    }

    private fun setUpRecyclerView(geocoder: Geocoder) {
        requestAdapter = RequestsAdapter(geocoder)

        rv_own_requests.apply {
            adapter = requestAdapter
            layoutManager = LinearLayoutManager(activity)

            val itemTouchHelperCallback =
                object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
                    override fun onMove(
                        recyclerView: RecyclerView,
                        viewHolder: RecyclerView.ViewHolder,
                        target: RecyclerView.ViewHolder
                    ): Boolean {
                        return false
                    }

                    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                        val request = (adapter as RequestsAdapter).get(viewHolder)


                        if (request != null) {
                            // Удаление идеи
                            requestsViewModel.deleteRequest(request)

                            // Сообщение с возможностью отмены удаления
                            Snackbar.make(requireView(), "Request deleted", Snackbar.LENGTH_LONG)
                                .setAction("UNDO") {
                                    requestsViewModel.undoRequest(request)
                                }.show()
                        } else {
                            Snackbar.make(requireView(), "Can't find request", Snackbar.LENGTH_LONG)
                                .show()
                        }
                    }
                }

            val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
            itemTouchHelper.attachToRecyclerView(this)
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