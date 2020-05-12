package ru.laink

import android.app.Application
import androidx.work.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.laink.city.util.Constants.Companion.WORK_NAME
import ru.laink.city.work.RefreshDataWorker
import java.util.concurrent.TimeUnit

class CityApplication : Application() {

    private val applicationScope = CoroutineScope(Dispatchers.Default)

    override fun onCreate() {
        super.onCreate()

        delayedInit()
    }

    // Настройка повторяющеся фоновой работы
    private fun setupRecurringWork() {
/*
        // Ограничение сетевого типа
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.UNMETERED)
            .build()
*/
        // Периодический запрос один раз в час
        val repeatingRequest = PeriodicWorkRequestBuilder<RefreshDataWorker>(1, TimeUnit.HOURS)
//            .setConstraints(constraints) // Установка ограничений
            .build()

        // Этот метод позволяет добавить уникальное имя PeriodicWorkRequestв очередь,
        // где одновременно PeriodicWorkRequest может быть активным только одно из определенных имен
        WorkManager.getInstance().enqueueUniquePeriodicWork(
            WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,// Если существует ожидающая (незавершенная) работа с тем же именем,
            // ExistingPeriodicWorkPolicy.KEEP параметр заставляет WorkManagerсохранить предыдущую периодическую работу
            // и отклонить новый запрос на работу.
            repeatingRequest
        )
    }

    // Для запуска coroutine
    private fun delayedInit() {
        applicationScope.launch {
/*
            Timber.plant(Timber.DebugTree())
*/
            setupRecurringWork()
        }
    }
}