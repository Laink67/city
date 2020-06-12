package ru.laink.city.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import ru.laink.city.db.RequestDatabase
import ru.laink.city.firebase.FirebaseRequestRepoImpl
import ru.laink.city.util.Resource
import timber.log.Timber

class RefreshRequestsWorker(appContext: Context, params: WorkerParameters) :
    CoroutineWorker(appContext, params) {

    // То, что будет выполняться в фоновом режиме
    override suspend fun doWork(): Result {
        val database = RequestDatabase(applicationContext)
        val repository = FirebaseRequestRepoImpl(database)

        try {
            val resource = repository.getUserRequests()

            if (resource is Resource.Success) {
                repository.insertAllToDb(resource.data!!)
            }

            Timber.d("Work request for sync is run")
        } catch (exception: Exception) {
            return Result.retry()
        }

        return Result.success()
    }
}