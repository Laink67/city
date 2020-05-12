package ru.laink.city.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.laink.city.repositories.RequestRepository
import ru.laink.city.ui.viewmodels.RequestsViewModel

class RequestViewModelProviderFactory(
    val requestRepository: RequestRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return RequestsViewModel(requestRepository) as T
    }
}