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

    private val _categoriesAnswer: MutableLiveData<Resource<List<Category>?, Exception>> =
        MutableLiveData()
    val categoriesAnswer: LiveData<Resource<List<Category>?, Exception>> = _categoriesAnswer
    val localCategories = firebaseCategoryRepoImpl.localCategories

//    init {
//        getCategories()
//    }

    private fun getCategories() = viewModelScope.launch {
        _categoriesAnswer.postValue(Resource.Loading())

        val categoriesResource = firebaseCategoryRepoImpl.getCategories()

        if (categoriesResource is Resource.Error) {
            _categoriesAnswer.postValue(categoriesResource)
        } else {
            _categoriesAnswer.postValue(Resource.Success(emptyList()))
//            firebaseCategoryRepoImpl.insertToDb(categoriesResource.data!!)
        }
    }

//    fun getLocalCategories(): LiveData<List<Category>> = viewModelScope.launch {
//        firebaseCategoryRepoImpl.getLocalCategory()
//    }

}