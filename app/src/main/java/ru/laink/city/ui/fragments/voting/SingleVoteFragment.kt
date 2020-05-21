package ru.laink.city.ui.fragments.voting

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.single_vote_fragment.*
import ru.laink.city.R
import ru.laink.city.adapters.VoteAnswersAdapter
import ru.laink.city.firebase.VotingRepository
import ru.laink.city.models.vote.Vote
import ru.laink.city.models.vote.VoteAnswer
import ru.laink.city.ui.factory.SingleVoteViewModelFactory
import ru.laink.city.ui.viewmodels.SingleVoteViewModel
import ru.laink.city.util.Resource

class SingleVoteFragment : Fragment(R.layout.single_vote_fragment) {

    private val args: SingleVoteFragmentArgs by navArgs()
    private lateinit var voteAnswersAdapter: VoteAnswersAdapter
    private lateinit var singleVoteViewModel: SingleVoteViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val viewModelProviderFactory =
            SingleVoteViewModelFactory(VotingRepository())
        singleVoteViewModel =
            ViewModelProviders.of(this, viewModelProviderFactory).get(SingleVoteViewModel::class.java)

        val vote = args.vote
        setUpRecyclerView()
        updateInfo(vote)

        onVoteButtonClick(vote.id)
        onRefresh(vote.id)

        singleVoteViewModel.votingAnswer.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Resource.Loading -> {
                    vote_swipe_layout.isRefreshing = true
                }
                is Resource.Success -> {
                    vote_swipe_layout.isRefreshing = false
                    updateInfo(response.data!!)
                }
                is Resource.Error -> {
                    vote_swipe_layout.isRefreshing = false
                    Snackbar.make(view, response.message.toString(), 2000).show()
                }
            }
        })
    }

    private fun onRefresh(id: String) {
        vote_swipe_layout.setOnRefreshListener {
            singleVoteViewModel.getById(id)
        }
    }

    private fun updateInfo(vote: Vote) {
        val sumOfAnswers = vote.answer.values.sum()

        description_vote_text.text = vote.description
        title_vote_text.text = vote.title
        count_answers_text.text = getString(R.string.count_of_answers, sumOfAnswers)

        voteAnswersAdapter.update(getAnswers(vote.answer))
    }

    private fun onVoteButtonClick(id: String) {
        vote_button.setOnClickListener {
            val answer = voteAnswersAdapter.selectedText

            if (answer != null) {
                singleVoteViewModel.changeAnswer(id, answer)
                singleVoteViewModel.getById(id)
            } else {
                Snackbar.make(requireView(), "Сделайте выбор", 2000).show()
            }
        }
    }

    private fun getAnswers(map: Map<String, Int>): MutableList<VoteAnswer> {
        val list = mutableListOf<VoteAnswer>()

        for ((answer, count) in map) {
            list.add(VoteAnswer(answer, count))
        }

        return list
    }

    private fun setUpRecyclerView() {
        voteAnswersAdapter = VoteAnswersAdapter()

        vote_answers_recycler.apply {
            adapter = voteAnswersAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }
}