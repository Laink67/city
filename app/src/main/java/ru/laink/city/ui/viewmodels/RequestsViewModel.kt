package ru.laink.city.ui.viewmodels

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.laink.city.firebase.FirebaseRequestRepoImpl
import ru.laink.city.firebase.FirebaseUserRepoImpl
import ru.laink.city.models.Request
import ru.laink.city.models.RequestRoom
import ru.laink.city.util.Resource
import java.io.ByteArrayOutputStream
import java.lang.Exception

class RequestsViewModel(
    val requestRepository: FirebaseRequestRepoImpl
) : ViewModel() {

    private val _result: MutableLiveData<Resource<Unit, Exception>> = MutableLiveData()
    val result: LiveData<Resource<Unit, Exception>> = _result

    fun upsertRequest(request: Request, bitmap: Bitmap) =
        viewModelScope.launch {
            _result.postValue(Resource.Loading())
            _result.postValue(requestRepository.upsertRequestFirebase(request, bitmap))
        }

}