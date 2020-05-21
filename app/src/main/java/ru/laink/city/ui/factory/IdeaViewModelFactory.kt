package ru.laink.city.ui.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.laink.city.firebase.IdeaRepository
import ru.laink.city.ui.viewmodels.IdeasViewModel
import kotlin.reflect.typeOf

class IdeaViewModelFactory(
    private val ideaRepository: IdeaRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return IdeasViewModel(ideaRepository) as T
    }
}