package ru.laink.city.ui.viewmodels

import androidx.lifecycle.ViewModel
import ru.laink.city.repositories.RequestRepository

class RequestsViewModel(
    val requestRepository: RequestRepository
) : ViewModel() {
}