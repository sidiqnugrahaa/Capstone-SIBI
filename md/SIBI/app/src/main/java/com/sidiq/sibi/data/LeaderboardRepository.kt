package com.sidiq.sibi.data

import com.sidiq.sibi.data.source.LeaderboardService
import com.sidiq.sibi.data.wrapper.Resource
import com.sidiq.sibi.domain.model.AuthUser
import com.sidiq.sibi.domain.model.History
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LeaderboardRepository @Inject constructor(
    private val service: LeaderboardService) {

    fun getGlobalRank(): Flow<Resource<List<AuthUser>>> {
        return flow {
            emit(Resource.Loading())
            try {
                val results = service.getGlobalRank()
                emit(results)
            }catch (e: Exception){
                emit(Resource.Failure<Nothing>(e))
            }
        }.flowOn(Dispatchers.IO)
    }

    fun getHistory(userid: String) : Flow<Resource<List<History>>> {
        return flow {
            emit(Resource.Loading())
            try {
                val results = service.getHistory(userid)
                emit(results)
            }catch (e: Exception){
                emit(Resource.Failure<Nothing>(e))
            }
        }.flowOn(Dispatchers.IO)
    }

    @ExperimentalCoroutinesApi
    suspend fun insertHistory(userid: String, history: History) =
        service.insertHistory(userid, history)


}