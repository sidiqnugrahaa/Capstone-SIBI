package com.sidiq.sibi.ui.leaderboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayoutMediator
import com.sidiq.sibi.R
import com.sidiq.sibi.databinding.FragmentLeaderboardBinding

class LeaderboardFragment : Fragment() {
    private lateinit var binding: FragmentLeaderboardBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLeaderboardBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (activity != null) {
            setupTab()
        }
    }

    private fun setupTab() {
        val tabAdapter = TabPageAdapter(this)
        binding.leaderboardViewpager.adapter = tabAdapter
        TabLayoutMediator(binding.leaderboardTab, binding.leaderboardViewpager) { tab, position ->
            when (position) {
                0 -> {
                    tab.text = resources.getString(R.string.record)
                }
                1 -> {
                    tab.text = resources.getString(R.string.global_rank)
                }
            }
        }.attach()
    }

}