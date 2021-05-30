package com.sidiq.sibi.ui.leaderboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.sidiq.sibi.data.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LeaderBoardViewModel @Inject constructor(
    private val userRepository: UserRepository) : ViewModel() {

    val users = userRepository.getUsers().asLiveData()

}