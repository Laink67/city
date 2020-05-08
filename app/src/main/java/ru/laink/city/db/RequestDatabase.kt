package ru.laink.city.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import ru.laink.city.models.Request

@Database(
    entities = [Request::class],
    version = 1
)
abstract class RequestDatabase : RoomDatabase() {

    abstract fun getRequestDao(): RequestDao

    // Singletone
    companion object {
        // Volatile даёт возмодность другим потокам увидеть, когда данный поток меняет instance
        // Создаём экземпляр нашей бд, которая будет Singletone
        @Volatile
        private var instance: RequestDatabase? = null

        // Для синхронизирования настройки instance
        private val LOCK = Any()

        // Вызывается каждый раз, когда мы создаем экземпляр нашей БД
        // Возвращаем экземпляр, либо создаём экземпляр бд
        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: createDatabase(context).also { instance = it }
        }

        private fun createDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                RequestDatabase::class.java,
                "request_db.db"
            ).build()
    }
}