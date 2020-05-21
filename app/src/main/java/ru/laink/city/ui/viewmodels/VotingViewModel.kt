package ru.laink.city.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.laink.city.firebase.VotingRepository
import ru.laink.city.models.vote.Vote
import ru.laink.city.util.Resource

class VotingViewModel(
    private val votingRepository: VotingRepository
) : ViewModel() {

    private val _votingAnswer: MutableLiveData<Resource<List<Vote>?, Exception>> =
        MutableLiveData()
    val votingAnswer: LiveData<Resource<List<Vote>?, Exception>> = _votingAnswer

    fun getAllVoting() = viewModelScope.launch {
        _votingAnswer.postValue(Resource.Loading())
        _votingAnswer.postValue(votingRepository.getAllVoting())
    }
}