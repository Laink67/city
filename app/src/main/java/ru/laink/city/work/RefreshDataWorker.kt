package ru.laink.city.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import ru.laink.city.db.RequestDatabase
import ru.laink.city.firebase.FirebaseCategoryRepoImpl
import ru.laink.city.util.Resource
import timber.log.Timber

// В этом классе вы определяете фактическую задачу для выполнения в фоновом режиме
class RefreshDataWorker(appContext: Context, params: WorkerParameters) :
    CoroutineWorker(appContext, params) {

    // То, что будет выполняться в фоновом режиме
    override suspend fun doWork(): Result {
        val database = RequestDatabase(applicationContext)
        val repository = FirebaseCategoryRepoImpl(database)

        try {
            val resource = repository.getCategories()

            if(resource is Resource.Success){
                repository.insertToDb(resource.data!!)
            }

            Timber.d("Work request for sync is run")
        } catch (exception: Exception) {
            return Result.retry()
        }

        return Result.success()
    }

}