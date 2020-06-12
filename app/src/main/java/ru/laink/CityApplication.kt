package ru.laink

import android.app.Application
import androidx.work.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.laink.city.util.Constants.Companion.WORK_CATEGORY
import ru.laink.city.util.Constants.Companion.WORK_DELETE
import ru.laink.city.util.Constants.Companion.WORK_REQUEST
import ru.laink.city.work.DeleteLocalDataWork
import ru.laink.city.work.RefreshCategoryWorker
import ru.laink.city.work.RefreshRequestsWorker
import timber.log.Timber
import java.util.concurrent.TimeUnit

class CityApplication : Application() {

    private val applicationScope = CoroutineScope(Dispatchers.Default)

    override fun onCreate() {
        super.onCreate()

        delayedInit()
    }

    // Настройка повторяющяяся фоновой работы
    private fun setupRecurringWork() {
        // Периодический запрос один раз в сутки
        val repeatingCategory = PeriodicWorkRequestBuilder<RefreshCategoryWorker>(1, TimeUnit.DAYS)
//            .setConstraints(constraints) // Установка ограничений
            .build()

        // Периодический запрос три раза в сутки
        val repeatingRequest = PeriodicWorkRequestBuilder<RefreshRequestsWorker>(3, TimeUnit.DAYS)
//            .setConstraints(constraints) // Установка ограничений
            .build()

        // Периодический запрос три раза в сутки
        val repeatingClear = PeriodicWorkRequestBuilder<DeleteLocalDataWork>(4, TimeUnit.DAYS)
//            .setConstraints(constraints) // Установка ограничений
            .build()

        WorkManager.getInstance().enqueueUniquePeriodicWork(
            WORK_DELETE,
            ExistingPeriodicWorkPolicy.KEEP,
            repeatingClear
        )

        WorkManager.getInstance().enqueueUniquePeriodicWork(
            WORK_REQUEST,
            ExistingPeriodicWorkPolicy.KEEP,
            repeatingRequest
        )

        // Этот метод позволяет добавить уникальное имя PeriodicWorkRequestв очередь,
        // где одновременно PeriodicWorkRequest может быть активным только одно из определенных имен
        WorkManager.getInstance().enqueueUniquePeriodicWork(
            WORK_CATEGORY,
            ExistingPeriodicWorkPolicy.KEEP,// Если существует ожидающая (незавершенная) работа с тем же именем,
            // ExistingPeriodicWorkPolicy.KEEP параметр заставляет WorkManager сохранить предыдущую периодическую работу
            // и отклонить новый запрос на работу.
            repeatingCategory
        )
    }

    // Для запуска coroutine
    private fun delayedInit() {
        applicationScope.launch {
            Timber.plant(Timber.DebugTree())
            setupRecurringWork()
        }
    }
}