package com.sidiq.sibi.data.source

import com.google.firebase.firestore.FirebaseFirestore
import com.sidiq.sibi.data.source.function.FunctionAPI
import com.sidiq.sibi.data.wrapper.Resource
import com.sidiq.sibi.domain.model.Word
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FunctionService @Inject constructor(private val function: FunctionAPI) {

    suspend fun getRandomWord() : Flow<Resource<Word>> {
        return flow {
            emit(Resource.Loading())
            try {
                val response = function.getRandomWord()
                emit(Resource.Success(response))
            }catch (e: Exception){
                emit(Resource.Failure<Nothing>(e))
            }
        }.flowOn(Dispatchers.IO)
    }

}