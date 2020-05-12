package ru.laink.city.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.Dispatchers
import ru.laink.city.firebase.FirebaseUserRepoImpl
import ru.laink.city.ui.viewmodels.UserViewModel

class UserViewModelFactory(
    private val firebaseUserRepoImpl: FirebaseUserRepoImpl
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return UserViewModel(
            firebaseUserRepoImpl,
            Dispatchers.Main
        ) as T
    }
}