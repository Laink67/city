package ru.laink.city.ui.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.laink.city.firebase.FirebaseCategoryRepoImpl
import ru.laink.city.ui.viewmodels.CategoryViewModel

class CategoryViewModelFactory(
    private val firebaseCategoryRepoImpl: FirebaseCategoryRepoImpl
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return CategoryViewModel(
            firebaseCategoryRepoImpl/*,
            Dispatchers.Main*/
        ) as T
    }
}