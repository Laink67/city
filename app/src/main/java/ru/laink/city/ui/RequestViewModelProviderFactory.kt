package ru.laink.city.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.laink.city.firebase.FirebaseRequestRepoImpl
import ru.laink.city.ui.viewmodels.RequestsViewModel

class RequestViewModelProviderFactory(
    private val requestRepository: FirebaseRequestRepoImpl
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return RequestsViewModel(requestRepository) as T
    }
}