package ru.laink.city.ui.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.laink.city.firebase.FirebaseUserRepoImpl
import ru.laink.city.models.LoginResult
import ru.laink.city.models.User
import ru.laink.city.util.Constants.Companion.GOOGLE_SIGN_IN
import ru.laink.city.util.Resource

class UserViewModel(
    private val firebaseUserRepoImpl: FirebaseUserRepoImpl
) : ViewModel() {
    val signInAnswer: MutableLiveData<Resource<User?, Exception>> =
        MutableLiveData()
    val signUpAnswer: MutableLiveData<Resource<User?, Exception>> =
        MutableLiveData()

    fun onSignInResultByGoogle(result: LoginResult) = viewModelScope.launch {
        // Отправить задачу начала работы progressBar
        signInAnswer.postValue(Resource.Loading())

        if (result.requestCode == GOOGLE_SIGN_IN && result.userToken != null) {

            // Вход через Google c возвратом ответа с успехом или ошибкой
            val createGoogleUserResult = firebaseUserRepoImpl.signInGoogleUser(
                result.userToken
            )

            // Если успешно, то отправить текущего пользователя в прослушиваемые данные
            if (createGoogleUserResult is Resource.Success) signInAnswer.postValue(getUser())
        } else {
            signInAnswer.postValue(Resource.Error("Error"))
        }
    }

    fun onSignInByEmailAndPasswordResult(email: String, password: String) =
        viewModelScope.launch {
            signInAnswer.postValue(Resource.Loading())

            if (email.isNotEmpty() && password.isNotEmpty()) {
                val sign = firebaseUserRepoImpl.signInByEmailAndPassword(
                    email,
                    password
                )
                // Если регистрация через email и password успешна, то отправить в прослушиваемые данные текущего пользователя
                if (sign is Resource.Success
                ) {
                    signInAnswer.postValue(getUser())
                } else {
                    // Иначе отправить ошибку с сообщение
                    signInAnswer.postValue(Resource.Error(sign.message!!))
                }
            } else {
                signInAnswer.postValue(Resource.Error("Пустые поля"))
            }
        }

    fun signUp(login: String, email: String, password: String) = viewModelScope.launch {
        signUpAnswer.postValue(Resource.Loading())

        if (email.isNotEmpty() && password.isNotEmpty() && login.isNotEmpty()) {

            firebaseUserRepoImpl.signUpUser(login, email, password)
            signUpAnswer.postValue(getUser())
        } else {
            signUpAnswer.postValue(Resource.Error("Ошибка"))
        }
    }

    fun signOut(): Resource<Unit, Exception> {
        return firebaseUserRepoImpl.signOutCurrentUser()
    }

    fun getUser(): Resource<User?, Exception> {
        return firebaseUserRepoImpl.getCurrentUser()
    }

    fun clearSignInAnswer() {
        signInAnswer.value = null
    }
}

