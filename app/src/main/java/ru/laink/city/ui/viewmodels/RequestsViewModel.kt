package ru.laink.city.ui.viewmodels

import android.content.Context
import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.laink.city.firebase.FirebaseRequestRepoImpl
import ru.laink.city.models.request.Request
import ru.laink.city.models.request.RequestFirebase
import ru.laink.city.util.Resource
import ru.laink.city.ml.HelperClassifier

class RequestsViewModel(
    private val requestRepository: FirebaseRequestRepoImpl,
    context: Context
) : ViewModel() {

    private val helperClassifier = HelperClassifier(context)
    private val _resultUpsert: MutableLiveData<Resource<Unit, Exception>> = MutableLiveData()
    private val _resultRequests: MutableLiveData<Resource<List<Request>, Exception>> =
        MutableLiveData()

    val resultUpsert: LiveData<Resource<Unit, Exception>> = _resultUpsert
    val resultRequest: LiveData<Resource<List<Request>, Exception>> = _resultRequests
    val localOwnRequests = requestRepository.localOwnRequests

    fun upsertRequest(requestFirebase: RequestFirebase, bitmap: Bitmap) =
        viewModelScope.launch {
            _resultUpsert.postValue(Resource.Loading())

            // Проверка на нецензурную лексику с помощью ML
            requestFirebase.type = classifyMessage(requestFirebase.description!!)

            _resultUpsert.postValue(
                requestRepository.upsertRequestFirebase(
                    requestFirebase,
                    bitmap
                )
            )
        }

    fun getAllRequests() = viewModelScope.launch {
        _resultRequests.postValue(Resource.Loading())
        _resultRequests.postValue(requestRepository.getAllRequests())
    }

    fun getAndInsert() = viewModelScope.launch {
        _resultRequests.postValue(Resource.Loading())

        val requestResource = requestRepository.getUserRequests()

        if (requestResource is Resource.Error)
            _resultRequests.postValue(requestResource)
        else {
            _resultRequests.postValue(Resource.Success(emptyList()))
            insertToDb(requestResource.data!!)
        }
    }

    private suspend fun insertToDb(list: List<Request>) {
        requestRepository.insertToDb(list)
    }

    fun getByStatus(status: Int) = viewModelScope.launch {
        _resultRequests.postValue(Resource.Loading())
        _resultRequests.postValue(Resource.build { requestRepository.getByStatus(status) })
    }

    fun createInterpreter() {
        helperClassifier.createInterpreter()
    }

    private fun classifyMessage(message: String): Int {
        return helperClassifier.classifyMessage(message)
    }
}