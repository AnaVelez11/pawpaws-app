package app.pawpaws.core.utils

sealed class RequestResult<out T> {
    object Idle : RequestResult<Nothing>()
    object Loading : RequestResult<Nothing>()
    data class Success<T>(val data: T) : RequestResult<T>()
    data class Error(val message: String) : RequestResult<Nothing>()
}
