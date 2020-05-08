package ru.laink.city.util

import java.lang.Exception

// Обёртка для наших сетевых ответов
// Помогает различать successful and error responses,
// обрабатывать состояние загрузки
sealed class Resource<out T, out V>(
    // The body of response
    val data: T? = null,
    // The message of our response (for example, error message)
    val message: String? = null
) {
    class Success<T>(data: T) : Resource<T, Nothing>(data)
    class Error<V>(message: String) : Resource<Nothing, V>(null, message)
    class Loading<T> : Resource<T, Nothing>()

    companion object Factory{
        inline fun <T> build(function: () -> T): Resource<T,Exception> =
            try {
                Success(function.invoke())
            } catch (e: Exception) {
                Error(e.message!!)
            }
    }

}