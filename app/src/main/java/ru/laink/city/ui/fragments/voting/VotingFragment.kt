package ru.laink.city.ui.fragments.voting

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.voting_fragment.*
import ru.laink.city.R
import ru.laink.city.adapters.VotingAdapter
import ru.laink.city.firebase.VotingRepository
import ru.laink.city.ui.factory.VotingViewModelFactory
import ru.laink.city.ui.viewmodels.VotingViewModel
import ru.laink.city.util.Resource
import timber.log.Timber

class VotingFragment : Fragment(R.layout.voting_fragment) {

    private lateinit var votingViewModel: VotingViewModel
    private lateinit var votingAdapter: VotingAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewModelProviderFactory =
            VotingViewModelFactory(VotingRepository())
        votingViewModel =
            ViewModelProviders.of(this, viewModelProviderFactory).get(VotingViewModel::class.java)

        setUpRecyclerView()

        votingAdapter.setOnItemClickListener {
            val bundle = Bundle().apply {
                putParcelable("vote", it)
            }
            findNavController().navigate(
                R.id.action_voting_to_single_vote_dest,
                bundle
            )
        }

        swipe_voting.setOnRefreshListener {
            votingViewModel.getAllVoting()
        }

        votingViewModel.getAllVoting()

        votingViewModel.votingAnswer.observe(
            viewLifecycleOwner,
            Observer { response ->
                when (response) {
                    is Resource.Success -> {
                        swipe_voting.isRefreshing = false
                        votingAdapter.differ.submitList(response.data)
                    }
                    is Resource.Loading -> {
                        swipe_voting.isRefreshing = true
                    }
                    is Resource.Error -> {
                        swipe_voting.isRefreshing = false
                        Snackbar.make(requireView(), "Ошибка ${response.message}", 1000).show()
                        Timber.d("An error occured: ${response.message}")
                    }
                }
            })
    }

    private fun setUpRecyclerView() {
        votingAdapter = VotingAdapter()

        voting_recyclerview.apply {
            adapter = votingAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }

}