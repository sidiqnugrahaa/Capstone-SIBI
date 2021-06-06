package com.sidiq.sibi.ui.leaderboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.sidiq.sibi.data.LeaderboardRepository
import com.sidiq.sibi.data.UserRepository
import com.sidiq.sibi.data.wrapper.Resource
import com.sidiq.sibi.domain.model.History
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LeaderBoardViewModel @Inject constructor(
    private val repository: LeaderboardRepository) : ViewModel() {

    val globalRank = repository.getGlobalRank().asLiveData()

    fun history(userId: String) : LiveData<Resource<List<History>>> =
        repository.getHistory(userId).asLiveData()

}