package ru.laink.city.ui.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.laink.city.firebase.VotingRepository
import ru.laink.city.ui.viewmodels.SingleVoteViewModel

class SingleVoteViewModelFactory(
    private val votingRepository: VotingRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return SingleVoteViewModel(
            votingRepository
        ) as T
    }
}