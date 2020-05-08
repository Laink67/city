package ru.laink.city.firebase

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.laink.city.models.User
import ru.laink.city.util.Resource
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine


class FireBaseUserRepoImpl(val auth: FirebaseAuth = FirebaseAuth.getInstance()) {

    suspend fun signInGoogleUser(idToken: String): Resource<Unit, Exception> =
        withContext(Dispatchers.IO) {
            try {
                val credential = GoogleAuthProvider.getCredential(idToken, null)
                awaitTaskCompletable(auth.signInWithCredential(credential))
                Resource.build { Unit }
/*
                .addOnCompleteListener { task ->
                    resource = if (task.isSuccessful) {
                        Resource.build { Unit }
                    } else {
                        Resource.build { throw task.exception!! }
                        // Нужно сообщение с exception
                    }
                }
*/
//        }
/*
        return resource
*/
            } catch (exception: Exception) {
                Resource.build { throw exception }
            }
        }


    //Wraps Firebase/GMS calls
    internal suspend fun <T> awaitTaskCompletable(task: Task<T>): Unit =
        suspendCoroutine { continuation ->
            task.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    continuation.resume(Unit)
                } else {
                    continuation.resumeWithException(task.exception!!)
                }
            }
        }


    suspend fun getCurrentUser(): Resource<User?, Exception> {
        val firebaseUser = auth.currentUser

        return if (firebaseUser != null) {
            Resource.build { User(firebaseUser.uid, firebaseUser.displayName ?: "Null") }
        } else {
            Resource.build { null }
        }
    }

}