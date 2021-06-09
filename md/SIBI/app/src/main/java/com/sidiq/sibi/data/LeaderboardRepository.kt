package com.sidiq.sibi.data

import com.sidiq.sibi.data.source.LeaderboardService
import com.sidiq.sibi.data.wrapper.Resource
import com.sidiq.sibi.domain.model.AuthUser
import com.sidiq.sibi.domain.model.History
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LeaderboardRepository @Inject constructor(
    private val service: LeaderboardService) {

    @ExperimentalCoroutinesApi
    suspend fun getGlobalRank(): Flow<Resource<List<AuthUser>>> =
        service.getGlobalRank()

    @ExperimentalCoroutinesApi
    suspend fun getHistory(userid: String) : Flow<Resource<List<History>>> =
        service.getHistory(userid)

    @ExperimentalCoroutinesApi
    suspend fun insertHistory(userid: String, history: History) =
        service.insertHistory(userid, history)


}