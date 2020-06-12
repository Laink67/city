package ru.laink.city.ui.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import ru.laink.city.firebase.IdeaRepository
import ru.laink.city.models.idea.Idea
import ru.laink.city.util.Resource

class IdeasViewModel(
    private val ideaRepository: IdeaRepository
) : ViewModel() {

    val localIdeas = ideaRepository.localIdeas
    private val _ideaAnswer: MutableLiveData<Resource<Unit, Exception>> = MutableLiveData()
    val ideaAnswer = _ideaAnswer

    fun getAll() = viewModelScope.launch {
        _ideaAnswer.postValue(Resource.Loading())
        _ideaAnswer.postValue(ideaRepository.getAll())
    }

    fun removeItem(idea: Idea) = viewModelScope.launch {
        _ideaAnswer.postValue(Resource.Loading())
        _ideaAnswer.postValue(ideaRepository.delete(idea))
    }

    fun insert(idea: Idea) = viewModelScope.launch {
        _ideaAnswer.postValue(Resource.Loading())
        _ideaAnswer.postValue(ideaRepository.addToFirebaseById(idea))
    }

}