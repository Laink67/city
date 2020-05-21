package ru.laink.city.ui.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.laink.city.firebase.FirebaseUserRepoImpl
import ru.laink.city.ui.viewmodels.UserViewModel

class UserViewModelFactory(
    private val firebaseUserRepoImpl: FirebaseUserRepoImpl
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return UserViewModel(
            firebaseUserRepoImpl
        ) as T
    }
}