package ru.laink.city.ui.viewmodels

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.laink.city.firebase.FirebaseRequestRepoImpl
import ru.laink.city.models.Request
import ru.laink.city.models.RequestFirebase
import ru.laink.city.util.Resource
import java.lang.Exception

class RequestsViewModel(
    private val requestRepository: FirebaseRequestRepoImpl
) : ViewModel() {

    private val _resultUpsert: MutableLiveData<Resource<Unit, Exception>> = MutableLiveData()
    private val _resultOwnRequests: MutableLiveData<Resource<List<Request>, Exception>> =
        MutableLiveData()

    val resultUpsert: LiveData<Resource<Unit, Exception>> = _resultUpsert
    val resultOwnRequest: LiveData<Resource<List<Request>, Exception>> = _resultOwnRequests
    val localOwnRequests = requestRepository.localOwnRequests

    fun upsertRequest(requestFirebase: RequestFirebase, bitmap: Bitmap) =
        viewModelScope.launch {
            _resultUpsert.postValue(Resource.Loading())
            _resultUpsert.postValue(
                requestRepository.upsertRequestFirebase(
                    requestFirebase,
                    bitmap
                )
            )
        }

    fun getOwnRequests() = viewModelScope.launch {
        _resultOwnRequests.postValue(Resource.Loading())

        val requestResource = requestRepository.getUserRequests()

        if (requestResource is Resource.Error)
            _resultOwnRequests.postValue(requestResource)
        else
            _resultOwnRequests.postValue(Resource.Success(emptyList()))
    }


    fun getAndInsert() = viewModelScope.launch {
        _resultOwnRequests.postValue(Resource.Loading())

        val requestResource = requestRepository.getUserRequests()

        if (requestResource is Resource.Error)
            _resultOwnRequests.postValue(requestResource)
        else {
            _resultOwnRequests.postValue(Resource.Success(emptyList()))
            insertToDb(requestResource.data!!)
        }
    }

    private suspend fun insertToDb(list: List<Request>) {
        requestRepository.insertToDb(list)
    }
}