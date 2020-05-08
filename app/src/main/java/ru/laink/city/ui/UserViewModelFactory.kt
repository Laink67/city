package ru.laink.city.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.Dispatchers
import ru.laink.city.firebase.FireBaseUserRepoImpl

class UserViewModelFactory(
    val fireBaseUserRepoImpl: FireBaseUserRepoImpl
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return UserViewModel(fireBaseUserRepoImpl, Dispatchers.Main) as T
    }
}