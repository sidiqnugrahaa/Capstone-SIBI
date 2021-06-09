package com.sidiq.sibi.ui.leaderboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.sidiq.sibi.data.LeaderboardRepository
import com.sidiq.sibi.data.wrapper.Resource
import com.sidiq.sibi.domain.model.History
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import javax.inject.Inject

@HiltViewModel
class LeaderBoardViewModel @Inject constructor(
    private val repository: LeaderboardRepository) : ViewModel() {

    @ExperimentalCoroutinesApi
    val globalRank = liveData(Dispatchers.IO) {
        emit(Resource.Loading())
        try{
            repository.getGlobalRank().collect {
                emit(it)
            }
        }catch (e: Exception){
            emit(Resource.Failure<Nothing>(e))
        }
    }

    @ExperimentalCoroutinesApi
    fun history(userId: String) : LiveData<Resource<List<History>>> = liveData(Dispatchers.IO) {
        emit(Resource.Loading())
        try{
            repository.getHistory(userId).collect {
                emit(it)
            }
        }catch (e: Exception){
            emit(Resource.Failure<Nothing>(e))
        }
    }

}