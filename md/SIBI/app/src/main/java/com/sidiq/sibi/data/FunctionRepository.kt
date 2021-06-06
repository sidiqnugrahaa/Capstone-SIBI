package com.sidiq.sibi.data

import com.sidiq.sibi.data.source.FunctionService
import com.sidiq.sibi.data.wrapper.Resource
import com.sidiq.sibi.domain.model.Word
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FunctionRepository @Inject constructor(private val service: FunctionService) {

    suspend fun getRandomWord() : Flow<Resource<Word>> =
        service.getRandomWord()

}