package com.sidiq.sibi.data.wrapper

sealed class Result<out T>
{
    data class Success<out T>(val data: T) : Result<T>()
    data class Error<out T>(val exception: Exception) : Result<T>()
    data class Canceled<out T>(val exception: Exception?) : Result<T>()

    // string method to display a result for debugging
    override fun toString(): String {
        return when (this) {
            is Success<*> -> "Success[data=$data]"
            is Error -> "Error[exception=$exception]"
            is Canceled -> "Canceled[exception=$exception]"
        }
    }
}