package ru.laink.city.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.laink.city.firebase.FirebaseCategoryRepoImpl
import ru.laink.city.models.Category
import ru.laink.city.util.Resource

class CategoryViewModel(
    private val firebaseCategoryRepoImpl: FirebaseCategoryRepoImpl
) : ViewModel() {

    private val _categoriesAnswer: MutableLiveData<Resource<Unit, Exception>> =
        MutableLiveData()
    val categoriesAnswer: LiveData<Resource<Unit, Exception>> = _categoriesAnswer
    val localCategories = firebaseCategoryRepoImpl.localCategories

    fun getCategories() = viewModelScope.launch {
        _categoriesAnswer.postValue(Resource.Loading())

        val categoriesResource = firebaseCategoryRepoImpl.getCategories()

        if (categoriesResource is Resource.Error) {
            _categoriesAnswer.postValue(categoriesResource)
        } else {
            _categoriesAnswer.postValue(Resource.Success(Unit))
            firebaseCategoryRepoImpl.insertToDb(categoriesResource.data!!)
        }
    }
}