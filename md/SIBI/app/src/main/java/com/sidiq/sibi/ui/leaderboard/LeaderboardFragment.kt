package com.sidiq.sibi.ui.leaderboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.tabs.TabLayoutMediator
import com.sidiq.sibi.R
import com.sidiq.sibi.data.wrapper.Resource
import com.sidiq.sibi.databinding.FragmentLeaderboardBinding
import com.sidiq.sibi.ui.FirebaseAuthViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi


@AndroidEntryPoint
class LeaderboardFragment : Fragment() {
    private lateinit var binding: FragmentLeaderboardBinding
    private val authViewModel: FirebaseAuthViewModel by activityViewModels()
    private val leaderBoardViewModel: LeaderBoardViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLeaderboardBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    @ExperimentalCoroutinesApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (activity != null) {
            setupTab()
            setBoard()
        }
    }

    @ExperimentalCoroutinesApi
    private fun setBoard(){
        val user = authViewModel.checkUserLogin()!!

        leaderBoardViewModel.globalRank.observe(viewLifecycleOwner){ ranking ->
            when(ranking){
                is Resource.Success -> {
                    var rank = "0"
                    var i = 1
                    ranking.data.forEach {
                        if(user.userId == it.userId) rank = i.toString()
                        i++
                    }
                    binding.currentRank.text = rank
                }
                else -> {}
            }
        }
        leaderBoardViewModel.history(user.userId).observe(viewLifecycleOwner){ history ->
            when(history){
                is Resource.Success -> {
                    var max = 0L
                    history.data.forEach {
                        if(it.score!! > max) max = it.score!!
                    }
                    binding.bestRecord.text = max.toString()
                }
                else -> {}
            }
        }
    }

    private fun setupTab() {
        val tabAdapter = TabPageAdapter(this)
        binding.leaderboardViewpager.adapter = tabAdapter
        TabLayoutMediator(binding.leaderboardTab, binding.leaderboardViewpager) { tab, position ->
            when (position) {
                0 -> { tab.text = resources.getString(R.string.record) }
                1 -> { tab.text = resources.getString(R.string.global_rank) }
            }
        }.attach()
    }



}