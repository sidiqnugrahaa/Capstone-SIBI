package com.sidiq.sibi.data.source.function

import com.sidiq.sibi.domain.model.Word
import retrofit2.http.GET

interface FunctionAPI {
    @GET("function-1")
    suspend fun getRandomWord() : Word
}