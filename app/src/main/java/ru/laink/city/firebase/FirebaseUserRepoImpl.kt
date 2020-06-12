package ru.laink.city.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import ru.laink.city.models.User
import ru.laink.city.util.Constants.Companion.NOT_VALID_DATA
import ru.laink.city.util.Resource

class FirebaseUserRepoImpl(private val auth: FirebaseAuth = FirebaseAuth.getInstance()) {

    suspend fun signInGoogleUser(idToken: String): Resource<Unit, Exception> =
        withContext(Dispatchers.IO) {
            try {
                // Получение учётных данных, которые сервер аутенфикации может использовать для аутенфикации пользователя
                val credential = GoogleAuthProvider.getCredential(idToken, null)
                // Ожтдание успешного ответа после аутенфикации пользователя с помощью credential
                auth.signInWithCredential(credential).await()
                Resource.build { Unit }
            } catch (exception: Exception) {
                Resource.build { throw exception }
            }
        }

    suspend fun signInByEmailAndPassword(
        email: String,
        password: String
    ): Resource<Unit, Exception> =
        withContext(Dispatchers.IO) {
            try {
                auth.signInWithEmailAndPassword(email, password).await()
                Resource.build { Unit }
            } catch (exception: Exception) {
                Resource.build { throw exception }
            }
        }

    suspend fun signUpUser(
        login: String,
        email: String,
        password: String
    ): Resource<Unit, Exception> =
        withContext(Dispatchers.IO) {
            try {
                // Создание нового пользователя c ожиданием успешного ответа
                auth.createUserWithEmailAndPassword(email, password).await()

                // Если текущий узер не пустой, то обновить его displayName
                auth.currentUser?.let { user ->
                    val profileUpdates = UserProfileChangeRequest.Builder()
                        .setDisplayName(login)
                        .build()

                    user.updateProfile(profileUpdates).await()
                }

                Resource.build { Unit }
            } catch (exception: Exception) {
                Resource.build { throw exception }
            }
        }

    fun signOutCurrentUser(): Resource<Unit, Exception> {
        return Resource.build {
            auth.signOut()
        }
    }

    fun getCurrentUser(): Resource<User?, Exception> {
        val firebaseUser = auth.currentUser

        return if (firebaseUser != null) {
            Resource.build { User(firebaseUser.uid, firebaseUser.displayName ?: "Null") }
        } else {
            Resource.Error(NOT_VALID_DATA)
        }
    }
}