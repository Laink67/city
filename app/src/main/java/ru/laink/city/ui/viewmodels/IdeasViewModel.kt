package ru.laink.city.ui.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.laink.city.firebase.IdeaRepository
import ru.laink.city.util.Resource

class IdeasViewModel(
    private val ideaRepository: IdeaRepository
) : ViewModel() {

    val localIdeas = ideaRepository.localIdeas
    private val _ideaAnswer: MutableLiveData<Resource<Unit, Exception>> = MutableLiveData()
    val ideaAnswer = _ideaAnswer

//    fun addToFirebase(idea: Idea) = viewModelScope.launch {
//        _ideaAnswer.postValue(Resource.Loading())
//        _ideaAnswer.postValue(ideaRepository.addToFirebaseById(idea))
//    }
}