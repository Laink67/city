package ru.laink.city.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import ru.laink.city.db.RequestDatabase
import ru.laink.city.firebase.FirebaseCategoryRepoImpl
import ru.laink.city.models.Category
import ru.laink.city.repositories.CategoryRepository
import ru.laink.city.util.Resource
import java.io.IOException
import java.lang.Exception
import kotlin.coroutines.CoroutineContext

class CategoryViewModel(
    private val firebaseCategoryRepoImpl: FirebaseCategoryRepoImpl,
/*
    private val categoryRepository: CategoryRepository,
*/
    private val uiContext: CoroutineContext
) : ViewModel() {

    val categoriesAnswer: MutableLiveData<Resource<List<Category>?, Exception>> = MutableLiveData()

    init {
        getCategories()
    }

    private fun getCategories() = viewModelScope.launch {
        categoriesAnswer.postValue(Resource.Loading())

        val categoriesResource = firebaseCategoryRepoImpl.getCategories()

        if (categoriesResource is Resource.Error) {
            categoriesAnswer.postValue(categoriesResource)
        }
        else{
            categoriesAnswer.postValue(Resource.Success(emptyList()))
        }
/*
        categoriesAnswer.postValue(*/
/*Resource.build { categoryRepository.categories.value }*//*

            firebaseCategoryRepoImpl.getCategories()
        )
*/
    }

    fun getLocalCategories(): LiveData<List<Category>> {
        getCategories()
        return firebaseCategoryRepoImpl.getLocalCategory()
    }
/*firebaseCategoryRepoImpl.getCategories()*//*
)
    }
*/

/*
    private fun refreshDataFromRepository() =
        */
/*CoroutineScope(uiContext)*//*
viewModelScope.launch {
            try {
                categoryRepository.refreshCategories()
            } catch (ioException: IOException) {
                categoriesAnswer.postValue(Resource.Error(ioException.toString()))
            }
        }
*/


}