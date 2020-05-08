package ru.laink.city.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.laink.city.models.Category
import ru.laink.city.repositories.CategoriesRepository
import ru.laink.city.util.Resource

class CategoriesViewModel(
    val categoriesRepository: CategoriesRepository
) : ViewModel() {

//    val categoires: MutableLiveData<Resource<Category>> = MutableLiveData()
//    var page = 1
//
//    init {
//
//    }
//
//    fun getCategories() = viewModelScope.launch {
//        categoires.postValue(Resource.Loading())
//        val response = categoriesRepository.getAllCategories()
////        categoires.postValue()
//    }


}