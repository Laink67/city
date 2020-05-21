package ru.laink.city.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.laink.city.firebase.VotingRepository
import ru.laink.city.models.vote.Vote
import ru.laink.city.util.Resource

class SingleVoteViewModel(
    private val votingRepository: VotingRepository
) : ViewModel() {
    private val _votingAnswer: MutableLiveData<Resource<Vote?, Exception>> =
        MutableLiveData()
    val votingAnswer: LiveData<Resource<Vote?, Exception>> = _votingAnswer

    fun getById(id: String) = viewModelScope.launch {
        _votingAnswer.postValue(Resource.Loading())
        _votingAnswer.postValue(votingRepository.getVoteById(id))
    }

    fun changeAnswer(id: String, answerTitle: String) = viewModelScope.launch {
        _votingAnswer.postValue(Resource.Loading())
        votingRepository.changeAnswer(id, answerTitle)
    }
}