package ru.laink.city.ui.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.laink.city.firebase.IdeaRepository
import ru.laink.city.models.idea.Idea
import ru.laink.city.util.Resource
import java.lang.Exception

class SingleIdeaViewModel(
    private val ideaRepository: IdeaRepository
) : ViewModel() {

    private val _ideaAnswer: MutableLiveData<Resource<Unit, Exception>> = MutableLiveData()
    val ideaAnswer = _ideaAnswer

    fun insert(idea: Idea) = viewModelScope.launch {
        _ideaAnswer.postValue(Resource.Loading())
        _ideaAnswer.postValue(ideaRepository.addToFirebaseById(idea))
    }

}