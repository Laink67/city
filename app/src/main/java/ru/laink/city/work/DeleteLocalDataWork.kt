package ru.laink.city.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import ru.laink.city.db.RequestDatabase
import timber.log.Timber

class DeleteLocalDataWork(appContext: Context, params: WorkerParameters) :
    CoroutineWorker(appContext, params) {

    // То, что будет выполняться в фоновом режиме
    override suspend fun doWork(): Result {
        val database = RequestDatabase(applicationContext)

        try {
            database.clearAllTables()

            Timber.d("Work request to delete local data for sync is run")
        } catch (exception: Exception) {
            return Result.retry()
        }

        return Result.success()
    }
}