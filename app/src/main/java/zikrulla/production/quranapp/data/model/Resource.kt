package zikrulla.production.quranapp.data.model

sealed class Resource<T> {
    class Loading<T> : Resource<T>()
    class Success<T : Any>(val data: T) : Resource<T>()
    class Error<T : Any>(val e: Throwable) : Resource<T>()
}