package ru.laink.city.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.AuthResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import ru.laink.city.firebase.FireBaseUserRepoImpl
import ru.laink.city.models.LoginResult
import ru.laink.city.models.User
import ru.laink.city.util.Constants.Companion.GOOGLE_SIGN_IN
import ru.laink.city.util.Resource
import kotlin.coroutines.CoroutineContext

class UserViewModel(
    val fireBaseUserRepoImpl: FireBaseUserRepoImpl,
    val uiContext: CoroutineContext
) : ViewModel() {
    val answer: MutableLiveData<Resource<User?, Exception>> = MutableLiveData()

    fun onSignInResult(result: LoginResult) = CoroutineScope(uiContext).launch {
        answer.postValue(Resource.Loading())

        if (result.requestCode == GOOGLE_SIGN_IN && result.userToken != null) {

            val createGoogleUserResult = fireBaseUserRepoImpl.signInGoogleUser(
                result.userToken
            )

            if (createGoogleUserResult is Resource.Success) answer.postValue(getUser())
//            else showErrorState()
//        } else {
//            showErrorState()
        }
    }


    private suspend fun getUser(): Resource<User?, Exception> {
        return fireBaseUserRepoImpl.getCurrentUser()
    }
}

